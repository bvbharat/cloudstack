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
package com.cloud.storage.allocator;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;

import com.cloud.configuration.dao.ConfigurationDao;
import com.cloud.deploy.DeploymentPlan;
import com.cloud.deploy.DeploymentPlanner.ExcludeList;
import com.cloud.storage.StorageManager;
import com.cloud.storage.StoragePool;
import com.cloud.utils.component.ComponentLocator;
import com.cloud.vm.DiskProfile;
import com.cloud.vm.VirtualMachine;
import com.cloud.vm.VirtualMachineProfile;

@Local(value=StoragePoolAllocator.class)
public class GarbageCollectingStoragePoolAllocator extends AbstractStoragePoolAllocator {
    private static final Logger s_logger = Logger.getLogger(GarbageCollectingStoragePoolAllocator.class);
    
    StoragePoolAllocator _firstFitStoragePoolAllocator;
    StoragePoolAllocator _localStoragePoolAllocator;
    StorageManager _storageMgr;
    ConfigurationDao _configDao;
    boolean _storagePoolCleanupEnabled;
    
    @Override
    public boolean allocatorIsCorrectType(DiskProfile dskCh) {
    	return true;
    }
    
    public Integer getStorageOverprovisioningFactor() {
    	return null;
    }
    
    public Long getExtraBytesPerVolume() {
    	return null;
    }
    
    @Override
    public List<StoragePool> allocateToPool(DiskProfile dskCh, VirtualMachineProfile<? extends VirtualMachine> vmProfile, DeploymentPlan plan, ExcludeList avoid, int returnUpTo) {
    	
    	if (!_storagePoolCleanupEnabled) {
    		s_logger.debug("Storage pool cleanup is not enabled, so GarbageCollectingStoragePoolAllocator is being skipped.");
    		return null;
    	}
    	
    	// Clean up all storage pools
    	_storageMgr.cleanupStorage(false);
    	// Determine what allocator to use
    	StoragePoolAllocator allocator;
    	if (localStorageAllocationNeeded(dskCh)) {
    		allocator = _localStoragePoolAllocator;
    	} else {
    		allocator = _firstFitStoragePoolAllocator;
    	}

    	// Try to find a storage pool after cleanup
        ExcludeList myAvoids = new ExcludeList(avoid.getDataCentersToAvoid(), avoid.getPodsToAvoid(), avoid.getClustersToAvoid(), avoid.getHostsToAvoid(), avoid.getPoolsToAvoid());
        
        return allocator.allocateToPool(dskCh, vmProfile, plan, myAvoids, returnUpTo);
    }

    @Override
    public boolean configure(String name, Map<String, Object> params) throws ConfigurationException {
        super.configure(name, params);
        
        ComponentLocator locator = ComponentLocator.getCurrentLocator();
        
        _firstFitStoragePoolAllocator = ComponentLocator.inject(FirstFitStoragePoolAllocator.class);
        _firstFitStoragePoolAllocator.configure("GCFirstFitStoragePoolAllocator", params);
        _localStoragePoolAllocator = ComponentLocator.inject(LocalStoragePoolAllocator.class);
        _localStoragePoolAllocator.configure("GCLocalStoragePoolAllocator", params);
        
        _storageMgr = locator.getManager(StorageManager.class);
        if (_storageMgr == null) {
        	throw new ConfigurationException("Unable to get " + StorageManager.class.getName());
        }
        
        _configDao = locator.getDao(ConfigurationDao.class);
        if (_configDao == null) {
            throw new ConfigurationException("Unable to get the configuration dao.");
        }
        
        String storagePoolCleanupEnabled = _configDao.getValue("storage.pool.cleanup.enabled");
        _storagePoolCleanupEnabled = (storagePoolCleanupEnabled == null) ? true : Boolean.parseBoolean(storagePoolCleanupEnabled);
        
        return true;
    }
    
    public GarbageCollectingStoragePoolAllocator() {
    }
    
}
