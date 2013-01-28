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

public class AdvanceZone217To224UpgradeTest extends TestCase {
    private static final Logger s_logger = Logger.getLogger(AdvanceZone217To224UpgradeTest.class);

    @Override
    @Before
    public void setUp() throws Exception {
        DbTestUtils.executeScript("cleanup.sql", false, true);
    }
    
    @Override
    @After
    public void tearDown() throws Exception {
    }
    
    public void test217to22Upgrade() throws SQLException {
        s_logger.debug("Finding sample data from 2.1.7");
        DbTestUtils.executeScript("fake.sql", false, true);
        
        Connection conn;
        PreparedStatement pstmt;
        
        VersionDaoImpl dao = ComponentLocator.inject(VersionDaoImpl.class);
        DatabaseUpgradeChecker checker = ComponentLocator.inject(DatabaseUpgradeChecker.class);
        
        String version = dao.getCurrentVersion();
        assert version.equals("2.1.7") : "Version returned is not 2.1.7 but " + version;
        
        checker.upgrade("2.1.7", "2.2.4");
        
        conn = Transaction.getStandaloneConnection();
        try {
            pstmt = conn.prepareStatement("SELECT version FROM version ORDER BY id DESC LIMIT 1");
            ResultSet rs = pstmt.executeQuery();
            assert rs.next() : "No version selected";
            assert rs.getString(1).equals("2.2.4") : "VERSION stored is not 2.2.4: " + rs.getString(1);
            rs.close();
            pstmt.close();
            
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM network_offerings");
            rs = pstmt.executeQuery();
            assert rs.next() : "Unable to get the count of network offerings.";
            assert (rs.getInt(1) == 7) : "Didn't find 7 network offerings but found " + rs.getInt(1);
            rs.close();
            pstmt.close();
            
            pstmt = conn.prepareStatement("SELECT DISTINCT networktype FROM data_center");
            rs = pstmt.executeQuery();
            assert rs.next()  && rs.getString(1).equals("Advanced") : "Network type is not advanced? " + rs.getString(1);
            assert !rs.next() : "Why do we have another one? " + rs.getString(1);
            rs.close();
            pstmt.close();
            
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM disk_offering WHERE removed IS NULL AND system_use=1 AND type='Service' AND recreatable=1");
            rs = pstmt.executeQuery();
            assert (rs.next() && rs.getInt(1) == 3) : "DiskOffering for system VMs are incorrect.  Expecting 3 but got " + rs.getInt(1);
            rs.close();
            pstmt.close();
            
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM op_dc_link_local_ip_address_alloc WHERE nic_id IS NOT NULL");
            rs = pstmt.executeQuery();
            rs.next();
            int controlNics = rs.getInt(1);
            rs.close();
            pstmt.close();
            
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM nics WHERE reserver_name='ControlNetworkGuru' and ip4_address is NOT NULL");
            rs = pstmt.executeQuery();
            assert (rs.next() && controlNics == rs.getInt(1)) : "Allocated nics should have been " + controlNics + " but it is " + rs.getInt(1);
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
