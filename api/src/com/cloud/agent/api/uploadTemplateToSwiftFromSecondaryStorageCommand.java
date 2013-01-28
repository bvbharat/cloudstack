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
package com.cloud.agent.api;

import com.cloud.agent.api.LogLevel.Log4jLevel;
import com.cloud.agent.api.to.SwiftTO;

/**
 *
 *
 */

public class uploadTemplateToSwiftFromSecondaryStorageCommand extends Command {
    @LogLevel(Log4jLevel.Off)
    private SwiftTO swift;
    private String secondaryStorageUrl;

    private Long dcId;
    private Long accountId;
    private Long templateId;

    protected uploadTemplateToSwiftFromSecondaryStorageCommand() {

    }

    public uploadTemplateToSwiftFromSecondaryStorageCommand(SwiftTO swift, String secondaryStorageUrl, Long dcId, Long accountId, Long templateId, int wait) {

        this.swift = swift;
        this.secondaryStorageUrl = secondaryStorageUrl;
        this.dcId = dcId;
        this.accountId = accountId;
        this.templateId = templateId;
        setWait(wait);
    }

    public SwiftTO getSwift() {
        return this.swift;
    }

    public void setSwift(SwiftTO swift) {
        this.swift = swift;
    }

    public String getSecondaryStorageUrl() {
        return secondaryStorageUrl;
    }

    public Long getDcId() {
        return dcId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    @Override
    public boolean executeInSequence() {
        // TODO Auto-generated method stub
        return true;
    }

}
