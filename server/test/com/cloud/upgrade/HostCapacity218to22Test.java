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
package com.cloud.upgrade;

import java.sql.SQLException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;

import com.cloud.upgrade.dao.VersionDaoImpl;
import com.cloud.utils.component.ComponentLocator;
import com.cloud.utils.db.DbTestUtils;

public class HostCapacity218to22Test extends TestCase {
    private static final Logger s_logger = Logger.getLogger(HostCapacity218to22Test.class);
	
    @Override
    @Before
    public void setUp() throws Exception {
        DbTestUtils.executeScript("cleanup.sql", false, true);
    }
    
    @Override
    @After
    public void tearDown() throws Exception {
    }

    public void test218to22Upgrade() throws SQLException {
        s_logger.debug("Finding sample data from 2.1.8");
        DbTestUtils.executeScript("fake.sql", false, true);
        
        VersionDaoImpl dao = ComponentLocator.inject(VersionDaoImpl.class);
        DatabaseUpgradeChecker checker = ComponentLocator.inject(DatabaseUpgradeChecker.class);
        
        String version = dao.getCurrentVersion();
        
        if (!version.equals("2.1.8")) {
            s_logger.error("Version returned is not 2.1.8 but " + version);
        } else {
            s_logger.debug("Instance group test version is " + version);
        }
        
        checker.upgrade("2.1.8", "2.2.4");
        
        // manually check into database for now to verify
    }
}
