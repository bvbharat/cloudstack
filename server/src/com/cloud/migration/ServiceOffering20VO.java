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
package com.cloud.migration;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.cloud.dc.Vlan;
import com.cloud.dc.Vlan.VlanType;
import com.cloud.utils.db.GenericDao;
import org.apache.cloudstack.api.InternalIdentity;

@Entity
@Table(name="service_offering")
public class ServiceOffering20VO implements InternalIdentity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
	private Long id = null;
    
    @Column(name="name")
	private String name = null;
    
    @Column(name="cpu")
	private int cpu;
    
    @Column(name="speed")
    private int speed;
    
    @Column(name="ram_size")
	private int ramSize;
    
    @Column(name="nw_rate")
    private int rateMbps;
    
    @Column(name="mc_rate")
    private int multicastRateMbps;
    
    @Column(name="mirrored")
    private boolean mirroredVolumes;
    
    @Column(name="ha_enabled")
    private boolean offerHA;
    
    @Column(name="display_text")
	private String displayText = null;
    
    @Column(name="guest_ip_type")
    @Enumerated(EnumType.STRING)
    private Vlan.VlanType guestIpType = Vlan.VlanType.VirtualNetwork;
    
    @Column(name="use_local_storage")
    private boolean useLocalStorage;
    
	@Column(name=GenericDao.CREATED_COLUMN)
    private Date created;
    
    @Column(name=GenericDao.REMOVED_COLUMN)
    private Date removed;
    
    protected ServiceOffering20VO() {
    }

    public ServiceOffering20VO(Long id, String name, int cpu, int ramSize, int speed, int rateMbps, int multicastRateMbps, boolean offerHA, String displayText, boolean localStorageRequired) {
        this(id, name, cpu, ramSize, speed, rateMbps, multicastRateMbps, offerHA, displayText, Vlan.VlanType.VirtualNetwork, localStorageRequired);
    }
    
    public ServiceOffering20VO(Long id, String name, int cpu, int ramSize, int speed, int rateMbps, int multicastRateMbps, boolean offerHA, String displayText, VlanType guestIpType, boolean useLocalStorage) {
        this.id = id;
        this.name = name;
        this.cpu = cpu;
        this.ramSize = ramSize;
        this.speed = speed;
        this.rateMbps = rateMbps;
        this.multicastRateMbps = multicastRateMbps;
        this.offerHA = offerHA;
        this.displayText = displayText;
        this.guestIpType = guestIpType;
        this.useLocalStorage = useLocalStorage;
    }

	public boolean getOfferHA() {
	    return offerHA;
	}
	
	public void setOfferHA(boolean offerHA) {
		this.offerHA = offerHA;
	}
	
	public long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getCpu() {
	    return cpu;
	}
	
	public void setCpu(int cpu) {
		this.cpu = cpu;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void setRamSize(int ramSize) {
		this.ramSize = ramSize;
	}

	public int getSpeed() {
	    return speed;
	}
	
	public int getRamSize() {
	    return ramSize;
	}
	
	public Date getCreated() {
		return created;
	}
	
	public Date getRemoved() {
		return removed;
	}

	public void setMirroredVolumes(boolean mirroredVolumes) {
		this.mirroredVolumes = mirroredVolumes;
	}

	public boolean isMirroredVolumes() {
		return mirroredVolumes;
	}
	
	public String getDisplayText() {
		return displayText;
	}

	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	public void setRateMbps(int rateMbps) {
		this.rateMbps = rateMbps;
	}

	public int getRateMbps() {
		return rateMbps;
	}

	public void setMulticastRateMbps(int multicastRateMbps) {
		this.multicastRateMbps = multicastRateMbps;
	}

	public int getMulticastRateMbps() {
		return multicastRateMbps;
	}

	public void setGuestIpType(Vlan.VlanType guestIpType) {
		this.guestIpType = guestIpType;
	}

	public Vlan.VlanType getGuestIpType() {
		return guestIpType;
	}
	
	public boolean getUseLocalStorage() {
		return useLocalStorage;
	}
}
