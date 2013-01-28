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
package org.apache.cloudstack.api.command.admin.vpc;

import java.util.List;

import org.apache.cloudstack.api.*;
import org.apache.log4j.Logger;

import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.response.VpcOfferingResponse;
import com.cloud.event.EventTypes;
import com.cloud.exception.ResourceAllocationException;
import com.cloud.network.vpc.VpcOffering;
import com.cloud.user.Account;

@APICommand(name = "createVPCOffering", description="Creates VPC offering", responseObject=VpcOfferingResponse.class)
public class CreateVPCOfferingCmd extends BaseAsyncCreateCmd{
    public static final Logger s_logger = Logger.getLogger(CreateVPCOfferingCmd.class.getName());
    private static final String _name = "createvpcofferingresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @Parameter(name=ApiConstants.NAME, type=CommandType.STRING, required=true, description="the name of the vpc offering")
    private String vpcOfferingName;

    @Parameter(name=ApiConstants.DISPLAY_TEXT, type=CommandType.STRING, required=true, description="the display text of " +
            "the vpc offering")
    private String displayText;

    @Parameter(name=ApiConstants.SUPPORTED_SERVICES, type=CommandType.LIST, required=true, collectionType=CommandType.STRING,
            description="services supported by the vpc offering")
    private List<String> supportedServices;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public String getVpcOfferingName() {
        return vpcOfferingName;
    }

    public String getDisplayText() {
        return displayText;
    }

    public List<String> getSupportedServices() {
        return supportedServices;
    }


    @Override
    public void create() throws ResourceAllocationException {
        VpcOffering vpcOff = _vpcService.createVpcOffering(getVpcOfferingName(), getDisplayText(), getSupportedServices());
        if (vpcOff != null) {
            this.setEntityId(vpcOff.getId());
            this.setEntityUuid(vpcOff.getUuid());
        } else {
            throw new ServerApiException(ApiErrorCode.INTERNAL_ERROR, "Failed to create a VPC offering");
        }
    }

    @Override
    public void execute() {
        VpcOffering vpc = _vpcService.getVpcOffering(this.getEntityId());
        if (vpc != null) {
            VpcOfferingResponse response = _responseGenerator.createVpcOfferingResponse(vpc);
            response.setResponseName(getCommandName());
            this.setResponseObject(response);
        } else {
            throw new ServerApiException(ApiErrorCode.INTERNAL_ERROR, "Failed to create VPC offering");
        }
    }


    @Override
    public String getEventType() {
       return EventTypes.EVENT_VPC_OFFERING_CREATE;
    }

    @Override
    public String getEventDescription() {
        return  "creating VPC offering. Id: " + getEntityId();
    }

    @Override
    public String getCommandName() {
        return _name;
    }

    @Override
    public long getEntityOwnerId() {
       return Account.ACCOUNT_ID_SYSTEM;
    }

}
