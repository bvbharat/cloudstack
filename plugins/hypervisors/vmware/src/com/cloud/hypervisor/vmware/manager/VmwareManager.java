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
package com.cloud.hypervisor.vmware.manager;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.cloud.hypervisor.Hypervisor.HypervisorType;
import com.cloud.hypervisor.vmware.mo.HostMO;
import com.cloud.hypervisor.vmware.util.VmwareContext;
import com.cloud.utils.Pair;
import com.vmware.vim25.ManagedObjectReference;

public interface VmwareManager {
	public final String CONTEXT_STOCK_NAME = "vmwareMgr";
	
	// this limitation comes from the fact that we are using linked clone on shared VMFS storage,
	// we need to limit the size of vCenter cluster, http://en.wikipedia.org/wiki/VMware_VMFS
	public final int MAX_HOSTS_PER_CLUSTER = 8;

	String composeWorkerName();
	
    String getSystemVMIsoFileNameOnDatastore();
    String getSystemVMDefaultNicAdapterType();
    
	void prepareSecondaryStorageStore(String strStorageUrl);
	
	void setupResourceStartupParams(Map<String, Object> params);
	List<ManagedObjectReference> addHostToPodCluster(VmwareContext serviceContext, long dcId, Long podId, Long clusterId,
			String hostInventoryPath) throws Exception;

	String getManagementPortGroupByHost(HostMO hostMo) throws Exception; 
	String getServiceConsolePortGroupName();
	String getManagementPortGroupName();
	
	String getSecondaryStorageStoreUrl(long dcId);
	
	File getSystemVMKeyFile();
	
	VmwareStorageManager getStorageManager();
	long pushCleanupCheckpoint(String hostGuid, String vmName);
	void popCleanupCheckpoint(long checkpiont);
	void gcLeftOverVMs(VmwareContext context);
	
	Pair<Integer, Integer> getAddiionalVncPortRange();
	
	int getMaxHostsPerCluster();
	int getRouterExtraPublicNics();
	
	boolean beginExclusiveOperation(int timeOutSeconds);
	void endExclusiveOperation();

    boolean getNexusVSwitchGlobalParameter();

    Map<String, String> getNexusVSMCredentialsByClusterId(Long clusterId);

    String getPrivateVSwitchName(long dcId, HypervisorType hypervisorType);

    String getPublicVSwitchName(long dcId, HypervisorType hypervisorType);

    String getGuestVSwitchName(long dcId, HypervisorType hypervisorType);
}
