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
package com.cloud.agent.api.storage;

import com.cloud.agent.api.Command;
import com.cloud.agent.api.to.StorageFilerTO;
import com.cloud.storage.StoragePool;
import com.cloud.vm.DiskProfile;

public class CreateCommand extends Command {
    private long volId;
    private StorageFilerTO pool;
    private DiskProfile diskCharacteristics;
    private String templateUrl;

    protected CreateCommand() {
        super();
    }

    /**
     * Construction for template based volumes.
     *
     * @param vol
     * @param vm
     * @param diskCharacteristics
     * @param templateUrl
     * @param pool
     */
    public CreateCommand(DiskProfile diskCharacteristics, String templateUrl, StorageFilerTO pool) {
        this(diskCharacteristics, pool);
        this.templateUrl = templateUrl;
    }

    /**
     * Construction for regular volumes.
     *
     * @param vol
     * @param vm
     * @param diskCharacteristics
     * @param pool
     */
    public CreateCommand(DiskProfile diskCharacteristics, StorageFilerTO pool) {
        this.volId = diskCharacteristics.getVolumeId();
        this.diskCharacteristics = diskCharacteristics;
        this.pool = pool;
        this.templateUrl = null;
    }

    public CreateCommand(DiskProfile diskCharacteristics, String templateUrl, StoragePool pool) {
        this(diskCharacteristics, templateUrl, new StorageFilerTO(pool));
    }

    public CreateCommand(DiskProfile diskCharacteristics, StoragePool pool) {
        this(diskCharacteristics, new StorageFilerTO(pool));
    }

    @Override
    public boolean executeInSequence() {
        return true;
    }

    public String getTemplateUrl() {
        return templateUrl;
    }

    public StorageFilerTO getPool() {
        return pool;
    }

    public DiskProfile getDiskCharacteristics() {
        return diskCharacteristics;
    }

    public long getVolumeId() {
        return volId;
    }

    @Deprecated
    public String getInstanceName() {
        return null;
    }
}
