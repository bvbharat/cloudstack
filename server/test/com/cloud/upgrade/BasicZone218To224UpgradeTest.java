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

public class BasicZone218To224UpgradeTest extends TestCase {
    private static final Logger s_logger = Logger.getLogger(BasicZone218To224UpgradeTest.class);

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
        s_logger.debug("Finding sample data from 2.1.8");
        DbTestUtils.executeScript("fake.sql", false, true);
        
        Connection conn = Transaction.getStandaloneConnection();
        PreparedStatement pstmt;
        
        VersionDaoImpl dao = ComponentLocator.inject(VersionDaoImpl.class);
        DatabaseUpgradeChecker checker = ComponentLocator.inject(DatabaseUpgradeChecker.class);
        
        String version = dao.getCurrentVersion();
        
        if (!version.equals("2.1.8")) {
            s_logger.error("Version returned is not 2.1.8 but " + version);
        } else {
            s_logger.debug("Basic zone test version is " + version);
        }
        
        checker.upgrade("2.1.8", "2.2.4");
        
        conn = Transaction.getStandaloneConnection();
        try {
            
            s_logger.debug("Starting tesing upgrade from 2.1.8 to 2.2.4 for Basic zone...");
            
            //Version check
            pstmt = conn.prepareStatement(" SELECT version FROM version ORDER BY id DESC LIMIT 1");
            ResultSet rs = pstmt.executeQuery();
            
            if (!rs.next()) {
                s_logger.error("ERROR: No version selected");
            } else if (!rs.getString(1).equals("2.2.4")) {
                s_logger.error("ERROR: VERSION stored is not 2.2.4: " + rs.getString(1));
            }
            rs.close();
            pstmt.close();
            
            //Check that default network offerings are present
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM network_offerings");
            rs = pstmt.executeQuery();
            
            if (!rs.next()) {
                s_logger.error("ERROR: Unable to get the count of network offerings.");
            } else if (rs.getInt(1) != 7) {
                s_logger.error("ERROR: Didn't find 7 network offerings but found " + rs.getInt(1));
            } else {
                s_logger.debug("Network offerings test passed");
            }

            rs.close();
            pstmt.close();
            
            
            //Zone network type check
            pstmt = conn.prepareStatement("SELECT DISTINCT networktype FROM data_center");
            rs = pstmt.executeQuery();
            
            if (!rs.next()) {
                s_logger.error("No zone exists after upgrade");
            } else if (!rs.getString(1).equals("Basic")) {
                s_logger.error("ERROR: Zone type is not Basic");
            } else if (rs.next()) {
                s_logger.error("ERROR: Why do we have more than 1 zone with different types??");
                System.exit(2);
            } else {
                s_logger.debug("Test passed. Zone was updated properly with type Basic");
            }
            rs.close();
            pstmt.close();
            
            //Check that vnet/cidr were set to NULL for basic zone
            pstmt = conn.prepareStatement("SELECT vnet, guest_network_cidr FROM data_center");
            rs = pstmt.executeQuery();
            
            if (!rs.next()) {
                s_logger.error("ERROR: vnet field is missing for the zone");
            } else if (rs.getString(1) != null || rs.getString(2) != null) {
                s_logger.error("ERROR: vnet/guestCidr should be NULL for basic zone; instead it's " + rs.getString(1));
            } else {
                s_logger.debug("Test passed. Vnet and cidr are set to NULL for the basic zone");
            }
            
            rs.close();
            pstmt.close();
            
            //Verify that default Direct guest network got created, and it's Shared and Default
            pstmt = conn.prepareStatement("SELECT traffic_type, guest_type, shared, is_default, id FROM networks WHERE name LIKE '%BasicZoneDirectNetwork%'");
            rs = pstmt.executeQuery();
            
            if (!rs.next()) {
                s_logger.error("Direct network is missing for the Basic zone");
            } else if (!rs.getString(1).equalsIgnoreCase("Guest") || !rs.getString(2).equalsIgnoreCase("Direct") || !rs.getBoolean(3) || !rs.getBoolean(4)) {
                s_logger.error("Direct network for basic zone has incorrect setting");
            } else {
                s_logger.debug("Test passed. Default Direct Basic zone network parameters were set correctly");
            }
            
            long defaultDirectNetworkId = rs.getInt(5);
            rs.close();
            pstmt.close();
            
            //Verify that all vlans in the zone belong to default Direct network
            pstmt = conn.prepareStatement("SELECT network_id FROM vlan");
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                if (rs.getInt(1) != defaultDirectNetworkId) {
                    s_logger.error("ERROR: network_id is set incorrectly for public untagged vlans in Basic zone");
                    System.exit(2);
                }
            }
            
            s_logger.debug("Test passed for vlan table in Basic zone");
            
            rs.close();
            pstmt.close();
            
            //Verify user_ip_address table
            pstmt = conn.prepareStatement("SELECT source_network_id FROM user_ip_address");
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                if (rs.getInt(1) != defaultDirectNetworkId) {
                    s_logger.error("ERROR: network_id is set incorrectly for public Ip addresses (user_ip_address table) in Basic zone");
                    System.exit(2);
                }
            }
            
            s_logger.debug("Test passed for user_ip_address table in Basic zone");
            
            rs.close();
            pstmt.close();
            
            //Verify domain_router table
            pstmt = conn.prepareStatement("SELECT network_id FROM domain_router");
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                if (rs.getInt(1) != defaultDirectNetworkId) {
                    s_logger.error("ERROR: network_id is set incorrectly for domain routers (domain_router table) in Basic zone");
                    System.exit(2);
                }
            }
            
            s_logger.debug("Test passed for domain_router table in Basic zone");
            
            rs.close();
            pstmt.close();
            
            s_logger.debug("Basic zone test is finished");
            
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }
    
}
