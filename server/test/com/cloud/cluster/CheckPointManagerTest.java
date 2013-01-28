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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Local;
import javax.naming.ConfigurationException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;

import com.cloud.agent.Listener;
import com.cloud.agent.api.Answer;
import com.cloud.agent.api.Command;
import com.cloud.cluster.dao.StackMaidDao;
import com.cloud.cluster.dao.StackMaidDaoImpl;
import com.cloud.configuration.Config;
import com.cloud.configuration.DefaultInterceptorLibrary;
import com.cloud.configuration.dao.ConfigurationDaoImpl;
import com.cloud.exception.AgentUnavailableException;
import com.cloud.exception.OperationTimedoutException;
import com.cloud.host.Status.Event;
import com.cloud.serializer.SerializerHelper;
import com.cloud.utils.component.ComponentLocator;
import com.cloud.utils.component.MockComponentLocator;
import com.cloud.utils.db.Transaction;
import com.cloud.utils.exception.CloudRuntimeException;

public class CheckPointManagerTest extends TestCase {
    private final static Logger s_logger = Logger.getLogger(CheckPointManagerTest.class);
    
    @Override
    @Before
    public void setUp() {
        MockComponentLocator locator = new MockComponentLocator("management-server");
        locator.addDao("StackMaidDao", StackMaidDaoImpl.class);
        locator.addDao("ConfigurationDao", ConfigurationDaoImpl.class);
        locator.addManager("ClusterManager", MockClusterManager.class);
        locator.makeActive(new DefaultInterceptorLibrary());
        MockMaid.map.clear();
        s_logger.info("Cleaning up the database");
        Connection conn = Transaction.getStandaloneConnection();
        try {
            conn.setAutoCommit(true);
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM stack_maid");
            stmt.executeUpdate();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new CloudRuntimeException("Unable to setup database", e);
        }
    }
    
    @Override
    @After
    public void tearDown() throws Exception {
    }
    
    public void testCompleteCase() throws Exception {
        ComponentLocator locator = ComponentLocator.getCurrentLocator();
        
        CheckPointManagerImpl taskMgr = ComponentLocator.inject(CheckPointManagerImpl.class);
        assertTrue(taskMgr.configure("TaskManager", new HashMap<String, Object>()));
        assertTrue(taskMgr.start());
        
        MockMaid delegate = new MockMaid();
        delegate.setValue("first");
        long taskId = taskMgr.pushCheckPoint(delegate);
        
        StackMaidDao maidDao = locator.getDao(StackMaidDao.class);
        CheckPointVO task = maidDao.findById(taskId);
        
        assertEquals(task.getDelegate(), MockMaid.class.getName());
        MockMaid retrieved = (MockMaid)SerializerHelper.fromSerializedString(task.getContext()); 
        assertEquals(retrieved.getValue(), delegate.getValue());
        
        delegate.setValue("second");
        taskMgr.updateCheckPointState(taskId, delegate);

        task = maidDao.findById(taskId);
        assertEquals(task.getDelegate(), MockMaid.class.getName());
        retrieved = (MockMaid)SerializerHelper.fromSerializedString(task.getContext()); 
        assertEquals(retrieved.getValue(), delegate.getValue());
        
        taskMgr.popCheckPoint(taskId);
        assertNull(maidDao.findById(taskId));
    }
    
    public void testSimulatedReboot() throws Exception {
        ComponentLocator locator = ComponentLocator.getCurrentLocator();
        
        CheckPointManagerImpl taskMgr = ComponentLocator.inject(CheckPointManagerImpl.class);
        assertTrue(taskMgr.configure("TaskManager", new HashMap<String, Object>()));
        assertTrue(taskMgr.start());
        
        MockMaid maid = new MockMaid();
        maid.setValue("first");
        long taskId = taskMgr.pushCheckPoint(maid);
        
        StackMaidDao maidDao = locator.getDao(StackMaidDao.class);
        CheckPointVO task = maidDao.findById(taskId);
        
        assertEquals(task.getDelegate(), MockMaid.class.getName());
        MockMaid retrieved = (MockMaid)SerializerHelper.fromSerializedString(task.getContext()); 
        assertEquals(retrieved.getValue(), maid.getValue());

        taskMgr.stop();
        
        assertNotNull(MockMaid.map.get(maid.getSeq()));
        
        taskMgr = ComponentLocator.inject(CheckPointManagerImpl.class);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(Config.TaskCleanupRetryInterval.key(), "1");
        taskMgr.configure("TaskManager", params);
        taskMgr.start();
        
        int i = 0;
        while (MockMaid.map.get(maid.getSeq()) != null && i++ < 5) {
            Thread.sleep(1000);
        }
        
        assertNull(MockMaid.map.get(maid.getSeq()));
    }
    
    public void testTakeover() throws Exception {
        ComponentLocator locator = ComponentLocator.getCurrentLocator();
        
        CheckPointManagerImpl taskMgr = ComponentLocator.inject(CheckPointManagerImpl.class);
        assertTrue(taskMgr.configure("TaskManager", new HashMap<String, Object>()));
        assertTrue(taskMgr.start());
        
        MockMaid delegate = new MockMaid();
        delegate.setValue("first");
        long taskId = taskMgr.pushCheckPoint(delegate);
        
        StackMaidDao maidDao = locator.getDao(StackMaidDao.class);
        CheckPointVO task = maidDao.findById(taskId);
        
        assertEquals(task.getDelegate(), MockMaid.class.getName());
        MockMaid retrieved = (MockMaid)SerializerHelper.fromSerializedString(task.getContext()); 
        assertEquals(retrieved.getValue(), delegate.getValue());

        Connection conn = Transaction.getStandaloneConnection();
        try {
            conn.setAutoCommit(true);
            PreparedStatement stmt = conn.prepareStatement("update stack_maid set msid=? where msid=?");
            stmt.setLong(1, 1234);
            stmt.setLong(2, ManagementServerNode.getManagementServerId());
            stmt.executeUpdate();
            stmt.close();
        } finally {
            conn.close();
        }
        
        MockClusterManager clusterMgr = (MockClusterManager)locator.getManager(ClusterManager.class);
        clusterMgr.triggerTakeover(1234);
        
        int i = 0;
        while (MockMaid.map.get(delegate.getSeq()) != null && i++ < 500) {
            Thread.sleep(1000);
        }
        
        assertNull(MockMaid.map.get(delegate.getSeq()));
    }
    
    public static class MockMaid implements CleanupMaid {
        private static int s_seq = 1;
        public static Map<Integer, MockMaid> map = new ConcurrentHashMap<Integer, MockMaid>();
        
        int seq;
        boolean canBeCleanup;
        String value;
        
        protected MockMaid() {
            canBeCleanup = true;
            seq = s_seq++;
            map.put(seq, this);
        }
        
        public int getSeq() {
            return seq;
        }
        
        public String getValue() {
            return value;
        }
        
        public void setCanBeCleanup(boolean canBeCleanup) {
            this.canBeCleanup = canBeCleanup;
        }

        @Override
        public int cleanup(CheckPointManager checkPointMgr) {
            s_logger.debug("Cleanup called for " + seq);
            map.remove(seq);
            return canBeCleanup ? 0 : -1;
        }
        
        public void setValue(String value) {
            this.value = value;
        }
        
        @Override
        public String getCleanupProcedure() {
            return "No cleanup necessary";
        }
    }
    
    @Local(value=ClusterManager.class)
    public static class MockClusterManager implements ClusterManager {
        String _name;
        ClusterManagerListener _listener;

        @Override
        public boolean configure(String name, Map<String, Object> params) throws ConfigurationException {
            _name = name;
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

        @Override
        public void OnReceiveClusterServicePdu(ClusterServicePdu pdu) {
            throw new CloudRuntimeException("Not implemented");
        }
        
        @Override
        public Answer[] execute(String strPeer, long agentId, Command[] cmds, boolean stopOnError) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public Answer[] sendToAgent(Long hostId, Command[] cmds, boolean stopOnError) throws AgentUnavailableException, OperationTimedoutException {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public boolean executeAgentUserRequest(long agentId, Event event) throws AgentUnavailableException {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public Boolean propagateAgentEvent(long agentId, Event event) throws AgentUnavailableException {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public int getHeartbeatThreshold() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public long getManagementNodeId() {
            return ManagementServerNode.getManagementServerId();
        }

        @Override
        public boolean isManagementNodeAlive(long msid) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public boolean pingManagementNode(long msid) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public long getCurrentRunId() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public String getSelfPeerName() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public String getSelfNodeIP() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public String getPeerName(long agentHostId) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public void registerListener(ClusterManagerListener listener) {
            _listener = listener;
        }

        @Override
        public void unregisterListener(ClusterManagerListener listener) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public ManagementServerHostVO getPeer(String peerName) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public void broadcast(long agentId, Command[] cmds) {
            throw new UnsupportedOperationException("Not implemented");
        }
        
        public void triggerTakeover(long msId) {
            ManagementServerHostVO node = new ManagementServerHostVO();
            node.setMsid(msId);
            
            List<ManagementServerHostVO> lst = new ArrayList<ManagementServerHostVO>();
            lst.add(node);
            
            _listener.onManagementNodeLeft(lst, ManagementServerNode.getManagementServerId());
        }
        
        protected MockClusterManager() {
        }
        
        @Override
        public boolean rebalanceAgent(long agentId, Event event, long currentOwnerId, long futureOwnerId) throws AgentUnavailableException, OperationTimedoutException {
            return false;
        }
        
        @Override
        public boolean isAgentRebalanceEnabled() {
            return false;
        }

		@Override
        public Boolean propagateResourceEvent(long agentId, com.cloud.resource.ResourceState.Event event) throws AgentUnavailableException {
	        // TODO Auto-generated method stub
	        return null;
        }

		@Override
        public boolean executeResourceUserRequest(long hostId, com.cloud.resource.ResourceState.Event event) throws AgentUnavailableException {
	        // TODO Auto-generated method stub
	        return false;
        }

        /* (non-Javadoc)
         * @see com.cloud.cluster.ClusterManager#executeAsync(java.lang.String, long, com.cloud.agent.api.Command[], boolean)
         */
        @Override
        public void executeAsync(String strPeer, long agentId, Command[] cmds, boolean stopOnError) {
            // TODO Auto-generated method stub
            
        }
    }
    
}
