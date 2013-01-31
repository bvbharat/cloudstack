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
package com.cloud.baremetal.networkservice;

import java.util.Map;

import javax.naming.ConfigurationException;

import com.cloud.agent.AgentManager;
import com.cloud.dc.dao.DataCenterDao;
import com.cloud.dc.dao.HostPodDao;
import com.cloud.host.dao.HostDao;
import com.cloud.utils.component.Inject;
import com.cloud.vm.dao.NicDao;

public abstract class BareMetalPxeServiceBase implements BaremetalPxeService {
	protected String _name;
	@Inject DataCenterDao _dcDao;
	@Inject HostDao _hostDao;
	@Inject AgentManager _agentMgr;
	@Inject HostPodDao _podDao;
	@Inject NicDao _nicDao;
	
	@Override
	public boolean configure(String name, Map<String, Object> params) throws ConfigurationException {
		_name = name;
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

	protected String getPxeServerGuid(String zoneId, String name, String ip) {
		return zoneId + "-" + name + "-" + ip;
	}
}
