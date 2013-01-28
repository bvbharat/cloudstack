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
import com.cloud.agent.api.to.S3TO;
import com.cloud.agent.api.to.SwiftTO;

/**
 * This command encapsulates a primitive operation which enables coalescing the backed up VHD snapshots on the secondary server
 * This currently assumes that the secondary storage are mounted on the XenServer.
 */
public class DeleteSnapshotBackupCommand extends SnapshotCommand {
    @LogLevel(Log4jLevel.Off)
    private SwiftTO swift;
    private S3TO s3;
    private Boolean all;

    public SwiftTO getSwift() {
        return swift;
    }

    public Boolean isAll() {
        return all;
    }

    public void setAll(Boolean all) {
        this.all = all;
    }

    public void setSwift(SwiftTO swift) {
        this.swift = swift;
    }

    public S3TO getS3() {
        return s3;
    }

    protected DeleteSnapshotBackupCommand() {
    }

    /**
     * Given 2 VHD files on the secondary storage which are linked in a parent chain as follows:
     * backupUUID = parent(childUUID)
     * It gets another VHD
     * previousBackupVHD = parent(backupUUID)
     *
     * And
     * 1) it coalesces backupUuid into its parent.
     * 2) It deletes the VHD file corresponding to backupUuid
     * 3) It sets the parent VHD of childUUID to that of previousBackupUuid
     *
     * It takes care of the cases when
     * 1) childUUID is null. - Step 3 is not done.
     * 2) previousBackupUUID is null
     *       - Merge childUUID into its parent backupUUID
     *       - Set the UUID of the resultant VHD to childUUID
     *       - Essentially we are deleting the oldest VHD file and setting the current oldest VHD to childUUID
     *
     * @param volumeName                  The name of the volume whose snapshot was taken (something like i-3-SV-ROOT)
     * @param secondaryStoragePoolURL    This is what shows up in the UI when you click on Secondary storage.
     *                                    In the code, it is present as: In the vmops.host_details table, there is a field mount.parent. This is the value of that field
     *                                    If you have better ideas on how to get it, you are welcome.
     * @param backupUUID                  The VHD which has to be deleted
     * @param childUUID                   The child VHD file of the backup whose parent is reset to its grandparent.
     */
    public DeleteSnapshotBackupCommand(SwiftTO swift,
                                       S3TO s3,
                                       String secondaryStoragePoolURL,
                                       Long   dcId,
                                       Long   accountId,
                                       Long   volumeId,
 String backupUUID, Boolean all)
    {
        super(null, secondaryStoragePoolURL, backupUUID, null, dcId, accountId, volumeId);
        setSwift(swift);
        this.s3 = s3;
        setAll(all);
    }
}
