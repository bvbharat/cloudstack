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


public interface AsyncJobExecutor {
	public AsyncJobManager getAsyncJobMgr();
	public void setAsyncJobMgr(AsyncJobManager asyncMgr);
	public SyncQueueItemVO getSyncSource();
	public void setSyncSource(SyncQueueItemVO syncSource);
	public AsyncJobVO getJob();
	public void setJob(AsyncJobVO job);
	public void setFromPreviousSession(boolean value);
	public boolean isFromPreviousSession();
	
	/**
	 * 
	 * 	otherwise return false and once the executor finally has completed with the sync source,
	 *  it needs to call AsyncJobManager.releaseSyncSource
	 *  
	 *  if executor does not have a sync source, always return true
	 */
	public boolean execute();
}

