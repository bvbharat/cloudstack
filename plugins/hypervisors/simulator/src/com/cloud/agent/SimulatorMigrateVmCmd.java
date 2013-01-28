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
package com.cloud.agent;

public class SimulatorMigrateVmCmd extends SimulatorCmd {

	private static final long serialVersionUID = 1L;

    private String destIp;

	private String vmName;
	private long ramSize;
	private int cpuCount;
	private int utilization;

	public SimulatorMigrateVmCmd(String testCase) {
		super(testCase);
	}

	public String getDestIp() {
		return destIp;
	}

	public void setDestIp(String destIp) {
		this.destIp = destIp;
	}

	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}

	public long getRamSize() {
		return ramSize;
	}

	public void setRamSize(long ramSize) {
		this.ramSize = ramSize;
	}

	public int getCpuCount() {
		return cpuCount;
	}

	public void setCpuCount(int cpuCount) {
		this.cpuCount = cpuCount;
	}

	public int getUtilization() {
		return utilization;
	}

	public void setUtilization(int utilization) {
		this.utilization = utilization;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("SimulatorMigrateVmCmd {").append("vm: ").append(getVmName());
		sb.append(", destIp: ").append(getDestIp()).append(", ramSize: ").append(getRamSize());
		sb.append(", cpuCount: ").append(getCpuCount()).append(", utilization: ").append(getUtilization());
		sb.append("}");

		return sb.toString();
	}
}
