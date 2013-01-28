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
package com.cloud.bridge.persist.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ejb.Local;

import org.apache.log4j.Logger;

import com.cloud.bridge.model.CloudStackConfigurationVO;
import com.cloud.utils.component.ComponentLocator;
import com.cloud.utils.db.DB;
import com.cloud.utils.db.GenericDaoBase;
import com.cloud.utils.db.SearchBuilder;
import com.cloud.utils.db.SearchCriteria;
import com.cloud.utils.db.Transaction;


@Local(value={CloudStackConfigurationDao.class})
public class CloudStackConfigurationDaoImpl extends GenericDaoBase<CloudStackConfigurationVO, String> implements CloudStackConfigurationDao {
	private static final Logger s_logger = Logger.getLogger(CloudStackConfigurationDaoImpl.class);
	
	final SearchBuilder<CloudStackConfigurationVO> NameSearch= createSearchBuilder();
	
	public CloudStackConfigurationDaoImpl() { }
	
	
	@Override
	@DB
	public String getConfigValue(String name) {
        NameSearch.and("name", NameSearch.entity().getName(), SearchCriteria.Op.EQ);
        Transaction txn = Transaction.currentTxn();
		try {
			txn.start();
			SearchCriteria<CloudStackConfigurationVO> sc = NameSearch.create();
			sc.setParameters("name", name);
			CloudStackConfigurationVO configItem = findOneBy(sc);
			if (configItem == null) {
				s_logger.warn("No configuration item found with name " + name);
				return null;
			}
			return configItem.getValue();
        }finally {
		
		}
	}
	
}
