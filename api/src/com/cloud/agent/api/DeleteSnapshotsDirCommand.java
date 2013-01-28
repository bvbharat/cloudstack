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

/**
 * This command encapsulates a primitive operation which enables coalescing the backed up VHD snapshots on the secondary server
 * This currently assumes that the secondary storage are mounted on the XenServer.
 */
public class DeleteSnapshotsDirCommand extends Command {
    String secondaryStorageUrl;
    Long dcId;
    Long accountId;
    Long volumeId;

    protected DeleteSnapshotsDirCommand() {

    }

    public DeleteSnapshotsDirCommand(String secondaryStorageUrl,
 Long dcId, Long accountId, Long volumeId)
    {
        this.secondaryStorageUrl = secondaryStorageUrl;
        this.dcId = dcId;
        this.accountId = accountId;
        this.volumeId = volumeId;
    }

    @Override
    public boolean executeInSequence() {
        return true;
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

    public Long getVolumeId() {
        return volumeId;
    }

}
