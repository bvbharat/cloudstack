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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.cloud.utils.db.GenericDao;
import org.apache.cloudstack.api.InternalIdentity;

@Entity
@Table(name="disk_offering_21")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="type", discriminatorType=DiscriminatorType.STRING, length=32)
public class DiskOffering21VO implements InternalIdentity {
    public enum Type {
        Disk,
        Service
    };
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id")
    long id;

    @Column(name="domain_id")
    Long domainId;

    @Column(name="unique_name")
    private String uniqueName;
    
    @Column(name="name")
    private String name = null;

    @Column(name="display_text")
    private String displayText = null;

    @Column(name="disk_size")
    long diskSize;

    @Column(name="mirrored")
    boolean mirrored;
    
    @Column(name="tags")
    String tags;
    
    @Column(name="type")
    Type type;
    
    @Column(name=GenericDao.REMOVED_COLUMN)
    private Date removed;

    @Column(name=GenericDao.CREATED_COLUMN)
    private Date created;
    
    @Column(name="recreatable")
    private boolean recreatable;
    
    @Column(name="use_local_storage")
    private boolean useLocalStorage;

    @Column(name="system_use")
    protected boolean systemUse;
    
    
    public DiskOffering21VO() {
    }

    public DiskOffering21VO(long domainId, String name, String displayText, long diskSize, boolean mirrored, String tags) {
        this.domainId = domainId;
        this.name = name;
        this.displayText = displayText;
        this.diskSize = diskSize;
        this.mirrored = mirrored;
        this.tags = tags;
        this.recreatable = false;
        this.type = Type.Disk;
        this.useLocalStorage = false;
    }
    
    public DiskOffering21VO(String name, String displayText, boolean mirrored, String tags, boolean recreatable, boolean useLocalStorage) {
        this.domainId = null;
        this.type = Type.Service;
        this.name = name;
        this.displayText = displayText;
        this.mirrored = mirrored;
        this.tags = tags;
        this.recreatable = recreatable;
        this.useLocalStorage = useLocalStorage;
    }

    public long getId() {
        return id;
    }
    
    public void setId(Long id) {
    	this.id = id;
    }
    
    public String getUniqueName() {
        return uniqueName;
    }

    public boolean getSystemUse() {
        return systemUse;
    }
    
    public void setSystemUse(boolean systemUse) {
        this.systemUse = systemUse;
    }
    
    public boolean getUseLocalStorage() {
        return useLocalStorage;
    }
    
    public void setUserLocalStorage(boolean useLocalStorage) {
    	this.useLocalStorage = useLocalStorage; 
    }
    
    public Long getDomainId() {
        return domainId;
    }
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
    	this.type = type;
    }
    
    public boolean isRecreatable() {
        return recreatable;
    }
    
    public void setDomainId(Long domainId) {
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

    public long getDiskSizeInBytes() {
        return diskSize * 1024 * 1024;
    }
    
    public void setDiskSize(long diskSize) {
        this.diskSize = diskSize;
    }

    public boolean isMirrored() {
        return mirrored;
    }
    public void setMirrored(boolean mirrored) {
        this.mirrored = mirrored;
    }

    public Date getRemoved() {
        return removed;
    }
    
	public Date getCreated() {
		return created;
	}
	
    protected void setTags(String tags) {
        this.tags = tags;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setUniqueName(String name) {
        this.uniqueName = name;
    }

    @Transient
    public String[] getTagsArray() {
        String tags = getTags();
        if (tags == null || tags.isEmpty()) {
            return new String[0];
        }
        
        return tags.split(",");
    }

    @Transient
    public boolean containsTag(String... tags) {
        if (this.tags == null) {
            return false;
        }
        
        for (String tag : tags) {
            if (!this.tags.matches(tag)) {
                return false;
            }
        }
        
        return true;
    }
    
    @Transient
    public void setTagsArray(List<String> newTags) {
        if (newTags.isEmpty()) {
            setTags(null);
            return;
        }
        
        StringBuilder buf = new StringBuilder();
        for (String tag : newTags) {
            buf.append(tag).append(",");
        }
        
        buf.delete(buf.length() - 1, buf.length());
        
        setTags(buf.toString());
    }
}
