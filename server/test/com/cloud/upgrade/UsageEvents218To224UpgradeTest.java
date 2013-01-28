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


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;

import com.cloud.upgrade.dao.VersionDaoImpl;
import com.cloud.utils.component.ComponentLocator;
import com.cloud.utils.db.DbTestUtils;
import com.cloud.utils.db.Transaction;

public class UsageEvents218To224UpgradeTest extends TestCase {
    private static final Logger s_logger = Logger.getLogger(UsageEvents218To224UpgradeTest.class);

    @Override
    @Before
    public void setUp() throws Exception {
        DbTestUtils.executeScript("cleanup.sql", false, true);
    }
    
    @Override
    @After
    public void tearDown() throws Exception {
    }
    
    public void test21to22Upgrade() throws SQLException {
        s_logger.debug("Finding sample data from 2.1.8");
        DbTestUtils.executeScript("fake.sql", false, true);
        
        Connection conn;
        PreparedStatement pstmt;
        
        VersionDaoImpl dao = ComponentLocator.inject(VersionDaoImpl.class);
        DatabaseUpgradeChecker checker = ComponentLocator.inject(DatabaseUpgradeChecker.class);
        
        String version = dao.getCurrentVersion();
        assert version.equals("2.1.8") : "Version returned is not 2.1.8 but " + version;
        
        checker.upgrade("2.1.8", "2.2.4");
        
        conn = Transaction.getStandaloneConnection();
        try {
            pstmt = conn.prepareStatement("SELECT version FROM version ORDER BY id DESC LIMIT 1");
            ResultSet rs = pstmt.executeQuery();
            assert rs.next() : "No version selected";
            assert rs.getString(1).equals("2.2.4") : "VERSION stored is not 2.2.4: " + rs.getString(1);
            rs.close();
            pstmt.close();
            
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM usage_event");
            rs = pstmt.executeQuery();
            assert rs.next() : "Unable to get the count of usage events";
            assert (rs.getInt(1) == 37) : "Didn't find 37 usage events but found " + rs.getInt(1);
            rs.close();
            pstmt.close();
            
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }
    
}
