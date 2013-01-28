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
package com.cloud.baremetal;

public class PxeServerProfile {
	Long zoneId;
	Long podId;
	String url;
	String username;
	String password;
	String type;
	String pingStorageServerIp;
	String pingDir;
	String tftpDir;
	String pingCifsUserName;
	String pingCifspassword;
	
	public PxeServerProfile (Long zoneId, Long podId, String url, String username, String password, String type,
			String pingStorageServerIp, String pingDir, String tftpDir, String pingCifsUserName, String pingCifsPassword) {
		this.zoneId = zoneId;
		this.podId = podId;
		this.url = url;
		this.username = username;
		this.password = password;
		this.type = type;
		this.pingStorageServerIp = pingStorageServerIp;
		this.pingDir = pingDir;
		this.tftpDir = tftpDir;
		this.pingCifsUserName = pingCifsUserName;
		this.pingCifspassword = pingCifsPassword;
	}
	
	public Long getZoneId() {
		return zoneId;
	}
	
	public Long getPodId() {
		return podId;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getType() {
		return type;
	}
	
	public String getPingStorageServerIp() {
		return pingStorageServerIp;
	}
	
	public String getPingDir() {
		return pingDir;
	}
	
	public String getTftpDir() {
		return tftpDir;
	}
	
	public String getPingCifsUserName() {
		return pingCifsUserName;
	}
	
	public String getPingCifspassword() {
		return pingCifspassword;
	}
}
