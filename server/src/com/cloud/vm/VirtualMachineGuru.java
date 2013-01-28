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

import com.cloud.agent.api.StopAnswer;
import com.cloud.agent.api.to.NicTO;
import com.cloud.agent.api.to.VirtualMachineTO;
import com.cloud.agent.manager.Commands;
import com.cloud.deploy.DeployDestination;
import com.cloud.exception.ConcurrentOperationException;
import com.cloud.exception.InsufficientCapacityException;
import com.cloud.exception.InsufficientNetworkCapacityException;
import com.cloud.exception.ResourceUnavailableException;
import com.cloud.network.Network;

/**
 * A VirtualMachineGuru knows how to process a certain type of virtual machine.
 *
 */
public interface VirtualMachineGuru<T extends VirtualMachine> {
    /**
     * Find the virtual machine by name.
     * @param name
     * @return virtual machine.
     */
    T findByName(String name);
    
    T findById(long id);
    
    T persist(T vm);
    
    boolean finalizeVirtualMachineProfile(VirtualMachineProfile<T> profile, DeployDestination dest, ReservationContext context);
    
    /**
     * finalize the virtual machine deployment.
     * @param cmds commands that were created.
     * @param profile virtual machine profile.
     * @param dest destination to send the command.
     * @return true if everything checks out.  false if not and we should try again.
     */
    boolean finalizeDeployment(Commands cmds, VirtualMachineProfile<T> profile, DeployDestination dest, ReservationContext context) throws ResourceUnavailableException;
    
    /**
     * Check the deployment results.
     * @param cmds commands and answers that were sent.
     * @param profile virtual machine profile.
     * @param dest destination it was sent to.
     * @return true if deployment was fine; false if it didn't go well.
     */
    boolean finalizeStart(VirtualMachineProfile<T> profile, long hostId, Commands cmds, ReservationContext context);
    
    boolean finalizeCommandsOnStart(Commands cmds, VirtualMachineProfile<T> profile);
    
    void finalizeStop(VirtualMachineProfile<T> profile, StopAnswer answer);
    
    void finalizeExpunge(T vm);
    
    /**
     * Returns the id parsed from the name.  If it cannot parse the name,
     * then return null.  This method is used to determine if this is
     * the right handler for this vm.
     * 
     * @param vmName vm name coming form the agent.
     * @return id if the handler works for this vm and can parse id.  null if not.
     */
    Long convertToId(String vmName);
    
    /**
     * Prepare for a nic to be plugged into the network.
     * @param network
     * @param nic
     * @param vm
     * @param context
     * @param dest TODO
     * @return
     * @throws ConcurrentOperationException
     * @throws ResourceUnavailableException
     * @throws InsufficientNetworkCapacityException
     */
    boolean plugNic(Network network, NicTO nic, VirtualMachineTO vm, 
            ReservationContext context, DeployDestination dest) throws ConcurrentOperationException, 
            ResourceUnavailableException, InsufficientCapacityException;
    
    /**
     * A nic is unplugged from this network.
     * @param network
     * @param nic
     * @param vm
     * @param context
     * @param dest TODO
     * @return
     * @throws ConcurrentOperationException
     * @throws ResourceUnavailableException
     */
    boolean unplugNic(Network network, NicTO nic, VirtualMachineTO vm, 
            ReservationContext context, DeployDestination dest) throws ConcurrentOperationException, ResourceUnavailableException;

    /**
     * Prepare Vm for Stop
     * @param profile
     * @return
     */
    void prepareStop(VirtualMachineProfile<T> profile);
}
