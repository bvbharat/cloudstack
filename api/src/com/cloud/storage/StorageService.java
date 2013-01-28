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
package com.cloud.storage;

import java.net.UnknownHostException;

import org.apache.cloudstack.api.command.admin.storage.*;
import org.apache.cloudstack.api.command.admin.storage.CancelPrimaryStorageMaintenanceCmd;
import org.apache.cloudstack.api.command.admin.storage.UpdateStoragePoolCmd;
import org.apache.cloudstack.api.command.user.volume.CreateVolumeCmd;
import org.apache.cloudstack.api.command.user.volume.UploadVolumeCmd;
import org.apache.cloudstack.api.command.user.volume.ResizeVolumeCmd;
import com.cloud.exception.ConcurrentOperationException;
import com.cloud.exception.InsufficientCapacityException;
import com.cloud.exception.PermissionDeniedException;
import com.cloud.exception.ResourceAllocationException;
import com.cloud.exception.ResourceInUseException;
import com.cloud.exception.ResourceUnavailableException;
import com.cloud.user.Account;

public interface StorageService{
    /**
     * Create StoragePool based on uri
     *
     * @param cmd
     *            the command object that specifies the zone, cluster/pod, URI, details, etc. to use to create the
     *            storage pool.
     * @return
     * @throws ResourceInUseException
     * @throws IllegalArgumentException
     * @throws UnknownHostException
     * @throws ResourceUnavailableException
     *             TODO
     */
    StoragePool createPool(CreateStoragePoolCmd cmd) throws ResourceInUseException, IllegalArgumentException,
    UnknownHostException, ResourceUnavailableException;

    /**
     * Creates the database object for a volume based on the given criteria
     *
     * @param cmd
     *            the API command wrapping the criteria (account/domainId [admin only], zone, diskOffering, snapshot,
     *            name)
     * @return the volume object
     * @throws PermissionDeniedException
     */
    Volume allocVolume(CreateVolumeCmd cmd) throws ResourceAllocationException;

    /**
     * Creates the volume based on the given criteria
     *
     * @param cmd
     *            the API command wrapping the criteria (account/domainId [admin only], zone, diskOffering, snapshot,
     *            name)
     * @return the volume object
     */
    Volume createVolume(CreateVolumeCmd cmd);


    /**
     * Resizes the volume based on the given criteria
     * 
     * @param cmd
     *            the API command wrapping the criteria
     * @return the volume object
     */
    Volume resizeVolume(ResizeVolumeCmd cmd);

    /**
     * Delete the storage pool
     *
     * @param cmd
     *            - the command specifying poolId
     * @return success or failure
     */
    boolean deletePool(DeletePoolCmd cmd);

    /**
     * Enable maintenance for primary storage
     *
     * @param cmd
     *            - the command specifying primaryStorageId
     * @return the primary storage pool
     * @throws ResourceUnavailableException
     *             TODO
     * @throws InsufficientCapacityException
     *             TODO
     */
    public StoragePool preparePrimaryStorageForMaintenance(Long primaryStorageId) throws ResourceUnavailableException,
    InsufficientCapacityException;

    /**
     * Complete maintenance for primary storage
     *
     * @param cmd
     *            - the command specifying primaryStorageId
     * @return the primary storage pool
     * @throws ResourceUnavailableException
     *             TODO
     */
    public StoragePool cancelPrimaryStorageForMaintenance(CancelPrimaryStorageMaintenanceCmd cmd)
            throws ResourceUnavailableException;

    public StoragePool updateStoragePool(UpdateStoragePoolCmd cmd) throws IllegalArgumentException;

    public StoragePool getStoragePool(long id);

    Volume migrateVolume(Long volumeId, Long storagePoolId) throws ConcurrentOperationException;


    /**
     * Uploads the volume to secondary storage
     *
     * @param UploadVolumeCmd cmd
     *
     * @return Volume object
     */
    Volume uploadVolume(UploadVolumeCmd cmd)	throws ResourceAllocationException;

    boolean deleteVolume(long volumeId, Account caller) throws ConcurrentOperationException;

}