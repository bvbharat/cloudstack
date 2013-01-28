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
package com.cloud.hypervisor.guru;

import javax.ejb.Local;

import com.cloud.agent.api.Command;
import com.cloud.agent.api.to.VirtualMachineTO;
import com.cloud.host.dao.HostDetailsDao;
import com.cloud.host.dao.HostDao;
import com.cloud.hypervisor.HypervisorGuru;
import com.cloud.hypervisor.HypervisorGuruBase;
import com.cloud.hypervisor.Hypervisor.HypervisorType;
import com.cloud.storage.GuestOSVO;
import com.cloud.storage.dao.GuestOSDao;
import com.cloud.template.VirtualMachineTemplate.BootloaderType;
import com.cloud.utils.component.Inject;
import com.cloud.vm.VirtualMachine;
import com.cloud.vm.VirtualMachineProfile;

/**
 * Implementation of Hypervisor guru for Hyper-Vr
 **/

@Local(value=HypervisorGuru.class)
public class HypervGuru extends HypervisorGuruBase implements HypervisorGuru {

    @Inject GuestOSDao _guestOsDao;
    @Inject HostDao _hostDao;

    protected HypervGuru() {
    	super();
    }
    
    @Override
    public HypervisorType getHypervisorType() {
        return HypervisorType.Hyperv;
    }

    @Override
    public <T extends VirtualMachine> VirtualMachineTO implement(VirtualMachineProfile<T> vm) {
        VirtualMachineTO to = toVirtualMachineTO(vm);
        to.setBootloader(BootloaderType.HVM);

        // Determine the VM's OS description
        GuestOSVO guestOS = _guestOsDao.findById(vm.getVirtualMachine().getGuestOSId());
        to.setOs(guestOS.getDisplayName());
        return to;
    }
    
    @Override
    public long getCommandHostDelegation(long hostId, Command cmd) {
    	return hostId;
    }   
    
    @Override
    public boolean trackVmHostChange() {
    	return false;
    }
}
