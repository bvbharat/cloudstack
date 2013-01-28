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
package com.cloud.storage;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.cloud.cluster.CheckPointManager;
import com.cloud.cluster.CleanupMaid;
import com.cloud.server.ManagementServer;
import com.cloud.storage.dao.VolumeDao;
import com.cloud.utils.component.ComponentLocator;
import com.cloud.utils.db.Transaction;
import com.cloud.utils.fsm.NoTransitionException;
import com.cloud.utils.fsm.StateMachine2;
import com.cloud.vm.VMInstanceVO;
import com.cloud.vm.VirtualMachine;
import com.cloud.vm.VirtualMachineManager;
import com.cloud.vm.dao.VMInstanceDao;

public class StorageMigrationCleanupMaid implements CleanupMaid {
	 private static final Logger s_logger = Logger.getLogger(StorageMigrationCleanupMaid.class);
	public static enum StorageMigrationState {
		MIGRATING,
		MIGRATINGFAILED,
		MIGRATINGSUCCESS;
	}
	
	private List<Long> _volumesIds = new ArrayList<Long>();
	private StorageMigrationState _migrateState;

	public StorageMigrationCleanupMaid() {
		
	}
	
	public StorageMigrationCleanupMaid(StorageMigrationState state, List<Long> volumes) {
		_migrateState = state;
		_volumesIds = volumes;
	}
	
	public void updateStaste(StorageMigrationState state) {
		_migrateState = state;
	}
	
	@Override
	public int cleanup(CheckPointManager checkPointMgr) {
		StateMachine2<Volume.State, Volume.Event, Volume> _stateMachine = Volume.State.getStateMachine();
		
		ComponentLocator locator = ComponentLocator.getLocator(ManagementServer.Name);
		VolumeDao volDao = locator.getDao(VolumeDao.class);
		VMInstanceDao vmDao = locator.getDao(VMInstanceDao.class);
		VirtualMachineManager vmMgr = locator.getManager(VirtualMachineManager.class);
		Long vmInstanceId = null;
		boolean success = true;
		Transaction txn = Transaction.open(Transaction.CLOUD_DB);

		try {
			txn.start();
			for (Long volumeId : _volumesIds) {
				VolumeVO volume = volDao.findById(volumeId);
				if (volume == null) {
					continue;
				}
				vmInstanceId = volume.getInstanceId();
				if (_migrateState == StorageMigrationState.MIGRATING && volume.getState() == Volume.State.Migrating) {
					try {
						_stateMachine.transitTo(volume, Volume.Event.OperationFailed, null, volDao);
					} catch (NoTransitionException e) {
						s_logger.debug("Failed to transit volume state: " + e.toString());
						success = false;
						break;
					}
				}
			}
			if (vmInstanceId != null) {
				VMInstanceVO vm = vmDao.findById(vmInstanceId);
				if (vm != null && vm.getState() == VirtualMachine.State.Migrating) {
					try {
						vmMgr.stateTransitTo(vm, VirtualMachine.Event.AgentReportStopped, null);
					} catch (NoTransitionException e) {
						s_logger.debug("Failed to transit vm state");
						success = false;
					}
				}
			}
			
			if (success) {
				txn.commit();
			} 
		} catch (Exception e) {
			s_logger.debug("storage migration cleanup failed:" + e.toString());
			txn.rollback();
		}finally {
			txn.close();
		}

		return 0;
	}

	@Override
	public String getCleanupProcedure() {
		return null;
	}

}
