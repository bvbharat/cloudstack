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
package com.cloud.consoleproxy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.ejb.Local;
import javax.naming.ConfigurationException;

import com.cloud.configuration.dao.ConfigurationDao;
import com.cloud.utils.NumbersUtil;
import com.cloud.utils.component.ComponentLocator;
import com.cloud.vm.ConsoleProxyVO;

import edu.emory.mathcs.backport.java.util.Collections;

@Local(value={ConsoleProxyAllocator.class})
public class ConsoleProxyBalanceAllocator implements ConsoleProxyAllocator {
	
    private String _name;
    private final Random _rand = new Random(System.currentTimeMillis());
   
    @Override
	public ConsoleProxyVO allocProxy(List<ConsoleProxyVO> candidates, final Map<Long, Integer> loadInfo, long dataCenterId) {
    	if(candidates != null) {
    		
    		List<ConsoleProxyVO> allocationList = new ArrayList<ConsoleProxyVO>();
    		for(ConsoleProxyVO proxy : candidates) {
				allocationList.add(proxy);
    		}
    		
    		Collections.sort(candidates, new Comparator<ConsoleProxyVO> () {
				@Override
				public int compare(ConsoleProxyVO x, ConsoleProxyVO y) {
					Integer loadOfX = loadInfo.get(x.getId());
					Integer loadOfY = loadInfo.get(y.getId());

					if(loadOfX != null && loadOfY != null) {
						if(loadOfX < loadOfY)
							return -1;
						else if(loadOfX > loadOfY)
							return 1;
						return 0;
					} else if(loadOfX == null && loadOfY == null) {
						return 0;
					} else {
						if(loadOfX == null)
							return -1;
						return 1;
					}
				}
    		});
    		
    		if(allocationList.size() > 0)
    			return allocationList.get(0);
    	}
    	return null;
    }

    @Override
    public boolean configure(String name, Map<String, Object> params) throws ConfigurationException {
        _name = name;
        ComponentLocator locator = ComponentLocator.getCurrentLocator();
        ConfigurationDao configDao = locator.getDao(ConfigurationDao.class);
        if (configDao == null) {
            throw new ConfigurationException("Unable to get the configuration dao.");
        }
        
        Map<String, String> configs = configDao.getConfiguration();
        
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
