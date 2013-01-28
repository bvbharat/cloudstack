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
package com.cloud.async;

import java.util.Map;

import javax.ejb.Local;
import javax.naming.ConfigurationException;

import com.cloud.agent.AgentManager;
import com.cloud.async.dao.AsyncJobDao;
import com.cloud.event.dao.EventDao;
import com.cloud.network.NetworkManager;
import com.cloud.network.NetworkModel;
import com.cloud.network.dao.IPAddressDao;
import com.cloud.server.ManagementServer;
import com.cloud.storage.StorageManager;
import com.cloud.storage.dao.VolumeDao;
import com.cloud.storage.snapshot.SnapshotManager;
import com.cloud.user.AccountManager;
import com.cloud.user.dao.AccountDao;
import com.cloud.user.dao.UserDao;
import com.cloud.utils.component.ComponentLocator;
import com.cloud.vm.UserVmManager;
import com.cloud.vm.VirtualMachineManager;
import com.cloud.vm.dao.DomainRouterDao;
import com.cloud.vm.dao.UserVmDao;

@Local(value={AsyncJobExecutorContext.class})
public class AsyncJobExecutorContextImpl implements AsyncJobExecutorContext {
	private String _name;
	
    private AgentManager _agentMgr;
	private NetworkModel _networkMgr;
	private UserVmManager _vmMgr;
    private SnapshotManager _snapMgr;
	private AccountManager _accountMgr;
	private StorageManager _storageMgr;
    private EventDao _eventDao;
    private UserVmDao _vmDao;
    private AccountDao _accountDao;
    private VolumeDao _volumeDao;
    private DomainRouterDao _routerDao;
    private IPAddressDao _ipAddressDao;
    private AsyncJobDao _jobDao;
    private UserDao _userDao;
    private VirtualMachineManager _itMgr;
    
    private ManagementServer _managementServer;
    
	@Override
	public ManagementServer getManagementServer() {
		return _managementServer;
	}

	@Override
	public AgentManager getAgentMgr() {
		return _agentMgr;
	}
	
	@Override
	public NetworkModel getNetworkMgr() {
		return _networkMgr;
	}
	
	@Override
	public UserVmManager getVmMgr() {
		return _vmMgr;
	}
	
	@Override
	public StorageManager getStorageMgr() {
		return _storageMgr;
	}
	
	/**server/src/com/cloud/async/AsyncJobExecutorContext.java
     * @return the _snapMgr
     */
	@Override
    public SnapshotManager getSnapshotMgr() {
        return _snapMgr;
    }

    @Override
	public AccountManager getAccountMgr() {
		return _accountMgr;
	}
	
	@Override
	public EventDao getEventDao() {
		return _eventDao;
	}
	
	@Override
	public UserVmDao getVmDao() {
		return _vmDao;
	}
	
	@Override
	public AccountDao getAccountDao() {
		return _accountDao;
	}
	
	@Override
	public VolumeDao getVolumeDao() {
		return _volumeDao;
	}

	@Override
    public DomainRouterDao getRouterDao() {
		return _routerDao;
	}
	
	@Override
    public IPAddressDao getIpAddressDao() {
    	return _ipAddressDao;
    }
	
	@Override
    public AsyncJobDao getJobDao() {
    	return _jobDao;
    }
	
	@Override
    public UserDao getUserDao() {
    	return _userDao;
    }
	
	@Override
	public VirtualMachineManager getItMgr() {
		return _itMgr;
	}
	
    @Override
    public boolean configure(String name, Map<String, Object> params) throws ConfigurationException {
    	_name = name;
		ComponentLocator locator = ComponentLocator.getCurrentLocator();
		
		_managementServer = (ManagementServer)ComponentLocator.getComponent("management-server");
        if (_managementServer == null) {
            throw new ConfigurationException("unable to get " + ManagementServer.class.getName());
        }

        _agentMgr = locator.getManager(AgentManager.class);
        if (_agentMgr == null) {
            throw new ConfigurationException("unable to get " + AgentManager.class.getName());
        }
        
        _networkMgr = locator.getManager(NetworkModel.class);
        if (_networkMgr == null) {
            throw new ConfigurationException("unable to get " + NetworkModel.class.getName());
        }
        
        _vmMgr = locator.getManager(UserVmManager.class);
        if (_vmMgr == null) {
            throw new ConfigurationException("unable to get " + UserVmManager.class.getName());
        }
        
        _snapMgr = locator.getManager(SnapshotManager.class);
        if (_snapMgr == null) {
            throw new ConfigurationException("unable to get " + SnapshotManager.class.getName());
        }
        
        _accountMgr = locator.getManager(AccountManager.class);
        if (_accountMgr == null) {
            throw new ConfigurationException("unable to get " + AccountManager.class.getName());
        }
        
        _storageMgr = locator.getManager(StorageManager.class);
        if (_storageMgr == null) {
        	throw new ConfigurationException("unable to get " + StorageManager.class.getName());
        }
        
        _eventDao = locator.getDao(EventDao.class);
        if (_eventDao == null) {
            throw new ConfigurationException("unable to get " + EventDao.class.getName());
        }
        
        _vmDao = locator.getDao(UserVmDao.class);
        if (_vmDao == null) {
            throw new ConfigurationException("unable to get " + UserVmDao.class.getName());
        }
        
        _accountDao = locator.getDao(AccountDao.class);
        if (_accountDao == null) {
            throw new ConfigurationException("unable to get " + AccountDao.class.getName());
        }
        
        _volumeDao = locator.getDao(VolumeDao.class);
        if (_volumeDao == null) {
            throw new ConfigurationException("unable to get " + VolumeDao.class.getName());
        }
        
        _routerDao = locator.getDao(DomainRouterDao.class);
        if (_routerDao == null) {
            throw new ConfigurationException("unable to get " + DomainRouterDao.class.getName());
        }
        
        _ipAddressDao = locator.getDao(IPAddressDao.class);
        if (_ipAddressDao == null) {
            throw new ConfigurationException("unable to get " + IPAddressDao.class.getName());
        }
        
        _jobDao = locator.getDao(AsyncJobDao.class);
        if(_jobDao == null) {
            throw new ConfigurationException("unable to get " + AsyncJobDao.class.getName());
        }
        
        _userDao = locator.getDao(UserDao.class);
        if(_userDao == null) {
            throw new ConfigurationException("unable to get " + UserDao.class.getName());
        }
        
        _itMgr = locator.getManager(VirtualMachineManager.class);
        if (_itMgr == null) {
        	throw new ConfigurationException("unable to get " + VirtualMachineManager.class.getName());
        }
    	return true;
    }
	
    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
    
    @Override
    public String getName() {
    	return _name;
    }
}
