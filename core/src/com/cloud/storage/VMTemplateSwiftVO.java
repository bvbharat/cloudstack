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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.cloud.utils.db.GenericDaoBase;
import org.apache.cloudstack.api.InternalIdentity;

/**
 * Join table for swift and templates
 * 
 * 
 */
@Entity
@Table(name = "template_swift_ref")
public class VMTemplateSwiftVO implements InternalIdentity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "swift_id")
    private long swiftId;

    @Column(name = "template_id")
    private long templateId;

    @Column(name = GenericDaoBase.CREATED_COLUMN)
    private Date created = null;

    @Column(name = "path")
    private String path;

    @Column(name = "size")
    private long size;

    @Column(name = "physical_size")
    private long physicalSize;

    public VMTemplateSwiftVO(long swiftId, long templateId, Date created, String path, long size, long physicalSize) {
        this.swiftId = swiftId;
        this.templateId = templateId;
        this.created = created;
        this.path = path;
        this.size = size;
        this.physicalSize = physicalSize;
    }

    protected VMTemplateSwiftVO() {

    }

    public long getTemplateId() {
        return templateId;
    }

    public long getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public String getPath() {
        return path;
    }

    public long getSwiftId() {
        return swiftId;
    }

    public long getSize() {
        return size;
    }

    public long getPhysicalSize() {
        return physicalSize;
    }

    @Override
    public String toString() {
        return new StringBuilder("TmplSwift[").append(id).append("-").append(templateId).append("-").append(swiftId).append("]").toString();
    }

}
