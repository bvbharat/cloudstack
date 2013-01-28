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

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.cloudstack.api.Identity;
import com.cloud.hypervisor.Hypervisor.HypervisorType;
import com.cloud.utils.db.GenericDao;
import com.google.gson.annotations.Expose;
import org.apache.cloudstack.api.InternalIdentity;

@Entity
@Table(name="snapshots")
public class SnapshotVO implements Snapshot {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id = -1;

    @Column(name="data_center_id")
    long dataCenterId;

    @Column(name="account_id")
    long accountId;

    @Column(name="domain_id")
    long domainId;

    @Column(name="volume_id")
    Long volumeId;

    @Column(name="disk_offering_id")
    Long diskOfferingId;

    @Expose
    @Column(name="path")
    String path;

    @Expose
    @Column(name="name")
    String name;

    @Expose
    @Column(name="status", updatable = true, nullable=false)
    @Enumerated(value=EnumType.STRING)
    private Status status;

    @Column(name="snapshot_type")
    short snapshotType;

    @Column(name="type_description")
    String typeDescription;

    @Column(name="size")
    long size;

    @Column(name=GenericDao.CREATED_COLUMN)
    Date created;

    @Column(name=GenericDao.REMOVED_COLUMN)
    Date removed;

    @Column(name="backup_snap_id")
    String backupSnapshotId;

    @Column(name="swift_id")
    Long swiftId;

    @Column(name="s3_id")
    Long s3Id;

    @Column(name="sechost_id")
    Long secHostId;

    @Column(name="prev_snap_id")
    long prevSnapshotId;

    @Column(name="hypervisor_type")
    @Enumerated(value=EnumType.STRING)
    HypervisorType  hypervisorType;

    @Expose
    @Column(name="version")
    String version;

    @Column(name="uuid")
    String uuid;

    public SnapshotVO() {
    	this.uuid = UUID.randomUUID().toString();
    }

    public SnapshotVO(long dcId, long accountId, long domainId, Long volumeId, Long diskOfferingId, String path, String name, short snapshotType, String typeDescription, long size, HypervisorType hypervisorType ) {
        this.dataCenterId = dcId;
        this.accountId = accountId;
        this.domainId = domainId;
        this.volumeId = volumeId;
        this.diskOfferingId = diskOfferingId;
        this.path = path;
        this.name = name;
        this.snapshotType = snapshotType;
        this.typeDescription = typeDescription;
        this.size = size;
        this.status = Status.Creating;
        this.prevSnapshotId = 0;
        this.hypervisorType = hypervisorType;
        this.version = "2.2";
    	this.uuid = UUID.randomUUID().toString();
    }

    @Override
    public long getId() {
        return id;
    }

    public long getDataCenterId() {
        return dataCenterId;
    }

    @Override
    public long getAccountId() {
        return accountId;
    }

    public long getDomainId() {
        return domainId;
    }

    @Override
    public long getVolumeId() {
        return volumeId;
    }

    public long getDiskOfferingId() {
        return diskOfferingId;
    }

    public void setVolumeId(Long volumeId) {
        this.volumeId = volumeId;
    }

    @Override
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
    	this.path = path;
    }

    @Override
    public String getName() {
        return name;
    }
    @Override
    public short getsnapshotType() {
        return snapshotType;
    }

    @Override
    public Type getType() {
        if (snapshotType < 0 || snapshotType >= Type.values().length) {
            return null;
        }
        return Type.values()[snapshotType];
    }

    public Long getSwiftId() {
        return swiftId;
    }

    public void setSwiftId(Long swiftId) {
        this.swiftId = swiftId;
    }

    public Long getSecHostId() {
        return secHostId;
    }

    public void setSecHostId(Long secHostId) {
        this.secHostId = secHostId;
    }

    @Override
    public HypervisorType getHypervisorType() {
    	return hypervisorType;
    }

    public void setSnapshotType(short snapshotType) {
        this.snapshotType = snapshotType;
    }

    @Override
    public boolean isRecursive(){
        if ( snapshotType >= Type.HOURLY.ordinal() && snapshotType <= Type.MONTHLY.ordinal() ) {
            return true;
        }
        return false;
    }

    public long getSize() {
        return size;
    }

    public String getTypeDescription() {
        return typeDescription;
    }
    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getCreated() {
        return created;
    }

    public Date getRemoved() {
        return removed;
    }

	@Override
    public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getBackupSnapshotId(){
		return backupSnapshotId;
	}

    public long getPrevSnapshotId(){
		return prevSnapshotId;
	}

	public void setBackupSnapshotId(String backUpSnapshotId){
		this.backupSnapshotId = backUpSnapshotId;
	}

	public void setPrevSnapshotId(long prevSnapshotId){
		this.prevSnapshotId = prevSnapshotId;
	}

    public static Type getSnapshotType(String snapshotType) {
        for ( Type type : Type.values()) {
            if ( type.equals(snapshotType)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String getUuid() {
    	return this.uuid;
    }

    public void setUuid(String uuid) {
    	this.uuid = uuid;
    }

    public Long getS3Id() {
        return s3Id;
    }

    public void setS3Id(Long s3Id) {
        this.s3Id = s3Id;
    }

}
