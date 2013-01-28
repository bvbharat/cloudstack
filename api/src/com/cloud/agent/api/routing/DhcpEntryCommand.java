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
package com.cloud.agent.api.routing;


public class DhcpEntryCommand extends NetworkElementCommand {

    String vmMac;
    String vmIpAddress;
    String vmName;
    String dns;
    String gateway;
    String nextServer;
    String defaultRouter;
    String staticRoutes;
    String defaultDns;


    protected DhcpEntryCommand() {

    }

    @Override
    public boolean executeInSequence() {
        return true;
    }

    public DhcpEntryCommand(String vmMac, String vmIpAddress, String vmName) {
        this.vmMac = vmMac;
        this.vmIpAddress = vmIpAddress;
        this.vmName = vmName;
    }

    public DhcpEntryCommand(String vmMac, String vmIpAddress, String vmName, String dns, String gateway) {
        this(vmMac, vmIpAddress, vmName);
        this.dns = dns;
        this.gateway = gateway;
    }

    public String getDns() {
        return dns;
    }

    public String getGateway() {
        return gateway;
    }

    public String getVmMac() {
        return vmMac;
    }

    public String getVmIpAddress() {
        return vmIpAddress;
    }

    public String getVmName() {
        return vmName;
    }

    public void setNextServer(String ip) {
        nextServer = ip;
    }

    public String getNextServer() {
        return nextServer;
    }

    public String getDefaultRouter() {
        return defaultRouter;
    }

    public void setDefaultRouter(String defaultRouter) {
        this.defaultRouter = defaultRouter;
    }

    public String getStaticRoutes() {
        return staticRoutes;
    }

    public void setStaticRoutes(String staticRoutes) {
        this.staticRoutes = staticRoutes;
    }

	public String getDefaultDns() {
		return defaultDns;
	}

	public void setDefaultDns(String defaultDns) {
		this.defaultDns = defaultDns;
	}
}
