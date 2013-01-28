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
package com.cloud.storage.secondary;

import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.ejb.Local;
import javax.naming.ConfigurationException;

import com.cloud.vm.SecondaryStorageVmVO;

@Local(value={SecondaryStorageVmAllocator.class})
public class SecondaryStorageVmDefaultAllocator implements SecondaryStorageVmAllocator {
	
    private String _name;
    private Random _rand = new Random(System.currentTimeMillis());

    @Override
	public SecondaryStorageVmVO allocSecondaryStorageVm(List<SecondaryStorageVmVO> candidates, Map<Long, Integer> loadInfo, long dataCenterId) {
    	if(candidates.size() > 0)
			return candidates.get(_rand.nextInt(candidates.size()));
    	return null;
    }

    @Override
    public boolean configure(String name, Map<String, Object> params) throws ConfigurationException {
      /*  _name = name;
        ComponentLocator locator = ComponentLocator.getCurrentLocator();
        ConfigurationDao configDao = locator.getDao(ConfigurationDao.class);
        if (configDao == null) {
            throw new ConfigurationException("Unable to get the configuration dao.");
        }
               */
        
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
