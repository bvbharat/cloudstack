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
package com.cloud.uuididentity;

import java.util.Map;

import javax.ejb.Local;
import javax.naming.ConfigurationException;

import org.apache.cloudstack.api.IdentityService;
import com.cloud.utils.component.Inject;
import com.cloud.utils.component.Manager;
import com.cloud.uuididentity.dao.IdentityDao;

@Local(value = { IdentityService.class })
public class IdentityServiceImpl implements Manager, IdentityService {
    private String _name;
	
	@Inject	private IdentityDao _identityDao;
	
    public Long getIdentityId(String tableName, String identityString) {
		return _identityDao.getIdentityId(tableName, identityString);
    }
	
	public String getIdentityUuid(String tableName, String identityString) {
		return _identityDao.getIdentityUuid(tableName, identityString);
	}

	@Override
	public boolean configure(String name, Map<String, Object> params)
			throws ConfigurationException {
		_name = name;
		
		return true;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public boolean start() {
		return true;
	}

	@Override
	public boolean stop() {
		return true;
	}
}
