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
package com.cloud.agent.api;

import com.cloud.storage.Storage.StoragePoolType;

public class AttachVolumeCommand extends Command {

	boolean attach;
	String vmName;
	StoragePoolType pooltype;
	String poolUuid;
	String volumeFolder;
	String volumePath;
	String volumeName;
	Long deviceId;
	String chainInfo;

	protected AttachVolumeCommand() {
	}

	public AttachVolumeCommand(boolean attach, String vmName, StoragePoolType pooltype, String volumeFolder, String volumePath, String volumeName, Long deviceId, String chainInfo) {
		this.attach = attach;
		this.vmName = vmName;
		this.pooltype = pooltype;
		this.volumeFolder = volumeFolder;
		this.volumePath = volumePath;
		this.volumeName = volumeName;
		this.deviceId = deviceId;
		this.chainInfo = chainInfo;
	}

	@Override
    public boolean executeInSequence() {
        return true;
    }

	public boolean getAttach() {
		return attach;
	}

	public String getVmName() {
		return vmName;
	}

	public StoragePoolType getPooltype() {
        return pooltype;
    }

    public void setPooltype(StoragePoolType pooltype) {
        this.pooltype = pooltype;
    }

    public String getVolumeFolder() {
		return volumeFolder;
	}

	public String getVolumePath() {
		return volumePath;
	}

	public String getVolumeName() {
		return volumeName;
	}

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getPoolUuid() {
    	return poolUuid;
    }

    public void setPoolUuid(String poolUuid) {
    	this.poolUuid = poolUuid;
    }

    public String getChainInfo() {
    	return chainInfo;
    }
}
