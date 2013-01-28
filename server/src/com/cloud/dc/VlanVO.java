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
package com.cloud.dc;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.cloudstack.api.Identity;
import org.apache.cloudstack.api.InternalIdentity;

@Entity
@Table(name="vlan")
public class VlanVO implements Vlan {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	Long id;

	@Column(name="vlan_id")
	String vlanTag;

	@Column(name="vlan_gateway")
	String vlanGateway;

	@Column(name="vlan_netmask")
	String vlanNetmask;

	@Column(name="data_center_id")
	long dataCenterId;

	@Column(name="description")
	String ipRange;

    @Column(name="network_id")
    Long networkId;

    @Column(name="physical_network_id")
    Long physicalNetworkId;

	@Column(name="vlan_type")
	@Enumerated(EnumType.STRING)
	VlanType vlanType;

    @Column(name="uuid")
    String uuid;

	public VlanVO(VlanType vlanType, String vlanTag, String vlanGateway, String vlanNetmask, long dataCenterId, String ipRange, Long networkId, Long physicalNetworkId) {
		this.vlanType = vlanType;
		this.vlanTag = vlanTag;
		this.vlanGateway = vlanGateway;
		this.vlanNetmask = vlanNetmask;
		this.dataCenterId = dataCenterId;
		this.ipRange = ipRange;
		this.networkId = networkId;
		this.uuid = UUID.randomUUID().toString();
		this.physicalNetworkId = physicalNetworkId;
	}

	public VlanVO() {
		this.uuid = UUID.randomUUID().toString();
	}

	@Override
    public long getId() {
		return id;
	}

	@Override
    public String getVlanTag() {
		return vlanTag;
	}

	@Override
    public String getVlanGateway() {
		return vlanGateway;
	}

	@Override
    public String getVlanNetmask() {
        return vlanNetmask;
    }

	@Override
    public long getDataCenterId() {
		return dataCenterId;
	}

	@Override
    public String getIpRange() {
		return ipRange;
	}

	@Override
    public VlanType getVlanType() {
		return vlanType;
	}

    @Override
    public Long getNetworkId() {
        return networkId;
    }

    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }

    @Override
    public String getUuid() {
    	return this.uuid;
    }

    public void setUuid(String uuid) {
    	this.uuid = uuid;
    }
    @Override
    public Long getPhysicalNetworkId() {
        return physicalNetworkId;
    }

    public void setPhysicalNetworkId(Long physicalNetworkId) {
        this.physicalNetworkId = physicalNetworkId;
    }

    transient String toString;
    @Override
    public String toString() {
        if (toString == null) {
            toString = new StringBuilder("Vlan[").append(vlanTag).append("|").append(vlanGateway).append("|").append(vlanNetmask).
                    append("|").append(ipRange).append("|").append(networkId).append("]").toString();
        }
        return toString;
    }

}
