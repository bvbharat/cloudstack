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

import org.apache.cloudstack.api.ApiConstants;
import com.cloud.serializer.Param;
import com.google.gson.annotations.SerializedName;
import org.apache.cloudstack.api.BaseResponse;

@SuppressWarnings("unused")
public class CapabilitiesResponse extends BaseResponse {
    @SerializedName("securitygroupsenabled") @Param(description="true if security groups support is enabled, false otherwise")
    private boolean securityGroupsEnabled;

    @SerializedName("cloudstackversion") @Param(description="version of the cloud stack")
    private String cloudStackVersion;

    @SerializedName("userpublictemplateenabled") @Param(description="true if user and domain admins can set templates to be shared, false otherwise")
    private boolean userPublicTemplateEnabled;

    @SerializedName("supportELB") @Param(description="true if region supports elastic load balancer on basic zones")
    private String supportELB;

    @SerializedName(ApiConstants.PROJECT_INVITE_REQUIRED) @Param(description="If invitation confirmation is required when add account to project")
    private Boolean projectInviteRequired;

    @SerializedName(ApiConstants.ALLOW_USER_CREATE_PROJECTS) @Param(description="true if regular user is allowed to create projects")
    private Boolean allowUsersCreateProjects;

    @SerializedName(ApiConstants.CUSTOM_DISK_OFF_MAX_SIZE) @Param(description="maximum size that can be specified when " +
            "create disk from disk offering with custom size")
    private Long diskOffMaxSize;


    public void setSecurityGroupsEnabled(boolean securityGroupsEnabled) {
        this.securityGroupsEnabled = securityGroupsEnabled;
    }

    public void setCloudStackVersion(String cloudStackVersion) {
        this.cloudStackVersion = cloudStackVersion;
    }

    public void setUserPublicTemplateEnabled(boolean userPublicTemplateEnabled) {
        this.userPublicTemplateEnabled = userPublicTemplateEnabled;
    }

    public void setSupportELB(String supportELB) {
        this.supportELB = supportELB;
    }

    public void setProjectInviteRequired(Boolean projectInviteRequired) {
        this.projectInviteRequired = projectInviteRequired;
    }

    public void setAllowUsersCreateProjects(Boolean allowUsersCreateProjects) {
        this.allowUsersCreateProjects = allowUsersCreateProjects;
    }

    public void setDiskOffMaxSize(Long diskOffMaxSize) {
        this.diskOffMaxSize = diskOffMaxSize;
    }

}
