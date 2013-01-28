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
package com.cloud.migration;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.cloud.utils.db.GenericDao;
import org.apache.cloudstack.api.InternalIdentity;

@Entity
@Table(name="disk_offering")
public class DiskOffering20VO implements InternalIdentity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    Long id;

    @Column(name="domain_id")
    long domainId;

    @Column(name="name")
    private String name = null;

    @Column(name="display_text")
    private String displayText = null;

    @Column(name="disk_size")
    long diskSize;

    @Column(name="mirrored")
    boolean mirrored;

    @Column(name=GenericDao.REMOVED_COLUMN)
    private Date removed;

    public DiskOffering20VO() {
    }

    public DiskOffering20VO(long domainId, String name, String displayText, long diskSize, boolean mirrored) {
        this.domainId = domainId;
        this.name = name;
        this.displayText = displayText;
        this.diskSize = diskSize;
        this.mirrored = mirrored;
    }

    public long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public long getDomainId() {
        return domainId;
    }
    public void setDomainId(long domainId) {
        this.domainId = domainId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayText() {
        return displayText;
    }
    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public long getDiskSize() {
        return diskSize;
    }
    public void setDiskSize(long diskSize) {
        this.diskSize = diskSize;
    }

    public boolean getMirrored() {
        return mirrored;
    }
    public void setMirrored(boolean mirrored) {
        this.mirrored = mirrored;
    }

    public Date getRemoved() {
        return removed;
    }
}
