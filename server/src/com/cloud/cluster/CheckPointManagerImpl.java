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
package com.cloud.cluster;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.ejb.Local;
import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;

import com.cloud.cluster.dao.StackMaidDao;
import com.cloud.configuration.Config;
import com.cloud.configuration.dao.ConfigurationDao;
import com.cloud.serializer.SerializerHelper;
import com.cloud.utils.DateUtil;
import com.cloud.utils.NumbersUtil;
import com.cloud.utils.component.ComponentLocator;
import com.cloud.utils.component.Inject;
import com.cloud.utils.component.Manager;
import com.cloud.utils.concurrency.NamedThreadFactory;
import com.cloud.utils.db.DB;
import com.cloud.utils.db.GlobalLock;

@Local(value=CheckPointManager.class)
public class CheckPointManagerImpl implements CheckPointManager, Manager, ClusterManagerListener {
    private static final Logger s_logger = Logger.getLogger(CheckPointManagerImpl.class);

    private static final int ACQUIRE_GLOBAL_LOCK_TIMEOUT_FOR_COOPERATION = 3; // 3 seconds
    private int _cleanupRetryInterval;

    private String _name;

    @Inject
    private StackMaidDao _maidDao;

    @Inject
    private ClusterManager _clusterMgr;
    
    long _msId;

    private final ScheduledExecutorService _cleanupScheduler = Executors.newScheduledThreadPool(1, new NamedThreadFactory("Task-Cleanup"));
    
    protected CheckPointManagerImpl() {
    }

    @Override
    public boolean configure(String name, Map<String, Object> xmlParams) throws ConfigurationException {
        _name = name;

        if (s_logger.isInfoEnabled()) {
            s_logger.info("Start configuring StackMaidManager : " + name);
        }

        StackMaid.init(ManagementServerNode.getManagementServerId());
        _msId = ManagementServerNode.getManagementServerId();

        _clusterMgr.registerListener(this);
        
        ComponentLocator locator = ComponentLocator.getCurrentLocator();
        ConfigurationDao configDao = locator.getDao(ConfigurationDao.class);
        
        Map<String, String> params = configDao.getConfiguration(xmlParams);
        
        _cleanupRetryInterval = NumbersUtil.parseInt(params.get(Config.TaskCleanupRetryInterval.key()), 600);
        _maidDao.takeover(_msId, _msId);
        return true;
    }

    private void cleanupLeftovers(List<CheckPointVO> l) {
        for (CheckPointVO maid : l) {
            if (StackMaid.doCleanup(maid)) {
                _maidDao.expunge(maid.getId());
            }
        }
    }
    
    @Override
	public void onManagementNodeIsolated() {
	}

    @DB
    private Runnable getGCTask() {
        return new Runnable() {
            @Override
            public void run() {
                GlobalLock scanLock = GlobalLock.getInternLock("StackMaidManagerGC");
                try {
                    if (scanLock.lock(ACQUIRE_GLOBAL_LOCK_TIMEOUT_FOR_COOPERATION)) {
                        try {
                            reallyRun();
                        } finally {
                            scanLock.unlock();
                        }
                    }
                } finally {
                    scanLock.releaseRef();
                }
            }

            public void reallyRun() {
                try {
                    Date cutTime = new Date(DateUtil.currentGMTTime().getTime() - 7200000);
                    List<CheckPointVO> l = _maidDao.listLeftoversByCutTime(cutTime);
                    cleanupLeftovers(l);
                } catch (Throwable e) {
                    s_logger.error("Unexpected exception when trying to execute queue item, ", e);
                }
            }
        };
    }

    @Override
    public boolean start() {
        _cleanupScheduler.schedule(new CleanupTask(), _cleanupRetryInterval > 0 ? _cleanupRetryInterval : 600, TimeUnit.SECONDS);
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

    @Override
    public void onManagementNodeJoined(List<ManagementServerHostVO> nodeList, long selfNodeId) {
        // Nothing to do
    }

    @Override
    public void onManagementNodeLeft(List<ManagementServerHostVO> nodeList, long selfNodeId) {
        for (ManagementServerHostVO node : nodeList) {
            if (_maidDao.takeover(node.getMsid(), selfNodeId)) {
                s_logger.info("Taking over from " + node.getMsid());
                _cleanupScheduler.execute(new CleanupTask());
            }
        }
    }
    
    @Override
    @DB
    public long pushCheckPoint(CleanupMaid context) {
        long seq =  _maidDao.pushCleanupDelegate(_msId, 0, context.getClass().getName(), context);
        return seq;
    }

    @Override
    @DB
    public void updateCheckPointState(long taskId, CleanupMaid updatedContext) {
        CheckPointVO task = _maidDao.createForUpdate();
        task.setDelegate(updatedContext.getClass().getName());
        task.setContext(SerializerHelper.toSerializedStringOld(updatedContext));
        _maidDao.update(taskId, task);
    }

    @Override
    @DB
    public void popCheckPoint(long taskId) {
        _maidDao.remove(taskId);
    }
    
    protected boolean cleanup(CheckPointVO task) {
        s_logger.info("Cleaning up " + task);
        CleanupMaid delegate = (CleanupMaid)SerializerHelper.fromSerializedString(task.getContext());
        assert delegate.getClass().getName().equals(task.getDelegate()) : "Deserializer says " + delegate.getClass().getName() + " but it's suppose to be " + task.getDelegate();
        
        int result = delegate.cleanup(this);
        if (result <= 0) {
            if (result == 0) {
                s_logger.info("Successfully cleaned up " + task.getId());
            } else {
                s_logger.warn("Unsuccessful in cleaning up " + task + ".  Procedure to cleanup manaully: " + delegate.getCleanupProcedure());
            }
            popCheckPoint(task.getId());
            return true;
        } else {
            s_logger.error("Unable to cleanup " + task.getId());
            return false;
        }
    }
    
    class CleanupTask implements Runnable {
        private Date _curDate;
        public CleanupTask() {
        	_curDate = new Date();
        }

        @Override
        public void run() {
            try {
            	List<CheckPointVO> tasks  = _maidDao.listLeftoversByCutTime(_curDate, _msId);
            	tasks.addAll(_maidDao.listCleanupTasks(_msId));
                
                List<CheckPointVO> retries = new ArrayList<CheckPointVO>();
                
                for (CheckPointVO task : tasks) {
                    try {
                        if (!cleanup(task)) {
                            retries.add(task);
                        }
                    } catch (Exception e) {
                        s_logger.warn("Unable to clean up " + task, e);
                        
                    }
                }
                
                if (retries.size() > 0) {
                    if (_cleanupRetryInterval > 0) {
                        _cleanupScheduler.schedule(this, _cleanupRetryInterval, TimeUnit.SECONDS);
                    } else {
                        for (CheckPointVO task : retries) {
                            s_logger.warn("Cleanup procedure for " + task + ": " + ((CleanupMaid)SerializerHelper.fromSerializedString(task.getContext())).getCleanupProcedure());
                        }
                    }
                }
                
            } catch (Exception e) {
                s_logger.error("Unable to cleanup all of the tasks for " + _msId, e);
            }
        }
    }
}
