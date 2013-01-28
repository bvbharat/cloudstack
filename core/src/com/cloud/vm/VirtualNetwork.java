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
package com.cloud.vm;

import com.cloud.network.Networks.IsolationType;
import com.cloud.network.Networks.Mode;

/**
 * VirtualNetwork describes from a management level the
 * machine.
 */
public class VirtualNetwork {
    /**
     * The gateway for this network.
     */
    public String gateway;
    
    /**
     * Netmask
     */
    public String netmask;
    
    /**
     * ip address.  null if mode is DHCP.
     */
    public String ip;
    
    /**
     * Mac Address.
     */
    public String mac;
    
    /**
     * rate limit on this network.  -1 if no limit.
     */
    public long rate;
    
    /**
     * tag for virtualization.
     */
    public String tag;
    
    /**
     * mode to acquire ip address.
     */
    public Mode mode;
    
    /**
     * Isolation method for networking.
     */
    public IsolationType method;
    
    public boolean firewalled;
    
    public int[] openPorts;
    
    public int[] closedPorts;
}
