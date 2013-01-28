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
package com.cloud.storage;

import java.util.List;

import org.apache.log4j.Logger;

import com.cloud.agent.Listener;
import com.cloud.agent.api.AgentControlAnswer;
import com.cloud.agent.api.AgentControlCommand;
import com.cloud.agent.api.Answer;
import com.cloud.agent.api.Command;
import com.cloud.agent.api.StartupCommand;
import com.cloud.agent.api.StartupStorageCommand;
import com.cloud.agent.api.StoragePoolInfo;
import com.cloud.capacity.Capacity;
import com.cloud.capacity.CapacityVO;
import com.cloud.capacity.dao.CapacityDao;
import com.cloud.dc.DataCenterVO;
import com.cloud.dc.dao.DataCenterDao;
import com.cloud.exception.ConnectionException;
import com.cloud.host.HostVO;
import com.cloud.host.Status;
import com.cloud.hypervisor.Hypervisor.HypervisorType;
import com.cloud.storage.dao.StoragePoolDao;
import com.cloud.storage.dao.StoragePoolHostDao;
import com.cloud.utils.component.Inject;
import com.cloud.utils.db.DB;
import com.cloud.utils.db.SearchCriteria;
import com.cloud.utils.db.Transaction;

public class LocalStoragePoolListener implements Listener {
    private final static Logger s_logger = Logger.getLogger(LocalStoragePoolListener.class);
    @Inject StoragePoolDao _storagePoolDao;
    @Inject StoragePoolHostDao _storagePoolHostDao;
    @Inject CapacityDao _capacityDao;
    @Inject StorageManager _storageMgr;
    @Inject DataCenterDao _dcDao;

    @Override
    public int getTimeout() {
        return 0;
    }

    @Override
    public boolean isRecurring() {
        return false;
    }

    @Override
    public boolean processAnswers(long agentId, long seq, Answer[] answers) {
        return false;
    }

    @Override
    public boolean processCommands(long agentId, long seq, Command[] commands) {
        return false;
    }
    
    @Override
    @DB
    public void processConnect(HostVO host, StartupCommand cmd, boolean forRebalance) throws ConnectionException {
        if (!(cmd instanceof StartupStorageCommand)) {
            return;
        }
        
        StartupStorageCommand ssCmd = (StartupStorageCommand)cmd;
        
        if (ssCmd.getResourceType() != Storage.StorageResourceType.STORAGE_POOL) {
            return;
        }
        
        StoragePoolInfo pInfo = ssCmd.getPoolInfo();
        if (pInfo == null) {
            return;
        }

        DataCenterVO dc = _dcDao.findById(host.getDataCenterId());
        if (dc == null || !dc.isLocalStorageEnabled()) {
            return;
        }

        try {
            StoragePoolVO pool = _storagePoolDao.findPoolByHostPath(host.getDataCenterId(), host.getPodId(), pInfo.getHost(), pInfo.getHostPath(), pInfo.getUuid());
        	if(pool == null && host.getHypervisorType() == HypervisorType.VMware) {
        		// perform run-time upgrade. In versions prior to 2.2.12, there is a bug that we don't save local datastore info (host path is empty), this will cause us
        		// not able to distinguish multiple local datastores that may be available on the host, to support smooth migration, we 
        		// need to perform runtime upgrade here
        		if(pInfo.getHostPath().length() > 0) {
        			pool = _storagePoolDao.findPoolByHostPath(host.getDataCenterId(), host.getPodId(), pInfo.getHost(), "", pInfo.getUuid());
        		}
        	}
            
            if (pool == null) {
            	
                long poolId = _storagePoolDao.getNextInSequence(Long.class, "id");
                String name = cmd.getName() == null ? (host.getName() + " Local Storage") : cmd.getName();
                Transaction txn = Transaction.currentTxn();
                txn.start();
                pool = new StoragePoolVO(poolId, name, pInfo.getUuid(), pInfo.getPoolType(), host.getDataCenterId(),
                                         host.getPodId(), pInfo.getAvailableBytes(), pInfo.getCapacityBytes(), pInfo.getHost(), 0,
                                         pInfo.getHostPath());
                pool.setClusterId(host.getClusterId());
                _storagePoolDao.persist(pool, pInfo.getDetails());
                StoragePoolHostVO poolHost = new StoragePoolHostVO(pool.getId(), host.getId(), pInfo.getLocalPath());
                _storagePoolHostDao.persist(poolHost);
                _storageMgr.createCapacityEntry(pool, Capacity.CAPACITY_TYPE_LOCAL_STORAGE, pool.getCapacityBytes() - pool.getAvailableBytes());
                
                txn.commit();
            } else {
                Transaction txn = Transaction.currentTxn();
                txn.start();
                pool.setPath(pInfo.getHostPath());
                pool.setAvailableBytes(pInfo.getAvailableBytes());
                pool.setCapacityBytes(pInfo.getCapacityBytes());
                _storagePoolDao.update(pool.getId(), pool);
                if (pInfo.getDetails() != null) {
                    _storagePoolDao.updateDetails(pool.getId(), pInfo.getDetails());
                }
                StoragePoolHostVO poolHost = _storagePoolHostDao.findByPoolHost(pool.getId(), host.getId());
                if (poolHost == null) {
                    poolHost = new StoragePoolHostVO(pool.getId(), host.getId(), pInfo.getLocalPath());
                    _storagePoolHostDao.persist(poolHost);
                }
                
                _storageMgr.createCapacityEntry(pool, Capacity.CAPACITY_TYPE_LOCAL_STORAGE, pool.getCapacityBytes() - pool.getAvailableBytes());
                
                txn.commit();
            }
        } catch (Exception e) {
            s_logger.warn("Unable to setup the local storage pool for " + host, e);
            throw new ConnectionException(true, "Unable to setup the local storage pool for " + host, e);
        }
    }
   

    @Override
    public AgentControlAnswer processControlCommand(long agentId, AgentControlCommand cmd) {
        return null;
    }

    @Override
    public boolean processDisconnect(long agentId, Status state) {
        return false;
    }

    @Override
    public boolean processTimeout(long agentId, long seq) {
        return false;
    }
}
