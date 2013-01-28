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

import java.util.Date;
import java.util.TimeZone;

import javax.management.StandardMBean;

import com.cloud.utils.DateUtil;

public class AsyncJobMBeanImpl extends StandardMBean implements AsyncJobMBean {
	private AsyncJobVO _jobVo;
	
	public AsyncJobMBeanImpl(AsyncJobVO jobVo) {
		super(AsyncJobMBean.class, false);
		
		_jobVo = jobVo;
	}
	
	public long getAccountId() {
		return _jobVo.getAccountId();
	}
	
	public long getUserId() {
		return _jobVo.getUserId();
	}
	
	public String getCmd() {
		return _jobVo.getCmd();
	}
	
	public String getCmdInfo() {
		return _jobVo.getCmdInfo();
	}
	
	public String getStatus() {
		int jobStatus = _jobVo.getStatus();
		switch(jobStatus) {
		case AsyncJobResult.STATUS_SUCCEEDED :
			return "Completed";
		
		case AsyncJobResult.STATUS_IN_PROGRESS:
			return "In preogress";
			
		case AsyncJobResult.STATUS_FAILED:
			return "failed";
		}
		
		return "Unknow";
	}
	
	public int getProcessStatus() {
		return _jobVo.getProcessStatus();
	}
	
	public int getResultCode() {
		return _jobVo.getResultCode();
	}
	
	public String getResult() {
		return _jobVo.getResult();
	}
	
	public String getInstanceType() {
		if(_jobVo.getInstanceType() != null)
			return _jobVo.getInstanceType().toString();
		return "N/A";
	}
	
	public String getInstanceId() {
		if(_jobVo.getInstanceId() != null)
			return String.valueOf(_jobVo.getInstanceId());
		return "N/A";
	}
	
	public String getInitMsid() {
		if(_jobVo.getInitMsid() != null) {
			return String.valueOf(_jobVo.getInitMsid());
		}
		return "N/A";
	}
	
	public String getCreateTime() {
		Date time = _jobVo.getCreated();
		if(time != null)
			return DateUtil.getDateDisplayString(TimeZone.getDefault(), time);
		return "N/A";
	}
	
	public String getLastUpdateTime() {
		Date time = _jobVo.getLastUpdated();
		if(time != null)
			return DateUtil.getDateDisplayString(TimeZone.getDefault(), time);
		return "N/A";
	}
	
	public String getLastPollTime() {
		Date time = _jobVo.getLastPolled();
	
		if(time != null)
			return DateUtil.getDateDisplayString(TimeZone.getDefault(), time);
		return "N/A";
	}
	
	public String getSyncQueueId() {
		SyncQueueItemVO item = _jobVo.getSyncSource();
		if(item != null && item.getQueueId() != null) {
			return String.valueOf(item.getQueueId());
		}
		return "N/A";
	}
	
	public String getSyncQueueContentType() {
		SyncQueueItemVO item = _jobVo.getSyncSource();
		if(item != null) {
			return item.getContentType();
		}
		return "N/A";
	}
	
	public String getSyncQueueContentId() {
		SyncQueueItemVO item = _jobVo.getSyncSource();
		if(item != null && item.getContentId() != null) {
			return String.valueOf(item.getContentId());
		}
		return "N/A";
	}
	
}
