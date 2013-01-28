// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the 
// specific language governing permissions and limitations
// under the License.
package com.cloud.agent.manager.allocator.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import org.apache.log4j.Logger;

import com.cloud.agent.manager.allocator.HostAllocator;
import com.cloud.deploy.DeploymentPlan;
import com.cloud.deploy.DeploymentPlanner.ExcludeList;
import com.cloud.host.Host;
import com.cloud.host.Host.Type;
import com.cloud.host.HostVO;
import com.cloud.host.dao.HostDao;
import com.cloud.offering.ServiceOffering;
import com.cloud.resource.ResourceManager;
import com.cloud.utils.component.ComponentLocator;
import com.cloud.vm.VirtualMachine;
import com.cloud.vm.VirtualMachineProfile;

@Local(value=HostAllocator.class)
public class RandomAllocator implements HostAllocator {
    private static final Logger s_logger = Logger.getLogger(RandomAllocator.class);
    private String _name;
    private HostDao _hostDao;
    private ResourceManager _resourceMgr;

    @Override
    public List<Host> allocateTo(VirtualMachineProfile<? extends VirtualMachine> vmProfile, DeploymentPlan plan, Type type,
            ExcludeList avoid, int returnUpTo) {
        return allocateTo(vmProfile, plan, type, avoid, returnUpTo, true);
    }
    
    @Override
    public List<Host> allocateTo(VirtualMachineProfile<? extends VirtualMachine> vmProfile, DeploymentPlan plan, Type type,
			ExcludeList avoid, int returnUpTo, boolean considerReservedCapacity) {

		long dcId = plan.getDataCenterId();
		Long podId = plan.getPodId();
		Long clusterId = plan.getClusterId();
		ServiceOffering offering = vmProfile.getServiceOffering();
    	
    	List<Host> suitableHosts = new ArrayList<Host>();
    	
        if (type == Host.Type.Storage) {
            return suitableHosts;
        }

        String hostTag = offering.getHostTag();
        if(hostTag != null){
        	s_logger.debug("Looking for hosts in dc: " + dcId + "  pod:" + podId + "  cluster:" + clusterId + " having host tag:" + hostTag);
        }else{
        	s_logger.debug("Looking for hosts in dc: " + dcId + "  pod:" + podId + "  cluster:" + clusterId);
        }

        // list all computing hosts, regardless of whether they support routing...it's random after all
        List<? extends Host> hosts = new ArrayList<HostVO>();
        if(hostTag != null){
        	hosts = _hostDao.listByHostTag(type, clusterId, podId, dcId, hostTag);
        }else{
        	hosts = _resourceMgr.listAllUpAndEnabledHosts(type, clusterId, podId, dcId);
        }
        
        s_logger.debug("Random Allocator found " + hosts.size() + "  hosts");
        
        if (hosts.size() == 0) {
            return suitableHosts;
        }


        Collections.shuffle(hosts);
        for (Host host : hosts) {
        	if(suitableHosts.size() == returnUpTo){
        		break;
        	}
        	
            if (!avoid.shouldAvoid(host)) {
            	suitableHosts.add(host);
            }else{
                if (s_logger.isDebugEnabled()) {
                    s_logger.debug("Host name: " + host.getName() + ", hostId: "+ host.getId() +" is in avoid set, skipping this and trying other available hosts");
                }
            }
        }
        if (s_logger.isDebugEnabled()) {
            s_logger.debug("Random Host Allocator returning "+suitableHosts.size() +" suitable hosts");
        }
        return suitableHosts;
    }

    @Override
    public boolean isVirtualMachineUpgradable(VirtualMachine vm, ServiceOffering offering) {
        // currently we do no special checks to rule out a VM being upgradable to an offering, so
        // return true
        return true;
    }

    @Override
    public boolean configure(String name, Map<String, Object> params) {
        ComponentLocator locator = ComponentLocator.getCurrentLocator();
        _hostDao = locator.getDao(HostDao.class);
        _resourceMgr = locator.getManager(ResourceManager.class);
        if (_hostDao == null) {
            s_logger.error("Unable to get host dao.");
            return false;
        }
        _name=name;
        
        return true;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
}
