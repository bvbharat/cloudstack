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

import java.util.List;

public class CleanupSnapshotBackupCommand extends Command {
        private String secondaryStoragePoolURL;
        private Long   dcId;
        private Long   accountId;
        private Long   volumeId;
        private List<String> validBackupUUIDs;

    protected CleanupSnapshotBackupCommand() {

    }

     /*
     * @param secondaryStoragePoolURL    This is what shows up in the UI when you click on Secondary storage.
     *                                    In the code, it is present as: In the vmops.host_details table, there is a field mount.parent. This is the value of that field
     *                                    If you have better ideas on how to get it, you are welcome.
     * @param validBackupUUID             The VHD which are valid
     */
    public CleanupSnapshotBackupCommand(String secondaryStoragePoolURL,
                                       Long   dcId,
                                       Long   accountId,
                                       Long   volumeId,
                                       List<String> validBackupUUIDs)
    {
        this.secondaryStoragePoolURL = secondaryStoragePoolURL;
        this.dcId = dcId;
        this.accountId = accountId;
        this.volumeId = volumeId;
        this.validBackupUUIDs = validBackupUUIDs;
    }

    public String getSecondaryStoragePoolURL() {
        return secondaryStoragePoolURL;
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

    public List<String> getValidBackupUUIDs() {
        return validBackupUUIDs;
    }

    @Override
    public boolean executeInSequence() {
        return false;
    }
}
