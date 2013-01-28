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
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;

import com.cloud.utils.component.ComponentLocator;
import com.cloud.utils.db.DbTestUtils;
import com.cloud.utils.db.Transaction;
import com.cloud.utils.exception.CloudRuntimeException;

public class Template2214To30UpgradeTest extends TestCase {
    private static final Logger s_logger = Logger
            .getLogger(Template2214To30UpgradeTest.class);

    @Override
    @Before
    public void setUp() throws Exception {
        DbTestUtils.executeScript("cleanup.sql", false,
                true);
    }

    @Override
    @After
    public void tearDown() throws Exception {
    }

    public void test2214to30Upgrade() throws SQLException {
        s_logger.debug("Finding sample data from 2.2.14");
        DbTestUtils.executeScript(
                "fake.sql", false,
                true);

        DatabaseUpgradeChecker checker = ComponentLocator
                .inject(DatabaseUpgradeChecker.class);

        checker.upgrade("2.2.14", "3.0.0");

        Connection conn = Transaction.getStandaloneConnection();

        try {
            checkSystemVm(conn);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }

    protected void checkSystemVm(Connection conn) throws SQLException {
        PreparedStatement pstmt;

        pstmt = conn
                .prepareStatement("SELECT version FROM `cloud`.`version` ORDER BY id DESC LIMIT 1");
        ResultSet rs = pstmt.executeQuery();
        assert rs.next() : "No version selected";
        assert rs.getString(1).equals("3.0.0") : "VERSION stored is not 3.0.0: "
                + rs.getString(1);
        rs.close();
        pstmt.close();

        pstmt = conn.prepareStatement("select id from vm_template where name='systemvm-xenserver-3.0.0' and removed is null");
        rs = pstmt.executeQuery();
        long templateId1 = rs.getLong(1);
        rs.close();
        pstmt.close();
        
        pstmt = conn.prepareStatement("select distinct(vm_template_id) from vm_instance where type <> 'USER' and hypervisor_type = 'XenServer'");
        rs = pstmt.executeQuery();
        long templateId = rs.getLong(1);
        rs.close();
        pstmt.close();

        assert (templateId ==  templateId1) : "XenServer System Vms not using 3.0.0 template";
        rs.close();
        pstmt.close();
        
        pstmt = conn.prepareStatement("select id from vm_template where name='systemvm-kvm-3.0.0' and removed is null");
        rs = pstmt.executeQuery();
        long templateId3 = rs.getLong(1);
        rs.close();
        pstmt.close();
        
        pstmt = conn.prepareStatement("select distinct(vm_template_id) from vm_instance where type <> 'USER' and hypervisor_type = 'KVM'");
        rs = pstmt.executeQuery();
        long templateId4 = rs.getLong(1);
        rs.close();
        pstmt.close();

        assert (templateId3 ==  templateId4) : "KVM System Vms not using 3.0.0 template";
        rs.close();
        pstmt.close();

    }

}
