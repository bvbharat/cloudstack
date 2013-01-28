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
 * This currently assumes that both primary and secondary storage are mounted on the XenServer.
 */
public class downloadSnapshotFromSwiftCommand extends SnapshotCommand {
    @LogLevel(Log4jLevel.Off)
    private SwiftTO _swift;

    private String _parent;

    protected downloadSnapshotFromSwiftCommand() {

    }

    public downloadSnapshotFromSwiftCommand(SwiftTO swift, String secondaryStorageUrl, Long dcId, Long accountId, Long volumeId, String parent, String BackupUuid, int wait) {

        super(null, secondaryStorageUrl, BackupUuid, "", dcId, accountId, volumeId);
        setParent(parent);
        setSwift(swift);
        setWait(wait);
    }


    public SwiftTO getSwift() {
        return this._swift;
    }

    public void setSwift(SwiftTO swift) {
        this._swift = swift;
    }

    public String getParent() {
        return _parent;
    }

    public void setParent(String parent) {
        this._parent = parent;
    }

}
