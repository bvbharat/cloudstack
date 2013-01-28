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
package com.cloud.configuration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.cloud.utils.crypt.DBEncryptionUtil;

@Entity
@Table(name="configuration")
public class ConfigurationVO implements Configuration{
	@Column(name="instance")
    private String instance;

	@Column(name="component")
	private String component;
	
	@Id
	@Column(name="name")
    private String name;
	
	@Column(name="value", length=4095)
    private String value;
	
	@Column(name="description", length=1024)
    private String description;
	
	@Column(name="category")
	private String category;
	
	protected ConfigurationVO() {}
	
	public ConfigurationVO(String category, String instance, String component, String name, String value, String description) {
		this.category = category;
		this.instance = instance;
		this.component = component;
		this.name = name;
		this.value = value;
		this.description = description;
	}

	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getInstance() {
		return instance;
	}
	
	public void setInstance(String instance) {
		this.instance = instance;
	}

	public String getComponent() {
	    return component;
	}
	
	public void setComponent(String component) {
		this.component = component;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return (("Hidden".equals(getCategory()) || "Secure".equals(getCategory())) ? DBEncryptionUtil.decrypt(value) : value);
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
}
