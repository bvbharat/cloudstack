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

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.cloud.offering.ServiceOffering;

@Entity
@Table(name="service_offering_21")
@DiscriminatorValue(value="Service")
@PrimaryKeyJoinColumn(name="id")
public class ServiceOffering21VO extends DiskOffering21VO implements ServiceOffering {
    @Column(name="cpu")
	private int cpu;

    @Column(name="speed")
    private int speed;

    @Column(name="ram_size")
	private int ramSize;

    @Column(name="nw_rate")
    private Integer rateMbps;

    @Column(name="mc_rate")
    private Integer multicastRateMbps;

    @Column(name="ha_enabled")
    private boolean offerHA;

    @Column(name="host_tag")
    private String hostTag;

    protected ServiceOffering21VO() {
        super();
    }

    public ServiceOffering21VO(String name, int cpu, int ramSize, int speed, Integer rateMbps, Integer multicastRateMbps, boolean offerHA, String displayText, boolean useLocalStorage, boolean recreatable, String tags) {
        super(name, displayText, false, tags, recreatable, useLocalStorage);
        this.cpu = cpu;
        this.ramSize = ramSize;
        this.speed = speed;
        this.rateMbps = rateMbps;
        this.multicastRateMbps = multicastRateMbps;
        this.offerHA = offerHA;
    }

    public ServiceOffering21VO(String name, int cpu, int ramSize, int speed, Integer rateMbps, Integer multicastRateMbps, boolean offerHA, String displayText, boolean useLocalStorage, boolean recreatable, String tags, String hostTag) {
    	this(name, cpu, ramSize, speed, rateMbps, multicastRateMbps, offerHA, displayText, useLocalStorage, recreatable, tags);
       	this.hostTag = hostTag;
    }


	@Override
	public boolean getOfferHA() {
	    return offerHA;
	}

	@Override
	public boolean getLimitCpuUse() {
	    return false;
	}

	public void setOfferHA(boolean offerHA) {
		this.offerHA = offerHA;
	}

	@Override
    @Transient
	public String[] getTagsArray() {
	    String tags = getTags();
	    if (tags == null || tags.length() == 0) {
	        return new String[0];
	    }

	    return tags.split(",");
	}

	@Override
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

	@Override
	public int getSpeed() {
	    return speed;
	}

	@Override
	public int getRamSize() {
	    return ramSize;
	}

	public void setRateMbps(Integer rateMbps) {
		this.rateMbps = rateMbps;
	}

	@Override
    public Integer getRateMbps() {
		return rateMbps;
	}

	public void setMulticastRateMbps(Integer multicastRateMbps) {
		this.multicastRateMbps = multicastRateMbps;
	}

	@Override
    public Integer getMulticastRateMbps() {
		return multicastRateMbps;
	}

	public String gethypervisorType() {
		return null;
	}

	public void setHostTag(String hostTag) {
		this.hostTag = hostTag;
	}

	public String getHostTag() {
		return hostTag;
	}

    @Override
    public boolean getDefaultUse() {
        return false;
    }

    @Override
    public String getSystemVmType() {
        return null;
    }

    @Override
    public String getUuid() {
        // TODO Auto-generated method stub
        return null;
    }


}
