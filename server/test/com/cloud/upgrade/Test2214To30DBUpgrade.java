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

public class Test2214To30DBUpgrade extends TestCase {
    private static final Logger s_logger = Logger
            .getLogger(Test2214To30DBUpgrade.class);

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
            checkPhysicalNetworks(conn);
            checkNetworkOfferings(conn);
            checkNetworks(conn);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }

    protected void checkPhysicalNetworks(Connection conn) throws SQLException {
        PreparedStatement pstmt;

        pstmt = conn
                .prepareStatement("SELECT version FROM `cloud`.`version` ORDER BY id DESC LIMIT 1");
        ResultSet rs = pstmt.executeQuery();
        assert rs.next() : "No version selected";
        assert rs.getString(1).equals("3.0.0") : "VERSION stored is not 3.0.0: "
                + rs.getString(1);
        rs.close();
        pstmt.close();

        pstmt = conn.prepareStatement("SELECT COUNT(*) FROM `cloud`.`physical_network`");
        rs = pstmt.executeQuery();
        assert rs.next() : "No physical networks setup.";
        rs.close();
        pstmt.close();

    }

    protected void checkNetworkOfferings(Connection conn) throws SQLException {
        // 1) verify that all fields are present
        List<String> fields = new ArrayList<String>();
        fields.add("id");
        fields.add("name");
        fields.add("unique_name");
        fields.add("display_text");
        fields.add("nw_rate");
        fields.add("mc_rate");
        fields.add("traffic_type");
        fields.add("specify_vlan");
        fields.add("system_only");
        fields.add("service_offering_id");
        fields.add("tags");
        fields.add("default");
        fields.add("availability");
        fields.add("state");
        fields.add("removed");
        fields.add("created");
        fields.add("guest_type");
        fields.add("dedicated_lb_service");
        fields.add("shared_source_nat_service");
        fields.add("specify_ip_ranges");
        fields.add("sort_key");
        fields.add("uuid");
        fields.add("redundant_router_service");
        fields.add("conserve_mode");
        fields.add("elastic_ip_service");
        fields.add("elastic_lb_service");

        PreparedStatement pstmt;
        for (String field : fields) {
            pstmt = conn
                    .prepareStatement("SHOW COLUMNS FROM `cloud`.`network_offerings` LIKE ?");
            pstmt.setString(1, field);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                throw new CloudRuntimeException("Field " + field
                        + " is missing in upgraded network_offerings table");
            }
            rs.close();
            pstmt.close();

        }

        // 2) compare default network offerings
    }

    protected void checkNetworks(Connection conn) throws SQLException {

        // 1) verify that all fields are present
        List<String> fields = new ArrayList<String>();
        fields.add("id");
        fields.add("name");

        fields.add("mode");
        fields.add("broadcast_domain_type");
        fields.add("traffic_type");
        fields.add("display_text");
        fields.add("broadcast_uri");
        fields.add("gateway");
        fields.add("cidr");
        fields.add("network_offering_id");
        fields.add("physical_network_id");
        fields.add("data_center_id");
        fields.add("related");
        fields.add("guru_name");
        fields.add("state");
        fields.add("dns1");
        fields.add("domain_id");
        fields.add("account_id");
        fields.add("set_fields");
        fields.add("guru_data");
        fields.add("dns2");
        fields.add("network_domain");
        fields.add("created");
        fields.add("removed");
        fields.add("reservation_id");
        fields.add("uuid");
        fields.add("guest_type");
        fields.add("restart_required");
        fields.add("specify_ip_ranges");
        fields.add("acl_type");

        PreparedStatement pstmt;
        for (String field : fields) {
            pstmt = conn.prepareStatement("SHOW COLUMNS FROM `cloud`.`networks` LIKE ?");
            pstmt.setString(1, field);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                throw new CloudRuntimeException("Field " + field
                        + " is missing in upgraded networks table");
            }
            rs.close();
            pstmt.close();

        }

    }
}
