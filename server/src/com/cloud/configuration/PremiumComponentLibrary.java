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

import java.util.ArrayList;
import java.util.List;

import com.cloud.baremetal.BareMetalPingServiceImpl;
import com.cloud.baremetal.BareMetalTemplateAdapter;
import com.cloud.baremetal.BareMetalVmManagerImpl;
import com.cloud.baremetal.ExternalDhcpManagerImpl;
import com.cloud.baremetal.PxeServerManager.PxeServerType;
import com.cloud.baremetal.PxeServerManagerImpl;
import com.cloud.baremetal.PxeServerService;
import com.cloud.ha.HighAvailabilityManagerExtImpl;
import com.cloud.network.ExternalNetworkDeviceManagerImpl;
import com.cloud.network.NetworkUsageManagerImpl;
import com.cloud.secstorage.CommandExecLogDaoImpl;
import com.cloud.secstorage.PremiumSecondaryStorageManagerImpl;
import com.cloud.template.TemplateAdapter;
import com.cloud.template.TemplateAdapter.TemplateAdapterType;
import com.cloud.upgrade.PremiumDatabaseUpgradeChecker;
import com.cloud.usage.dao.UsageDaoImpl;
import com.cloud.usage.dao.UsageIPAddressDaoImpl;
import com.cloud.usage.dao.UsageJobDaoImpl;
import com.cloud.utils.component.SystemIntegrityChecker;

public class PremiumComponentLibrary extends DefaultComponentLibrary {
    @Override
    protected void populateDaos() {
    	super.populateDaos();
        addDao("UsageJobDao", UsageJobDaoImpl.class);
        addDao("UsageDao", UsageDaoImpl.class);
        addDao("UsageIpAddressDao", UsageIPAddressDaoImpl.class);
        addDao("CommandExecLogDao", CommandExecLogDaoImpl.class);
    }

    @Override
    protected void populateManagers() {
    	// override FOSS SSVM manager
    	super.populateManagers();
        addManager("secondary storage vm manager", PremiumSecondaryStorageManagerImpl.class);
	
        addManager("HA Manager", HighAvailabilityManagerExtImpl.class);
        addManager("ExternalNetworkManager", ExternalNetworkDeviceManagerImpl.class);
        addManager("BareMetalVmManager", BareMetalVmManagerImpl.class);
        addManager("ExternalDhcpManager", ExternalDhcpManagerImpl.class);
        addManager("PxeServerManager", PxeServerManagerImpl.class);
        addManager("NetworkUsageManager", NetworkUsageManagerImpl.class);
    }

    @Override
    protected void populateAdapters() {
    	super.populateAdapters();
    	addAdapter(PxeServerService.class, PxeServerType.PING.getName(), BareMetalPingServiceImpl.class);
    	addAdapter(TemplateAdapter.class, TemplateAdapterType.BareMetal.getName(), BareMetalTemplateAdapter.class);
    }
}
