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
package org.apache.cloudstack.api.command.user.network;

import org.apache.log4j.Logger;

import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.ApiErrorCode;
import org.apache.cloudstack.api.BaseCmd;
import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.ServerApiException;
import org.apache.cloudstack.api.response.DomainResponse;
import org.apache.cloudstack.api.response.NetworkResponse;
import org.apache.cloudstack.api.response.NetworkOfferingResponse;
import org.apache.cloudstack.api.response.PhysicalNetworkResponse;
import org.apache.cloudstack.api.response.ProjectResponse;
import org.apache.cloudstack.api.response.VpcResponse;
import org.apache.cloudstack.api.response.ZoneResponse;
import com.cloud.exception.ConcurrentOperationException;
import com.cloud.exception.InsufficientCapacityException;
import com.cloud.exception.InvalidParameterValueException;
import com.cloud.exception.ResourceAllocationException;
import com.cloud.network.Network;
import com.cloud.network.Network.GuestType;
import com.cloud.offering.NetworkOffering;
import com.cloud.user.UserContext;

@APICommand(name = "createNetwork", description="Creates a network", responseObject=NetworkResponse.class)
public class CreateNetworkCmd extends BaseCmd {
    public static final Logger s_logger = Logger.getLogger(CreateNetworkCmd.class.getName());

    private static final String s_name = "createnetworkresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @Parameter(name=ApiConstants.NAME, type=CommandType.STRING, required=true, description="the name of the network")
    private String name;

    @Parameter(name=ApiConstants.DISPLAY_TEXT, type=CommandType.STRING, required=true, description="the display text of the network")
    private String displayText;

    @Parameter(name=ApiConstants.NETWORK_OFFERING_ID, type=CommandType.UUID, entityType = NetworkOfferingResponse.class,
            required=true, description="the network offering id")
    private Long networkOfferingId;

    @Parameter(name=ApiConstants.ZONE_ID, type=CommandType.UUID, entityType = ZoneResponse.class,
            required=true, description="the Zone ID for the network")
    private Long zoneId;

    @Parameter(name=ApiConstants.PHYSICAL_NETWORK_ID, type=CommandType.UUID, entityType = PhysicalNetworkResponse.class,
            description="the Physical Network ID the network belongs to")
    private Long physicalNetworkId;

    @Parameter(name=ApiConstants.GATEWAY, type=CommandType.STRING, description="the gateway of the network. Required " +
            "for Shared networks and Isolated networks when it belongs to VPC")
    private String gateway;

    @Parameter(name=ApiConstants.NETMASK, type=CommandType.STRING, description="the netmask of the network. Required " +
            "for Shared networks and Isolated networks when it belongs to VPC")
    private String netmask;

    @Parameter(name=ApiConstants.START_IP, type=CommandType.STRING, description="the beginning IP address in the network IP range")
    private String startIp;

    @Parameter(name=ApiConstants.END_IP, type=CommandType.STRING, description="the ending IP address in the network IP" +
            " range. If not specified, will be defaulted to startIP")
    private String endIp;

    @Parameter(name=ApiConstants.VLAN, type=CommandType.STRING, description="the ID or VID of the network")
    private String vlan;

    @Parameter(name=ApiConstants.NETWORK_DOMAIN, type=CommandType.STRING, description="network domain")
    private String networkDomain;

    @Parameter(name=ApiConstants.ACL_TYPE, type=CommandType.STRING, description="Access control type; supported values" +
            " are account and domain. In 3.0 all shared networks should have aclType=Domain, and all Isolated networks" +
            " - Account. Account means that only the account owner can use the network, domain - all accouns in the domain can use the network")
    private String aclType;

    @Parameter(name=ApiConstants.ACCOUNT, type=CommandType.STRING, description="account who will own the network")
    private String accountName;

    @Parameter(name=ApiConstants.PROJECT_ID, type=CommandType.UUID, entityType = ProjectResponse.class,
            description="an optional project for the ssh key")
    private Long projectId;

    @Parameter(name=ApiConstants.DOMAIN_ID, type=CommandType.UUID, entityType = DomainResponse.class,
            description="domain ID of the account owning a network")
    private Long domainId;

    @Parameter(name=ApiConstants.SUBDOMAIN_ACCESS, type=CommandType.BOOLEAN, description="Defines whether to allow" +
            " subdomains to use networks dedicated to their parent domain(s). Should be used with aclType=Domain, defaulted to allow.subdomain.network.access global config if not specified")
    private Boolean subdomainAccess;

    @Parameter(name=ApiConstants.VPC_ID, type=CommandType.UUID, entityType = VpcResponse.class,
            description="the VPC network belongs to")
    private Long vpcId;


    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////
    public Long getNetworkOfferingId() {
        return networkOfferingId;
    }

    public String getGateway() {
        return gateway;
    }

    public String getVlan() {
        return vlan;
    }

    public String getAccountName() {
        return accountName;
    }

    public Long getDomainId() {
        return domainId;
    }

    public String getNetmask() {
        return netmask;
    }

    public String getStartIp() {
        return startIp;
    }

    public String getEndIp() {
        return endIp;
    }

    public String getNetworkName() {
        return name;
    }

    public String getDisplayText() {
        return displayText;
    }

    public String getNetworkDomain() {
        return networkDomain;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getAclType() {
        return aclType;
    }

    public Boolean getSubdomainAccess() {
        return subdomainAccess;
    }

    public Long getVpcId() {
        return vpcId;
    }

    public Long getZoneId() {
        Long physicalNetworkId = getPhysicalNetworkId();

        if (physicalNetworkId == null && zoneId == null) {
            throw new InvalidParameterValueException("Zone id is required");
        }

        return zoneId;
    }

    public Long getPhysicalNetworkId() {
        NetworkOffering offering = _configService.getNetworkOffering(networkOfferingId);
        if (offering == null) {
            throw new InvalidParameterValueException("Unable to find network offering by id " + networkOfferingId);
        }

        if (physicalNetworkId != null) {
            if (offering.getGuestType() == GuestType.Shared) {
                return physicalNetworkId;
            } else {
                throw new InvalidParameterValueException("Physical network id can be specified for networks of guest ip type " + GuestType.Shared + " only.");
            }
        } else {
            if (zoneId == null) {
                throw new InvalidParameterValueException("ZoneId is required as physicalNetworkId is null");
            }
            return _networkService.findPhysicalNetworkId(zoneId, offering.getTags(), offering.getTrafficType());
        }
    }

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////
    @Override
    public String getCommandName() {
        return s_name;
    }

    @Override
    public long getEntityOwnerId() {
        Long accountId = finalyzeAccountId(accountName, domainId, projectId, true);
        if (accountId == null) {
            return UserContext.current().getCaller().getId();
        }

        return accountId;
    }

    @Override
    // an exception thrown by createNetwork() will be caught by the dispatcher.
    public void execute() throws InsufficientCapacityException, ConcurrentOperationException, ResourceAllocationException{
        Network result = _networkService.createGuestNetwork(this);
        if (result != null) {
            NetworkResponse response = _responseGenerator.createNetworkResponse(result);
            response.setResponseName(getCommandName());
            this.setResponseObject(response);
        }else {
            throw new ServerApiException(ApiErrorCode.INTERNAL_ERROR, "Failed to create network");
        }
    }
}
