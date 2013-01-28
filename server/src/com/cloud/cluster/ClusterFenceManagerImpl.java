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

import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;

import com.cloud.utils.component.Inject;

@Local(value={ClusterFenceManager.class})
public class ClusterFenceManagerImpl implements ClusterFenceManager, ClusterManagerListener {
    private static final Logger s_logger = Logger.getLogger(ClusterFenceManagerImpl.class);
	
	@Inject ClusterManager _clusterMgr;
	private String _name;
	
	@Override
	public boolean configure(String name, Map<String, Object> params) throws ConfigurationException {
		_name = name;
		
		_clusterMgr.registerListener(this);
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
	public void onManagementNodeJoined(List<ManagementServerHostVO> nodeList, long selfNodeId) {
	}

	@Override
	public void onManagementNodeLeft(List<ManagementServerHostVO> nodeList, long selfNodeId) {
	}

	@Override
	public void onManagementNodeIsolated() {
		s_logger.error("Received node isolation notification, will perform self-fencing and shut myself down");
		System.exit(SELF_FENCING_EXIT_CODE);
	}
}
