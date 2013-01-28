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
import org.apache.log4j.Logger;

import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.response.SystemVmResponse;
import com.cloud.async.AsyncJob;
import com.cloud.event.EventTypes;
import com.cloud.exception.ConcurrentOperationException;
import com.cloud.exception.ResourceUnavailableException;
import com.cloud.user.Account;
import com.cloud.user.UserContext;
import com.cloud.vm.VirtualMachine;

@APICommand(name = "stopSystemVm", description="Stops a system VM.", responseObject=SystemVmResponse.class)
public class StopSystemVmCmd extends BaseAsyncCmd {
    public static final Logger s_logger = Logger.getLogger(StopSystemVmCmd.class.getName());

    private static final String s_name = "stopsystemvmresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @Parameter(name=ApiConstants.ID, type=CommandType.UUID, entityType=SystemVmResponse.class,
            required=true, description="The ID of the system virtual machine")
    private Long id;

    @Parameter(name=ApiConstants.FORCED, type=CommandType.BOOLEAN, required=false, description="Force stop the VM.  The caller knows the VM is stopped.")
    private Boolean forced;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public Long getId() {
        return id;
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
    public String getEventType() {
        VirtualMachine.Type type = _mgr.findSystemVMTypeById(getId());
        if(type == VirtualMachine.Type.ConsoleProxy){
            return EventTypes.EVENT_PROXY_STOP;
        }
        else{
            return EventTypes.EVENT_SSVM_STOP;
        }
    }

    @Override
    public String getEventDescription() {
        return  "stopping system vm: " + getId();
    }

    @Override
    public AsyncJob.Type getInstanceType() {
        return AsyncJob.Type.SystemVm;
    }

    @Override
    public Long getInstanceId() {
        return getId();
    }

    public boolean isForced() {
        return (forced != null) ? forced : false;
    }

    @Override
    public void execute() throws ResourceUnavailableException, ConcurrentOperationException {
        UserContext.current().setEventDetails("Vm Id: "+getId());
        VirtualMachine result = _mgr.stopSystemVM(this);
        if (result != null) {
            SystemVmResponse response = _responseGenerator.createSystemVmResponse(result);
            response.setResponseName(getCommandName());
            this.setResponseObject(response);
        } else {
            throw new ServerApiException(ApiErrorCode.INTERNAL_ERROR, "Fail to stop system vm");
        }
    }
}
