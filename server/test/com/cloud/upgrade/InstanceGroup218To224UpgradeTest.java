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
import java.util.ArrayList;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;

import com.cloud.upgrade.dao.VersionDaoImpl;
import com.cloud.utils.component.ComponentLocator;
import com.cloud.utils.db.DbTestUtils;
import com.cloud.utils.db.Transaction;

public class InstanceGroup218To224UpgradeTest extends TestCase {
    private static final Logger s_logger = Logger.getLogger(InstanceGroup218To224UpgradeTest.class);

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
        
        PreparedStatement pstmt;
        ResultSet rs;
        
        VersionDaoImpl dao = ComponentLocator.inject(VersionDaoImpl.class);
        DatabaseUpgradeChecker checker = ComponentLocator.inject(DatabaseUpgradeChecker.class);
        
        String version = dao.getCurrentVersion();
        
        if (!version.equals("2.1.8")) {
            s_logger.error("Version returned is not 2.1.8 but " + version);
        } else {
            s_logger.debug("Instance group test version is " + version);
        }
        
        Long groupNumberVmInstance = 0L;
        ArrayList<Object[]> groups = new ArrayList<Object[]>();
        Connection conn = Transaction.getStandaloneConnection();
        ArrayList<Object[]> groupVmMaps = new ArrayList<Object[]>();
        
        try {
            //Check that correct number of instance groups were created
            pstmt = conn.prepareStatement("SELECT DISTINCT v.group, u.account_id from vm_instance v, user_vm u where v.group is not null and v.id=u.id");
            s_logger.debug("Query is" + pstmt);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                groupNumberVmInstance++;
            }

            rs.close();
            pstmt.close();
            //For each instance group from vm_instance table check that 1) entry was created in the instance_group table 2) vm to group map exists in instance_group_vm_map table
            //Check 1)
            pstmt = conn.prepareStatement("SELECT DISTINCT v.group, u.account_id from vm_instance v, user_vm u where v.group is not null and v.id=u.id");
            s_logger.debug("Query is" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] group = new Object[10];
                group[0] = rs.getString(1); // group name
                group[1] = rs.getLong(2);  // accountId
                groups.add(group);
            }
            rs.close();
            pstmt.close();
            
           
        } finally {
            conn.close();
        }
        
        checker.upgrade("2.1.8", "2.2.4");
        
        conn = Transaction.getStandaloneConnection();
        try {
            
            s_logger.debug("Starting tesing upgrade from 2.1.8 to 2.2.4 for Instance groups...");
            
            //Version check
            pstmt = conn.prepareStatement("SELECT version FROM version");
            rs = pstmt.executeQuery();
            
            if (!rs.next()) {
                s_logger.error("ERROR: No version selected");
            } else if (!rs.getString(1).equals("2.2.4")) {
                s_logger.error("ERROR: VERSION stored is not 2.2.4: " + rs.getString(1));
            }
            rs.close();
            pstmt.close();
            
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM instance_group");
            rs = pstmt.executeQuery();
            
            Long groupNumber = 0L;
            if (rs.next()) {
                groupNumber = rs.getLong(1);
            }

            rs.close();
            pstmt.close();
            
            if (groupNumber != groupNumberVmInstance) {
                s_logger.error("ERROR: instance groups were updated incorrectly. Have " + groupNumberVmInstance + " groups in vm_instance table, and " + groupNumber + " where created in instance_group table. Stopping the test");
                System.exit(2);
            }
            
            for (Object[] group : groups) {
                String groupName = (String)group[0];
                Long accountId = (Long)group[1];
                if (!checkInstanceGroup(conn, groupName, accountId)) {
                    s_logger.error("ERROR: Unable to find group with name " + groupName + " for account id=" + accountId + ", stopping the test");
                    System.exit(2);
                }
            } 
            
            rs.close();
            pstmt.close();
            
            //Check 2)
//            pstmt = conn.prepareStatement("SELECT v.id from vm_instance v, instance_group g WHERE g.account_id=v.account_id and v.group=?");
//            s_logger.debug("Query is" + pstmt);
//            rs = pstmt.executeQuery();
//            
//            while (rs.next()) {
//                Object[] groupMaps = new Object[10];
//                groupMaps[0] = rs.getLong(1); // vmId
//                groupMaps[1] = rs.getLong(2);  // groupId
//                groupVmMaps.add(groupMaps);
//            }
//            rs.close();
//            pstmt.close();
//            
//            for (Object[] groupMap : groupVmMaps) {
//                Long groupId = (Long)groupMap[0];
//                Long instanceId = (Long)groupMap[1];
//                if (!checkInstanceGroupVmMap(conn, groupId, instanceId)) {
//                    s_logger.error("ERROR: unable to find instanceGroupVMMap for vm id=" + instanceId + " and group id=" + groupId + ", stopping the test");
//                    System.exit(2);
//                }
//            }  
//            
//            rs.close();
//            pstmt.close();
            
            s_logger.debug("Instance group upgrade test is passed");
            
        } finally {
            conn.close();
        }
    }
    
    protected boolean checkInstanceGroup(Connection conn, String groupName, long accountId) throws SQLException{
        
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM  instance_group WHERE name = ? and account_id = ?");
        pstmt.setString(1, groupName);
        pstmt.setLong(2, accountId);
        ResultSet rs = pstmt.executeQuery();
        
        if (!rs.next()) {
            return false;
        } else {
            return true;
        }
    }
    
    protected boolean checkInstanceGroupVmMap(Connection conn, long groupId, long vmId) throws SQLException{
        
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM  instance_group_vm_map WHERE group_id = ? and instance_id = ?");
        pstmt.setLong(1, groupId);
        pstmt.setLong(2, vmId);
        ResultSet rs = pstmt.executeQuery();
        
        if (!rs.next()) {
            return false;
        } else {
            return true;
        }
    }
    
}

