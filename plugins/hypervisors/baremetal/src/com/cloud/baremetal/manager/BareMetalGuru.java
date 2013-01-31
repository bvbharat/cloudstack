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
// 
// Automatically generated by addcopyright.py at 01/29/2013
// Apache License, Version 2.0 (the "License"); you may not use this
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// 
// Automatically generated by addcopyright.py at 04/03/2012
package com.cloud.baremetal.manager;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Local;

import org.apache.log4j.Logger;

import com.cloud.agent.api.to.VirtualMachineTO;
import com.cloud.host.dao.HostDao;
import com.cloud.hypervisor.Hypervisor.HypervisorType;
import com.cloud.hypervisor.HypervisorGuru;
import com.cloud.hypervisor.HypervisorGuruBase;
import com.cloud.storage.GuestOSVO;
import com.cloud.storage.dao.GuestOSDao;
import com.cloud.utils.component.Inject;
import com.cloud.vm.VMInstanceVO;
import com.cloud.vm.VirtualMachine;
import com.cloud.vm.VirtualMachineProfile;
import com.cloud.vm.dao.VMInstanceDao;

@Local(value=HypervisorGuru.class)
public class BareMetalGuru extends HypervisorGuruBase implements HypervisorGuru {
	private static final Logger s_logger = Logger.getLogger(BareMetalGuru.class);
	@Inject GuestOSDao _guestOsDao;
	@Inject HostDao _hostDao;
	@Inject VMInstanceDao _vmDao;
	
	protected BareMetalGuru() {
		super();
	}
	
	@Override
	public HypervisorType getHypervisorType() {
		return HypervisorType.BareMetal;
	}

	@Override
	public <T extends VirtualMachine> VirtualMachineTO implement(VirtualMachineProfile<T> vm) {
		VirtualMachineTO to = toVirtualMachineTO(vm);

		VMInstanceVO vo = _vmDao.findById(vm.getId());
        if (vo.getLastHostId() == null) {
            to.setBootArgs(BaremetalManager.DO_PXE);
        }
        
        Map<String, String> details = new HashMap<String, String>();
        details.put("template", vm.getTemplate().getUrl());
        to.setDetails(details);
        
		// Determine the VM's OS description
		GuestOSVO guestOS = _guestOsDao.findById(vm.getVirtualMachine().getGuestOSId());
		to.setOs(guestOS.getDisplayName());

		return to;
	}
	
	@Override
    public boolean trackVmHostChange() {
    	return false;
    }
}

	
