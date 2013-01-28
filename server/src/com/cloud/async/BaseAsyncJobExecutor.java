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


public abstract class BaseAsyncJobExecutor implements AsyncJobExecutor {
	private SyncQueueItemVO _syncSource;
	private AsyncJobVO _job;
	private boolean _fromPreviousSession;
	private AsyncJobManager _asyncJobMgr;
	
	private static ThreadLocal<AsyncJobExecutor> s_currentExector = new ThreadLocal<AsyncJobExecutor>();
	
	public AsyncJobManager getAsyncJobMgr() {
		return _asyncJobMgr;
	}
	
	public void setAsyncJobMgr(AsyncJobManager asyncMgr) {
		_asyncJobMgr = asyncMgr;
	}
	
	public SyncQueueItemVO getSyncSource() {
		return _syncSource;
	}
	
	public void setSyncSource(SyncQueueItemVO syncSource) {
		_syncSource = syncSource;
	}
	
	public AsyncJobVO getJob() {
		return _job;
	}
	
	public void setJob(AsyncJobVO job) {
		_job = job;
	}
	
	public void setFromPreviousSession(boolean value) {
		_fromPreviousSession = value;
	}
	
	public boolean isFromPreviousSession() {
		return _fromPreviousSession;
	}
	
	public abstract boolean execute();
	
	public static AsyncJobExecutor getCurrentExecutor() {
		return s_currentExector.get();
	}
	
	public static void setCurrentExecutor(AsyncJobExecutor currentExecutor) {
		s_currentExector.set(currentExecutor);
	}
}
