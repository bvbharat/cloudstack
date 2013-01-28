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
package com.cloud.network.element;

import java.util.Collections;

import javax.naming.ConfigurationException;

import org.junit.Before;
import org.junit.Test;

import com.cloud.deploy.DeployDestination;
import com.cloud.domain.Domain;
import com.cloud.exception.ConcurrentOperationException;
import com.cloud.exception.InsufficientCapacityException;
import com.cloud.exception.ResourceUnavailableException;
import com.cloud.network.Network;
import com.cloud.network.Network.GuestType;
import com.cloud.network.Network.Provider;
import com.cloud.network.Network.Service;
import com.cloud.network.NetworkManager;
import com.cloud.network.NetworkModel;
import com.cloud.network.Networks.BroadcastDomainType;
import com.cloud.network.Networks.TrafficType;
import com.cloud.network.dao.NetworkServiceMapDao;
import com.cloud.offering.NetworkOffering;
import com.cloud.resource.ResourceManager;
import com.cloud.user.Account;
import com.cloud.vm.ReservationContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NiciraNvpElementTest {
	
	NiciraNvpElement _element = new NiciraNvpElement();
	NetworkManager _networkManager = mock(NetworkManager.class);
	NetworkModel _networkModel = mock(NetworkModel.class);
	NetworkServiceMapDao _ntwkSrvcDao = mock (NetworkServiceMapDao.class);
	
	@Before
	public void setUp() throws ConfigurationException {
		_element._resourceMgr = mock(ResourceManager.class);
		_element._networkManager = _networkManager;
		_element._ntwkSrvcDao = _ntwkSrvcDao;
		_element._networkModel = _networkModel;
		
		// Standard responses
		when(_networkModel.isProviderForNetwork(Provider.NiciraNvp, 42L)).thenReturn(true);
		
		_element.configure("NiciraNvpTestElement", Collections.<String, Object> emptyMap());
	}

	@Test
	public void canHandleTest() {
		Network net = mock(Network.class);
		when(net.getBroadcastDomainType()).thenReturn(BroadcastDomainType.Lswitch);
		when(net.getId()).thenReturn(42L);
		
		when(_ntwkSrvcDao.canProviderSupportServiceInNetwork(42L, Service.Connectivity, Provider.NiciraNvp)).thenReturn(true);	
		//  Golden path
		assertTrue(_element.canHandle(net, Service.Connectivity));
		
		when(net.getBroadcastDomainType()).thenReturn(BroadcastDomainType.Vlan);
		// Only broadcastdomaintype lswitch is supported
		assertFalse(_element.canHandle(net, Service.Connectivity));
		
		when(net.getBroadcastDomainType()).thenReturn(BroadcastDomainType.Lswitch);
		when(_ntwkSrvcDao.canProviderSupportServiceInNetwork(42L, Service.Connectivity, Provider.NiciraNvp)).thenReturn(false);
		// No nvp provider in the network
		assertFalse(_element.canHandle(net, Service.Connectivity));
		
		when(_networkModel.isProviderForNetwork(Provider.NiciraNvp, 42L)).thenReturn(false);
		when(_ntwkSrvcDao.canProviderSupportServiceInNetwork(42L, Service.Connectivity, Provider.NiciraNvp)).thenReturn(true);
		// NVP provider does not provide Connectivity for this network
		assertFalse(_element.canHandle(net, Service.Connectivity));
		
		when(_networkModel.isProviderForNetwork(Provider.NiciraNvp, 42L)).thenReturn(true);
		// Only service Connectivity is supported
		assertFalse(_element.canHandle(net, Service.Dhcp));
		
	}
	
	@Test
	public void implementTest() throws ConcurrentOperationException, ResourceUnavailableException, InsufficientCapacityException {
		Network network = mock(Network.class);
		when(network.getBroadcastDomainType()).thenReturn(BroadcastDomainType.Lswitch);
		when(network.getId()).thenReturn(42L);
		
		NetworkOffering offering = mock(NetworkOffering.class);
		when(offering.getId()).thenReturn(42L);
		when(offering.getTrafficType()).thenReturn(TrafficType.Guest);
		when(offering.getGuestType()).thenReturn(GuestType.Isolated);
		
		DeployDestination dest = mock(DeployDestination.class);
		
		Domain dom = mock(Domain.class);
		when(dom.getName()).thenReturn("domain");
		Account acc = mock(Account.class);
		when(acc.getAccountName()).thenReturn("accountname");
		ReservationContext context = mock(ReservationContext.class);
		when(context.getDomain()).thenReturn(dom);
		when(context.getAccount()).thenReturn(acc);
		
		//assertTrue(_element.implement(network, offering, dest, context));
	}

}
