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
package com.cloud.vm;

import java.util.List;

import com.cloud.event.EventTypes;
import com.cloud.event.UsageEventVO;
import com.cloud.event.dao.UsageEventDao;
import com.cloud.network.NetworkVO;
import com.cloud.network.dao.NetworkDao;
import com.cloud.utils.fsm.StateListener;
import com.cloud.vm.VirtualMachine.Event;
import com.cloud.vm.VirtualMachine.State;
import com.cloud.vm.dao.NicDao;

public class UserVmStateListener implements StateListener<State, VirtualMachine.Event, VirtualMachine> {

    protected UsageEventDao _usageEventDao;
    protected NetworkDao _networkDao;
    protected NicDao _nicDao;
    
    public UserVmStateListener(UsageEventDao usageEventDao, NetworkDao networkDao, NicDao nicDao) {
        this._usageEventDao = usageEventDao;
        this._networkDao = networkDao;
        this._nicDao = nicDao;
    }
    
    @Override
    public boolean preStateTransitionEvent(State oldState, Event event, State newState, VirtualMachine vo, boolean status, Object opaque) {
        return true;
    }

    @Override
    public boolean postStateTransitionEvent(State oldState, Event event, State newState, VirtualMachine vo, boolean status, Object opaque) {
        if(!status){
            return false;
        }
        
        if(vo.getType() != VirtualMachine.Type.User){
            return true;
        }
        
        if (VirtualMachine.State.isVmCreated(oldState, event, newState)) {
            UsageEventVO usageEvent = new UsageEventVO(EventTypes.EVENT_VM_CREATE, vo.getAccountId(), vo.getDataCenterIdToDeployIn(), vo.getId(), vo.getHostName(), vo.getServiceOfferingId(), 
                    vo.getTemplateId(), vo.getHypervisorType().toString());
            _usageEventDao.persist(usageEvent);
        } else if (VirtualMachine.State.isVmStarted(oldState, event, newState)) {
            UsageEventVO usageEvent = new UsageEventVO(EventTypes.EVENT_VM_START, vo.getAccountId(), vo.getDataCenterIdToDeployIn(), vo.getId(), vo.getHostName(), vo.getServiceOfferingId(), 
                    vo.getTemplateId(), vo.getHypervisorType().toString());
            _usageEventDao.persist(usageEvent);
        } else if (VirtualMachine.State.isVmStopped(oldState, event, newState)) {
            UsageEventVO usageEvent = new UsageEventVO(EventTypes.EVENT_VM_STOP, vo.getAccountId(), vo.getDataCenterIdToDeployIn(), vo.getId(), vo.getHostName());
            _usageEventDao.persist(usageEvent);
            List<NicVO> nics = _nicDao.listByVmId(vo.getId());
            for (NicVO nic : nics) {
                NetworkVO network = _networkDao.findById(nic.getNetworkId());
                usageEvent = new UsageEventVO(EventTypes.EVENT_NETWORK_OFFERING_REMOVE, vo.getAccountId(), vo.getDataCenterIdToDeployIn(), vo.getId(), null, network.getNetworkOfferingId(), null, 0L);
                _usageEventDao.persist(usageEvent);
            }
        } else if (VirtualMachine.State.isVmDestroyed(oldState, event, newState)) {
            UsageEventVO usageEvent = new UsageEventVO(EventTypes.EVENT_VM_DESTROY, vo.getAccountId(), vo.getDataCenterIdToDeployIn(), vo.getId(), vo.getHostName(), vo.getServiceOfferingId(), 
                    vo.getTemplateId(), vo.getHypervisorType().toString());
            _usageEventDao.persist(usageEvent);
        } 
        return true;
    }
}
