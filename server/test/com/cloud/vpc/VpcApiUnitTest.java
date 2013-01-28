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
package com.cloud.vpc;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Before;

import com.cloud.configuration.dao.ConfigurationDaoImpl;
import com.cloud.configuration.dao.ResourceCountDaoImpl;
import com.cloud.dc.dao.VlanDaoImpl;
import com.cloud.exception.InvalidParameterValueException;
import com.cloud.network.Network.Service;
import com.cloud.network.dao.FirewallRulesDaoImpl;
import com.cloud.network.dao.IPAddressDaoImpl;
import com.cloud.network.dao.PhysicalNetworkDaoImpl;
import com.cloud.network.dao.Site2SiteVpnGatewayDaoImpl;
import com.cloud.network.vpc.Vpc;
import com.cloud.network.vpc.VpcManager;
import com.cloud.network.vpc.VpcManagerImpl;
import com.cloud.network.vpc.dao.PrivateIpDaoImpl;
import com.cloud.network.vpc.dao.StaticRouteDaoImpl;
import com.cloud.network.vpc.dao.VpcGatewayDaoImpl;
import com.cloud.network.vpc.dao.VpcOfferingDaoImpl;
import com.cloud.server.ManagementService;
import com.cloud.tags.dao.ResourceTagsDaoImpl;
import com.cloud.user.AccountVO;
import com.cloud.user.MockAccountManagerImpl;
import com.cloud.user.dao.AccountDaoImpl;
import com.cloud.utils.component.ComponentLocator;
import com.cloud.utils.component.MockComponentLocator;
import com.cloud.vm.dao.DomainRouterDaoImpl;
import com.cloud.vpc.dao.MockNetworkDaoImpl;
import com.cloud.vpc.dao.MockNetworkOfferingDaoImpl;
import com.cloud.vpc.dao.MockNetworkOfferingServiceMapDaoImpl;
import com.cloud.vpc.dao.MockNetworkServiceMapDaoImpl;
import com.cloud.vpc.dao.MockVpcDaoImpl;
import com.cloud.vpc.dao.MockVpcOfferingDaoImpl;
import com.cloud.vpc.dao.MockVpcOfferingServiceMapDaoImpl;

public class VpcApiUnitTest extends TestCase{
    private static final Logger s_logger = Logger.getLogger(VpcApiUnitTest.class);
    MockComponentLocator _locator;
    VpcManager _vpcService;

    @Override
    @Before
    public void setUp() throws Exception {
        _locator = new MockComponentLocator(ManagementService.Name);
        _locator.addDao("VpcDao", MockVpcDaoImpl.class);
        _locator.addDao("VpcOfferingDao", VpcOfferingDaoImpl.class);
        _locator.addDao("ConfigurationDao", ConfigurationDaoImpl.class);
        _locator.addDao("NetworkDao", MockNetworkDaoImpl.class);
        _locator.addDao("IPAddressDao", IPAddressDaoImpl.class);
        _locator.addDao("DomainRouterDao", DomainRouterDaoImpl.class);
        _locator.addDao("VpcGatewayDao", VpcGatewayDaoImpl.class);
        _locator.addDao("PrivateIpDao", PrivateIpDaoImpl.class);
        _locator.addDao("StaticRouteDao", StaticRouteDaoImpl.class);
        _locator.addDao("NetworkOfferingServiceMapDao", MockNetworkOfferingServiceMapDaoImpl.class);
        _locator.addDao("VpcOfferingServiceMapDao", MockVpcOfferingServiceMapDaoImpl.class);
        _locator.addDao("PhysicalNetworkDao", PhysicalNetworkDaoImpl.class);
        _locator.addDao("ResourceTagDao", ResourceTagsDaoImpl.class);
        _locator.addDao("FirewallRulesDao", FirewallRulesDaoImpl.class);
        _locator.addDao("VlanDao", VlanDaoImpl.class);
        _locator.addDao("AccountDao", AccountDaoImpl.class);
        _locator.addDao("ResourceCountDao", ResourceCountDaoImpl.class);
        _locator.addDao("NetworkOfferingDao", MockNetworkOfferingDaoImpl.class);
        _locator.addDao("NetworkServiceMapDao", MockNetworkServiceMapDaoImpl.class);
        _locator.addDao("VpcOfferingDao", MockVpcOfferingDaoImpl.class);
        _locator.addDao("Site2SiteVpnDao", Site2SiteVpnGatewayDaoImpl.class);
        
        _locator.addManager("ConfigService", MockConfigurationManagerImpl.class);
        _locator.addManager("vpc manager", VpcManagerImpl.class);
        _locator.addManager("account manager", MockAccountManagerImpl.class);
        _locator.addManager("network manager", MockNetworkManagerImpl.class);
        _locator.addManager("Site2SiteVpnManager", MockSite2SiteVpnManagerImpl.class);
        _locator.addManager("ResourceLimitService", MockResourceLimitManagerImpl.class);
        
        _locator.makeActive(null);
        
        _vpcService = ComponentLocator.inject(VpcManagerImpl.class);
    }
    
    public void test() {
        s_logger.debug("Starting test for VpcService interface");
        //Vpc service methods
        //getActiveVpc();
        //deleteVpc();
        
        //Vpc manager methods
        validateNtwkOffForVpc();
        //destroyVpc();
       
    }
    
    protected void deleteVpc() {
        //delete existing offering
        boolean result = false;
        String msg = null;
        try {
            List<String> svcs = new ArrayList<String>();
            svcs.add(Service.SourceNat.getName());
            result = _vpcService.deleteVpc(1);
        }  catch (Exception ex) {
            msg = ex.getMessage();
        } finally {
            if (result) {
                s_logger.debug("Delete vpc: Test passed, vpc is deleted");
            } else {
                s_logger.error("Delete vpc: TEST FAILED, vpc failed to delete " + msg);
            }
        }
        
        //delete non-existing offering
        result = false;
        msg = null;
        try {
            List<String> svcs = new ArrayList<String>();
            svcs.add(Service.SourceNat.getName());
            result = _vpcService.deleteVpc(100);
        }  catch (Exception ex) {
            msg = ex.getMessage();
        } finally {
            if (!result) {
                s_logger.debug("Delete vpc: Test passed, non existing vpc failed to delete ");
            } else {
                s_logger.error("Delete vpc: TEST FAILED, true is returned when try to delete non existing vpc");
            }
        }
    }
    
    protected void getActiveVpc() {
        //test for active vpc
        boolean result = false;
        String msg = null;
        Vpc vpc = null;
        try {
            List<String> svcs = new ArrayList<String>();
            svcs.add(Service.SourceNat.getName());
            vpc = _vpcService.getActiveVpc(1);
            if (vpc != null) {
                result = true;
            }
        }  catch (Exception ex) {
            msg = ex.getMessage();
        } finally {
            if (result) {
                s_logger.debug("Get active Vpc: Test passed, active vpc is returned");
            } else {
                s_logger.error("Get active Vpc: TEST FAILED, active vpc is not returned " + msg);
            }
        }
        
        //test for inactive vpc
        result = false;
        msg = null;
        vpc = null;
        try {
            List<String> svcs = new ArrayList<String>();
            svcs.add(Service.SourceNat.getName());
            vpc = _vpcService.getActiveVpc(2);
            if (vpc != null) {
                result = true;
            }
        }  catch (Exception ex) {
            msg = ex.getMessage();
        } finally {
            if (!result) {
                s_logger.debug("Get active Vpc: Test passed, no vpc is returned");
            } else {
                s_logger.error("Get active Vpc: TEST FAILED, non active vpc is returned");
            }
        }
    }
    
    protected void destroyVpc() {
        try {
            _vpcService.destroyVpc(_vpcService.getVpc(1), new AccountVO(), 1L);
        } catch (Exception ex) {
            s_logger.error("Destroy VPC TEST FAILED due to exc ", ex);
        }
    }

    protected void validateNtwkOffForVpc() {
        //validate network offering
        //1) correct network offering
        boolean result = false;
        try {
            _vpcService.validateNtkwOffForVpc(1, "0.0.0.0", "111-", new AccountVO(), _vpcService.getVpc(1), 2L, "10.1.1.1");
            result = true;
            s_logger.debug("Validate network offering: Test passed: the offering is valid for vpc creation");
        } catch (Exception ex) {
            s_logger.error("Validate network offering: TEST FAILED due to exc ", ex);
        }
        
        //2) invalid offering - source nat is not included
        result = false;
        String msg = null;
        try {
            _vpcService.validateNtkwOffForVpc(2, "0.0.0.0", "111-", new AccountVO(), _vpcService.getVpc(1), 2L, "10.1.1.1");
            result = true;
        } catch (InvalidParameterValueException ex) {
            msg = ex.getMessage();
        } finally {
            if (!result) {
                s_logger.debug("Validate network offering: Test passed: "  + msg);
            } else {
                s_logger.error("Validate network offering: TEST FAILED, can't use network offering without SourceNat service");
            }
        }
        
        //3) invalid offering - conserve mode is off
        result = false;
        msg = null;
        try {
            _vpcService.validateNtkwOffForVpc(3, "0.0.0.0", "111-", new AccountVO(), _vpcService.getVpc(1), 2L, "10.1.1.1");
            result = true;
        } catch (InvalidParameterValueException ex) {
            msg = ex.getMessage();
        } finally {
            if (!result) {
                s_logger.debug("Validate network offering: Test passed: " + msg);
            } else {
                s_logger.error("Validate network offering: TEST FAILED, can't use network offering without conserve mode = true");
            }
        }
        
        //4) invalid offering - guest type shared
        result = false;
        try {
            _vpcService.validateNtkwOffForVpc(4, "0.0.0.0", "111-", new AccountVO(), _vpcService.getVpc(1), 2L, "10.1.1.1");
            result = true;
        } catch (InvalidParameterValueException ex) {
            msg = ex.getMessage();
        } finally {
            if (!result) {
                s_logger.debug("Validate network offering: Test passed: " + msg);
            } else {
                s_logger.error("Validate network offering: TEST FAILED, can't use network offering with guest type = Shared");
            }
        }
        
        //5) Invalid offering - no redundant router support
        result = false;
        try {
            _vpcService.validateNtkwOffForVpc(5, "0.0.0.0", "111-", new AccountVO(), _vpcService.getVpc(1), 2L, "10.1.1.1");
            result = true;
        } catch (InvalidParameterValueException ex) {
            msg = ex.getMessage();
        } finally {
            if (!result) {
                s_logger.debug("Validate network offering: Test passed: " + msg);
            } else {
                s_logger.error("TEST FAILED, can't use network offering with guest type = Shared");
            }
        }
        
        //6) Only one network in the VPC can support LB service - negative scenario
        result = false;
        try {
            _vpcService.validateNtkwOffForVpc(6, "0.0.0.0", "111-", new AccountVO(), _vpcService.getVpc(1), 2L, "10.1.1.1");
            result = true;
            s_logger.debug("Validate network offering: Test passed: the offering is valid for vpc creation");
        } catch (InvalidParameterValueException ex) {
            msg = ex.getMessage();
        } finally {
            if (!result) {
                s_logger.debug("Test passed: " + msg);
            } else {
                s_logger.error("Validate network offering: TEST FAILED, can't use network offering with guest type = Shared");
            }
        }
    }

}
