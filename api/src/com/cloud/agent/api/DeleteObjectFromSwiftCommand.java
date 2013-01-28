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

public class DeleteObjectFromSwiftCommand extends Command {
    @LogLevel(Log4jLevel.Off)
    private SwiftTO swift;
    private String container;
    private String object;

    protected DeleteObjectFromSwiftCommand() {

    }

    public DeleteObjectFromSwiftCommand(SwiftTO swift, String container, String object) {
        this.swift = swift;
        this.container = container;
        this.object = object;
    }

    public SwiftTO getSwift() {
        return this.swift;
    }

    public String getContainer() {
        return container;
    }

    public String getObject() {
        return object;
    }

    @Override
    public boolean executeInSequence() {
        // TODO Auto-generated method stub
        return true;
    }

}
