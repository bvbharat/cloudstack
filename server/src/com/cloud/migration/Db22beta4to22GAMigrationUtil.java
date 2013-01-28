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
package com.cloud.migration;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.xml.DOMConfigurator;

import com.cloud.utils.PropertiesUtil;
import com.cloud.utils.db.DB;
import com.cloud.utils.db.Transaction;
import com.cloud.utils.exception.CloudRuntimeException;

@DB(txn=false)
public class Db22beta4to22GAMigrationUtil {
        
    private Map<Long,Long> pfRuleIdToIpAddressIdMap = new HashMap<Long, Long>();
    private final String FindPfIdToPublicIpId = "SELECT id,ip_address_id from firewall_rules where is_static_nat=1";
    private final String FindVmIdPerPfRule = "SELECT instance_id from port_forwarding_rules where id = ?";
    private final String WriteVmIdToIpAddrTable = "UPDATE user_ip_address set vm_id = ? where id = ?";
    protected Db22beta4to22GAMigrationUtil() {
    }
    
    @DB
    //This method gets us a map of pf/firewall id <-> ip address id
    //Using the keyset, we will iterate over the pf table to find corresponding vm id
    //When we get the vm id, we will use the val for each key to update the corresponding ip addr row with the vm id
    public void populateMap(){
        Long key = null;
        Long val = null;
        
        Transaction txn = Transaction.open(Transaction.CLOUD_DB);
        
        StringBuilder sql = new StringBuilder(FindPfIdToPublicIpId);

        PreparedStatement pstmt = null;
        try {
            pstmt = txn.prepareAutoCloseStatement(sql.toString());

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                key = rs.getLong("id");
                val = rs.getLong("ip_address_id");
                pfRuleIdToIpAddressIdMap.put(key, val);
            }           

        } catch (SQLException e) {
            throw new CloudRuntimeException("Unable to execute " + pstmt.toString(), e);
        }

    }
    
    @DB
    public void updateVmIdForIpAddresses(){
        Transaction txn = Transaction.open(Transaction.CLOUD_DB);
        Set<Long> pfIds = pfRuleIdToIpAddressIdMap.keySet();
        StringBuilder sql = new StringBuilder(FindVmIdPerPfRule);
        Long vmId = null;
        Long ipAddressId = null;
        PreparedStatement pstmt = null;
        for(Long pfId : pfIds){
            try {
                pstmt = txn.prepareAutoCloseStatement(sql.toString());
                pstmt.setLong(1, pfId);
                ResultSet rs = pstmt.executeQuery();
                while(rs.next()) {
                    vmId = rs.getLong("instance_id");
                }
                ipAddressId = pfRuleIdToIpAddressIdMap.get(pfId);
                finallyUpdate(ipAddressId, vmId, txn);
            } catch (SQLException e) {
                throw new CloudRuntimeException("Unable to execute " + pstmt.toString(), e);
            }
        }
    }
    
    @DB
    public void finallyUpdate(Long ipAddressId, Long vmId, Transaction txn){

        StringBuilder sql = new StringBuilder(WriteVmIdToIpAddrTable);

        PreparedStatement pstmt = null;
        try {
            pstmt = txn.prepareAutoCloseStatement(sql.toString());
            pstmt.setLong(1, vmId);
            pstmt.setLong(2, ipAddressId);
            int rs = pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CloudRuntimeException("Unable to execute " + pstmt.toString(), e);
        }
    }
    
    public static void main(String[] args) {

        File file = PropertiesUtil.findConfigFile("log4j-cloud.xml");

        if(file != null) {
            System.out.println("Log4j configuration from : " + file.getAbsolutePath());
            DOMConfigurator.configureAndWatch(file.getAbsolutePath(), 10000);
        } else {
            System.out.println("Configure log4j with default properties");
        }
        
        Db22beta4to22GAMigrationUtil util = new Db22beta4to22GAMigrationUtil();
        util.populateMap();
        util.updateVmIdForIpAddresses();
    }
}
