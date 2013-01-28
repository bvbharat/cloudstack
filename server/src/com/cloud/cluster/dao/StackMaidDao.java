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
package com.cloud.cluster.dao;

import java.util.Date;
import java.util.List;

import com.cloud.cluster.CheckPointVO;
import com.cloud.utils.db.GenericDao;

public interface StackMaidDao extends GenericDao<CheckPointVO, Long> {
	public long pushCleanupDelegate(long msid, int seq, String delegateClzName, Object context);
	public CheckPointVO popCleanupDelegate(long msid);
	public void clearStack(long msid);
	
	public List<CheckPointVO> listLeftoversByMsid(long msid); 
	public List<CheckPointVO> listLeftoversByCutTime(Date cutTime);
	
	/**
	 * Take over the task items of another management server and clean them up.
	 * 
	 * @param takeOverMsid management server id to take over.
	 * @param selfId the management server id of this node.
	 * @return list of tasks to take over.
	 */
	boolean takeover(long takeOverMsid, long selfId);
	
	List<CheckPointVO> listCleanupTasks(long selfId);
	List<CheckPointVO> listLeftoversByCutTime(Date cutTime, long msid);
}
