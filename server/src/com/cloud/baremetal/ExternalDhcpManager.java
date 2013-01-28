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
package com.cloud.baremetal;

import com.cloud.baremetal.ExternalDhcpEntryListener.DhcpEntryState;
import com.cloud.deploy.DeployDestination;
import com.cloud.exception.ResourceUnavailableException;
import com.cloud.host.Host;
import com.cloud.network.Network;
import com.cloud.uservm.UserVm;
import com.cloud.utils.component.Manager;
import com.cloud.vm.NicProfile;
import com.cloud.vm.ReservationContext;
import com.cloud.vm.VirtualMachine;
import com.cloud.vm.VirtualMachineProfile;

public interface ExternalDhcpManager extends Manager {
	public static class DhcpServerType {
		private String _name;
		
		public static final DhcpServerType Dnsmasq = new DhcpServerType("Dnsmasq");
		public static final DhcpServerType Dhcpd = new DhcpServerType("Dhcpd");
		
		public DhcpServerType(String name) {
			_name = name;
		}
		
		public String getName() {
			return _name;
		}
		
	}
	
	
	DhcpServerResponse getApiResponse(Host dhcpServer);
	
	boolean addVirtualMachineIntoNetwork(Network network, NicProfile nic, VirtualMachineProfile<? extends VirtualMachine> profile, DeployDestination dest, ReservationContext context) throws ResourceUnavailableException;
	
	Host addDhcpServer(Long zoneId, Long podId, String type, String url, String username, String password);
}
