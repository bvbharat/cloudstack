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
package org.apache.cloudstack.api.command.admin.systemvm;

import org.apache.cloudstack.api.*;
import org.apache.cloudstack.api.command.user.vm.UpgradeVMCmd;
import org.apache.cloudstack.api.response.DiskOfferingResponse;
import org.apache.log4j.Logger;

import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.response.SystemVmResponse;
import com.cloud.exception.InvalidParameterValueException;
import com.cloud.offering.ServiceOffering;
import com.cloud.user.Account;
import com.cloud.user.UserContext;
import com.cloud.vm.VirtualMachine;

@APICommand(name = "changeServiceForSystemVm", responseObject=SystemVmResponse.class, description="Changes the service offering for a system vm (console proxy or secondary storage). " +
                                                                                            "The system vm must be in a \"Stopped\" state for " +
                                                                                            "this command to take effect.")
public class UpgradeSystemVMCmd extends BaseCmd {
    public static final Logger s_logger = Logger.getLogger(UpgradeVMCmd.class.getName());
    private static final String s_name = "changeserviceforsystemvmresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @Parameter(name=ApiConstants.ID, type=CommandType.UUID, entityType=SystemVmResponse.class,
            required=true, description="The ID of the system vm")
    private Long id;

    @Parameter(name=ApiConstants.SERVICE_OFFERING_ID, type=CommandType.UUID, entityType=DiskOfferingResponse.class,
            required=true, description="the service offering ID to apply to the system vm")
    private Long serviceOfferingId;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public Long getId() {
        return id;
    }

    public Long getServiceOfferingId() {
        return serviceOfferingId;
    }

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////

    @Override
    public String getCommandName() {
        return s_name;
    }

    @Override
    public long getEntityOwnerId() {
        Account account = UserContext.current().getCaller();
        if (account != null) {
            return account.getId();
        }

        return Account.ACCOUNT_ID_SYSTEM; // no account info given, parent this command to SYSTEM so ERROR events are tracked
    }

    @Override
    public void execute(){
        UserContext.current().setEventDetails("Vm Id: "+getId());

        ServiceOffering serviceOffering = _configService.getServiceOffering(serviceOfferingId);
        if (serviceOffering == null) {
            throw new InvalidParameterValueException("Unable to find service offering: " + serviceOfferingId);
        }

        VirtualMachine result = _mgr.upgradeSystemVM(this);
        if (result != null) {
            SystemVmResponse response = _responseGenerator.createSystemVmResponse(result);
            response.setResponseName(getCommandName());
            this.setResponseObject(response);
        } else {
            throw new ServerApiException(ApiErrorCode.INTERNAL_ERROR, "Fail to reboot system vm");
        }
    }
}
