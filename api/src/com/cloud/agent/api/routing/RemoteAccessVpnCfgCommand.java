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
package com.cloud.agent.api.routing;


public class RemoteAccessVpnCfgCommand extends NetworkElementCommand {

	boolean create;
    String vpnServerIp;
    String ipRange;
    String presharedKey;
    String localIp;

    protected RemoteAccessVpnCfgCommand() {
    	this.create = false;
    }

    public boolean isCreate() {
		return create;
	}

	@Override
    public boolean executeInSequence() {
        return true;
    }


	public RemoteAccessVpnCfgCommand(boolean create, String vpnServerAddress, String localIp, String ipRange, String ipsecPresharedKey) {
		this.vpnServerIp = vpnServerAddress;
		this.ipRange  = ipRange;
		this.presharedKey = ipsecPresharedKey;
		this.localIp = localIp;
		this.create = create;
	}

	public String getVpnServerIp() {
		return vpnServerIp;
	}

	public void setVpnServerIp(String vpnServerIp) {
		this.vpnServerIp = vpnServerIp;
	}

	public String getIpRange() {
		return ipRange;
	}

	public void setIpRange(String ipRange) {
		this.ipRange = ipRange;
	}

	public String getPresharedKey() {
		return presharedKey;
	}

	public void setPresharedKey(String presharedKey) {
		this.presharedKey = presharedKey;
	}

	public String getLocalIp() {
		return localIp;
	}

}
