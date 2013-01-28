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
package com.cloud.network;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name=("remote_access_vpn"))
public class RemoteAccessVpnVO implements RemoteAccessVpn {
    @Column(name="account_id")
    private long accountId;

    @Column(name="network_id")
    private long networkId;
    
    @Column(name="domain_id")
    private long domainId;

    @Id
    @Column(name="vpn_server_addr_id")
    private long serverAddressId;
    
    @Column(name="local_ip")
    private String localIp;

    @Column(name="ip_range")
    private String ipRange;

    @Column(name="ipsec_psk")
    private String ipsecPresharedKey;
    
    @Column(name="state")
    private State state;

    public RemoteAccessVpnVO() { }

    public RemoteAccessVpnVO(long accountId, long domainId, long networkId, long publicIpId, String localIp, String ipRange,  String presharedKey) {
        this.accountId = accountId;
        this.serverAddressId = publicIpId;
        this.ipRange = ipRange;
        this.ipsecPresharedKey = presharedKey;
        this.localIp = localIp;
        this.domainId = domainId;
        this.networkId = networkId;
        this.state = State.Added;
    }
    
    @Override
    public State getState() {
        return state;
    }
    
    public void setState(State state) {
        this.state = state;
    }

    @Override
    public long getAccountId() {
        return accountId;
    }
    
    @Override
	public long getServerAddressId() {
        return serverAddressId;
    }

    @Override
    public String getIpRange() {
		return ipRange;
	}

    public void setIpRange(String ipRange) {
		this.ipRange = ipRange;
	}

	@Override
    public String getIpsecPresharedKey() {
		return ipsecPresharedKey;
	}

    public void setIpsecPresharedKey(String ipsecPresharedKey) {
		this.ipsecPresharedKey = ipsecPresharedKey;
	}

	@Override
    public String getLocalIp() {
		return localIp;
	}

	@Override
    public long getDomainId() {
		return domainId;
	}
	
	@Override
    public long getNetworkId() {
	    return networkId;
	}
}
