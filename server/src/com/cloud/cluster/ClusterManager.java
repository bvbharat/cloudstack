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

import com.cloud.agent.api.Answer;
import com.cloud.agent.api.Command;
import com.cloud.exception.AgentUnavailableException;
import com.cloud.exception.OperationTimedoutException;
import com.cloud.host.Status.Event;
import com.cloud.resource.ResourceState;
import com.cloud.utils.component.Manager;

public interface ClusterManager extends Manager {
	public static final int DEFAULT_HEARTBEAT_INTERVAL = 1500;
	public static final int DEFAULT_HEARTBEAT_THRESHOLD = 150000;
	public static final String ALERT_SUBJECT = "cluster-alert";
	
	public void OnReceiveClusterServicePdu(ClusterServicePdu pdu);
    public void executeAsync(String strPeer, long agentId, Command [] cmds, boolean stopOnError);
    public Answer[] execute(String strPeer, long agentId, Command [] cmds, boolean stopOnError);

    public Answer[] sendToAgent(Long hostId, Command []  cmds, boolean stopOnError) throws AgentUnavailableException, OperationTimedoutException;
    public boolean executeAgentUserRequest(long agentId, Event event) throws AgentUnavailableException;
    public Boolean propagateAgentEvent(long agentId, Event event) throws AgentUnavailableException;
    public Boolean propagateResourceEvent(long agentId, ResourceState.Event event) throws AgentUnavailableException;
    public boolean executeResourceUserRequest(long hostId, ResourceState.Event event) throws AgentUnavailableException;
	
	public int getHeartbeatThreshold();
	
	public long getManagementNodeId();		// msid of current management server node
    public boolean isManagementNodeAlive(long msid);
    public boolean pingManagementNode(long msid);
	public long getCurrentRunId();
    
	public String getSelfPeerName();
	public String getSelfNodeIP();
    public String getPeerName(long agentHostId);
	
	public void registerListener(ClusterManagerListener listener);
	public void unregisterListener(ClusterManagerListener listener);
    public ManagementServerHostVO getPeer(String peerName);
    
    /**
     * Broadcast the command to all of the  management server nodes.
     * @param agentId agent id this broadcast is regarding
     * @param cmds commands to broadcast
     */
    public void broadcast(long agentId, Command[] cmds);
    
    boolean rebalanceAgent(long agentId, Event event, long currentOwnerId, long futureOwnerId) throws AgentUnavailableException, OperationTimedoutException;
    
    boolean isAgentRebalanceEnabled();
}
