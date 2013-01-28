# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
# 
#   http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
""" BVT tests for Service offerings"""

#Import Local Modules
import marvin
from marvin.cloudstackTestCase import *
from marvin.cloudstackAPI import *
from marvin.integration.lib.utils import *
from marvin.integration.lib.base import *
from marvin.integration.lib.common import *
from nose.plugins.attrib import attr


_multiprocess_shared_ = True
class Services:
    """Test Service offerings Services
    """

    def __init__(self):
        self.services = {
                        "off":
                            {
                                "name": "Service Offering",
                                "displaytext": "Service Offering",
                                "cpunumber": 1,
                                "cpuspeed": 100, # MHz
                                "memory": 128, # in MBs
                            },
                     }

class TestCreateServiceOffering(cloudstackTestCase):

    def setUp(self):
        self.apiclient = self.testClient.getApiClient()
        self.dbclient = self.testClient.getDbConnection()
        self.cleanup = []
        self.services = Services().services

    def tearDown(self):
        try:
            #Clean up, terminate the created templates
            cleanup_resources(self.apiclient, self.cleanup)

        except Exception as e:
            raise Exception("Warning: Exception during cleanup : %s" % e)

        return
    
    @attr(tags = ["advanced", "advancedns", "smoke", "basic", "eip", "sg"])
    def test_01_create_service_offering(self):
        """Test to create service offering"""

        # Validate the following:
        # 1. createServiceOfferings should return a valid information for newly created offering
        # 2. The Cloud Database contains the valid information

        service_offering = ServiceOffering.create(
                                            self.apiclient,
                                            self.services["off"]
                                            )
        self.cleanup.append(service_offering)

        self.debug("Created service offering with ID: %s" % service_offering.id)

        list_service_response = list_service_offering(
                                                      self.apiclient,
                                                      id=service_offering.id
                                                      )
        self.assertEqual(
                            isinstance(list_service_response, list),
                            True,
                            "Check list response returns a valid list"
                        )
         
        self.assertNotEqual(
                        len(list_service_response),
                        0,
                        "Check Service offering is created"
                        )
        service_response = list_service_response[0]

        self.assertEqual(
                        list_service_response[0].cpunumber,
                        self.services["off"]["cpunumber"],
                        "Check server id in createServiceOffering"
                        )
        self.assertEqual(
                        list_service_response[0].cpuspeed,
                        self.services["off"]["cpuspeed"],
                        "Check cpuspeed in createServiceOffering"
                        )
        self.assertEqual(
                        list_service_response[0].displaytext,
                        self.services["off"]["displaytext"],
                        "Check server displaytext in createServiceOfferings"
                        )
        self.assertEqual(
                        list_service_response[0].memory,
                        self.services["off"]["memory"],
                        "Check memory in createServiceOffering"
                        )
        self.assertEqual(
                            list_service_response[0].name,
                            self.services["off"]["name"],
                            "Check name in createServiceOffering"
                        )
        return


class TestServiceOfferings(cloudstackTestCase):

    def setUp(self):
        self.apiclient = self.testClient.getApiClient()
        self.dbclient = self.testClient.getDbConnection()
        self.cleanup = []

    def tearDown(self):

        try:
            #Clean up, terminate the created templates
            cleanup_resources(self.apiclient, self.cleanup)

        except Exception as e:
            raise Exception("Warning: Exception during cleanup : %s" % e)

        return

    @classmethod
    def setUpClass(cls):
        cls.services = Services().services
        cls.api_client = super(TestServiceOfferings, cls).getClsTestClient().getApiClient()
        cls.service_offering_1 = ServiceOffering.create(
                                                cls.api_client,
                                                cls.services["off"]
                                            )
        cls.service_offering_2 = ServiceOffering.create(
                                                cls.api_client,
                                                cls.services["off"]
                                            )
        cls._cleanup = [cls.service_offering_1]
        return

    @classmethod
    def tearDownClass(cls):
        try:
            cls.api_client = super(TestServiceOfferings, cls).getClsTestClient().getApiClient()
            #Clean up, terminate the created templates
            cleanup_resources(cls.api_client, cls._cleanup)

        except Exception as e:
            raise Exception("Warning: Exception during cleanup : %s" % e)
        return

    @attr(tags = ["advanced", "advancedns", "smoke", "basic", "eip", "sg"])
    def test_02_edit_service_offering(self):
        """Test to update existing service offering"""

        # Validate the following:
        # 1. updateServiceOffering should return
        #    a valid information for newly created offering

        #Generate new name & displaytext from random data
        random_displaytext = random_gen()
        random_name = random_gen()

        self.debug("Updating service offering with ID: %s" % 
                                        self.service_offering_1.id)

        cmd = updateServiceOffering.updateServiceOfferingCmd()
        #Add parameters for API call
        cmd.id = self.service_offering_1.id
        cmd.displaytext = random_displaytext
        cmd.name = random_name
        self.apiclient.updateServiceOffering(cmd)

        list_service_response = list_service_offering(
                                            self.apiclient,
                                            id=self.service_offering_1.id
                                            )
        self.assertEqual(
                            isinstance(list_service_response, list),
                            True,
                            "Check list response returns a valid list"
                        )
        
        self.assertNotEqual(
                            len(list_service_response),
                            0,
                            "Check Service offering is updated"
                        )

        self.assertEqual(
                            list_service_response[0].displaytext,
                            random_displaytext,
                            "Check server displaytext in updateServiceOffering"
                        )
        self.assertEqual(
                            list_service_response[0].name,
                            random_name,
                            "Check server name in updateServiceOffering"
                        )

        return

    @attr(tags = ["advanced", "advancedns", "smoke", "basic", "eip", "sg"])
    def test_03_delete_service_offering(self):
        """Test to delete service offering"""

        # Validate the following:
        # 1. deleteServiceOffering should return
        #    a valid information for newly created offering

        self.debug("Deleting service offering with ID: %s" % 
                                        self.service_offering_2.id)

        self.service_offering_2.delete(self.apiclient)

        list_service_response = list_service_offering(
                                            self.apiclient,
                                            id=self.service_offering_2.id
                                            )

        self.assertEqual(
                        list_service_response,
                        None,
                        "Check if service offering exists in listDiskOfferings"
                    )

        return
