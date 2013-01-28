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
package com.cloud.configuration;

import com.cloud.agent.manager.MockAgentManagerImpl;
import com.cloud.agent.manager.MockStorageManagerImpl;
import com.cloud.agent.manager.MockVmManagerImpl;
import com.cloud.agent.manager.SimulatorManagerImpl;
import com.cloud.simulator.dao.MockConfigurationDaoImpl;
import com.cloud.simulator.dao.MockHostDaoImpl;
import com.cloud.simulator.dao.MockSecStorageDaoImpl;
import com.cloud.simulator.dao.MockSecurityRulesDaoImpl;
import com.cloud.simulator.dao.MockStoragePoolDaoImpl;
import com.cloud.simulator.dao.MockVMDaoImpl;
import com.cloud.simulator.dao.MockVolumeDaoImpl;

public class SimulatorComponentLibrary extends PremiumComponentLibrary {
	  @Override
	    protected void populateManagers() {
	        addManager("VM Manager", MockVmManagerImpl.class);
	        addManager("agent manager", MockAgentManagerImpl.class);
	        addManager("storage manager", MockStorageManagerImpl.class);
	        addManager("SimulatorManager", SimulatorManagerImpl.class);
	    }

	    @Override
	    protected void populateDaos() {
	        addDao("mock Host", MockHostDaoImpl.class);
	        addDao("mock secondary storage", MockSecStorageDaoImpl.class);
	        addDao("mock storage pool", MockStoragePoolDaoImpl.class);
	        addDao("mock vm", MockVMDaoImpl.class);
	        addDao("mock volume", MockVolumeDaoImpl.class);
	        addDao("mock config", MockConfigurationDaoImpl.class);
	        addDao("mock security rules", MockSecurityRulesDaoImpl.class);
	    }
}
