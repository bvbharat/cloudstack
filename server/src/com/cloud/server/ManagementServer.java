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
package com.cloud.server;

import java.util.Date;
import java.util.List;

import com.cloud.event.EventVO;
import com.cloud.host.HostVO;
import com.cloud.info.ConsoleProxyInfo;
import com.cloud.storage.GuestOSVO;
import com.cloud.storage.StoragePoolVO;
import com.cloud.utils.Pair;
import com.cloud.utils.component.PluggableService;
import com.cloud.vm.VirtualMachine;

/**
 */
public interface ManagementServer extends ManagementService, PluggableService  {
    
    /**
     * returns the instance id of this management server.
     * 
     * @return id of the management server
     */
    long getId();
    
    /**
     * Fetches the version of cloud stack
    */
    @Override
    String getVersion();

    /**
     * Retrieves a host by id
     * 
     * @param hostId
     * @return Host
     */
    HostVO getHostBy(long hostId);

    /**
     * Retrieves all Events between the start and end date specified
     * 
     * @param userId
     *            unique id of the user, pass in -1 to retrieve events for all users
     * @param accountId
     *            unique id of the account (which could be shared by many users), pass in -1 to retrieve events for all accounts
     * @param domainId
     *            the id of the domain in which to search for users (useful when -1 is passed in for userId)
     * @param type
     *            the type of the event.
     * @param level
     *            INFO, WARN, or ERROR
     * @param startDate
     *            inclusive.
     * @param endDate
     *            inclusive. If date specified is greater than the current time, the system will use the current time.
     * @return List of events
     */
    List<EventVO> getEvents(long userId, long accountId, Long domainId, String type, String level, Date startDate, Date endDate);

    //FIXME - move all console proxy related commands to corresponding managers
    ConsoleProxyInfo getConsoleProxyForVm(long dataCenterId, long userVmId);

    String getConsoleAccessUrlRoot(long vmId);
    
    GuestOSVO getGuestOs(Long guestOsId);

    /**
     * Returns the vnc port of the vm.
     * 
     * @param VirtualMachine vm
     * @return the vnc port if found; -1 if unable to find.
     */
    Pair<String, Integer> getVncPort(VirtualMachine vm);

    public long getMemoryOrCpuCapacityByHost(Long hostId, short capacityType);

    Pair<List<StoragePoolVO>, Integer> searchForStoragePools(Criteria c);

    String getHashKey();
    
    public void enableAdminUser(String password);
}
