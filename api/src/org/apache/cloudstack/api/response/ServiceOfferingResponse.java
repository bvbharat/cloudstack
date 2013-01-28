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
package org.apache.cloudstack.api.response;

import java.util.Date;

import com.cloud.offering.ServiceOffering;
import org.apache.cloudstack.api.ApiConstants;
import com.cloud.serializer.Param;
import com.google.gson.annotations.SerializedName;
import org.apache.cloudstack.api.BaseResponse;
import org.apache.cloudstack.api.EntityReference;

@EntityReference(value = ServiceOffering.class)
public class ServiceOfferingResponse extends BaseResponse {
    @SerializedName("id") @Param(description="the id of the service offering")
    private String id;

    @SerializedName("name") @Param(description="the name of the service offering")
    private String name;

    @SerializedName("displaytext") @Param(description="an alternate display text of the service offering.")
    private String displayText;

    @SerializedName("cpunumber") @Param(description="the number of CPU")
    private int cpuNumber;

    @SerializedName("cpuspeed") @Param(description="the clock rate CPU speed in Mhz")
    private int cpuSpeed;

    @SerializedName("memory") @Param(description="the memory in MB")
    private int memory;

    @SerializedName("created") @Param(description="the date this service offering was created")
    private Date created;

    @SerializedName("storagetype") @Param(description="the storage type for this service offering")
    private String storageType;

    @SerializedName("offerha") @Param(description="the ha support in the service offering")
    private Boolean offerHa;

    @SerializedName("limitcpuuse") @Param(description="restrict the CPU usage to committed service offering")
    private Boolean limitCpuUse;

    @SerializedName("tags") @Param(description="the tags for the service offering")
    private String tags;

    @SerializedName("domainid") @Param(description="the domain id of the service offering")
    private String domainId;

    @SerializedName(ApiConstants.DOMAIN) @Param(description="Domain name for the offering")
    private String domain;

    @SerializedName(ApiConstants.HOST_TAGS) @Param(description="the host tag for the service offering")
    private String hostTag;

    @SerializedName(ApiConstants.IS_SYSTEM_OFFERING) @Param(description="is this a system vm offering")
    private Boolean isSystem;

    @SerializedName(ApiConstants.IS_DEFAULT_USE) @Param(description="is this a  default system vm offering")
    private Boolean defaultUse;

    @SerializedName(ApiConstants.SYSTEM_VM_TYPE) @Param(description="is this a the systemvm type for system vm offering")
    private String vm_type;

    @SerializedName(ApiConstants.NETWORKRATE) @Param(description="data transfer rate in megabits per second allowed.")
    private Integer networkRate;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystemOffering(Boolean isSystem) {
        this.isSystem = isSystem;
    }


    public Boolean getDefaultUse() {
        return defaultUse;
    }

    public void setDefaultUse(Boolean defaultUse) {
        this.defaultUse = defaultUse;
    }


    public String getSystemVmType() {
        return vm_type;
    }

    public void setSystemVmType(String vmtype) {
        this.vm_type = vmtype;
    }


    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public int getCpuNumber() {
        return cpuNumber;
    }

    public void setCpuNumber(int cpuNumber) {
        this.cpuNumber = cpuNumber;
    }

    public int getCpuSpeed() {
        return cpuSpeed;
    }

    public void setCpuSpeed(int cpuSpeed) {
        this.cpuSpeed = cpuSpeed;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public Boolean getOfferHa() {
        return offerHa;
    }

    public void setOfferHa(Boolean offerHa) {
        this.offerHa = offerHa;
    }

    public Boolean getLimitCpuUse() {
        return limitCpuUse;
    }

    public void setLimitCpuUse(Boolean limitCpuUse) {
        this.limitCpuUse = limitCpuUse;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getHostTag() {
        return hostTag;
    }

    public void setHostTag(String hostTag) {
        this.hostTag = hostTag;
    }

    public void setNetworkRate(Integer networkRate) {
        this.networkRate = networkRate;
    }
}
