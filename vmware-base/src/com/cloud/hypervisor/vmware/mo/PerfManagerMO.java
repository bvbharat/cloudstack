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
package com.cloud.hypervisor.vmware.mo;

import java.util.Calendar;

import com.cloud.hypervisor.vmware.util.VmwareContext;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.PerfCompositeMetric;
import com.vmware.vim25.PerfCounterInfo;
import com.vmware.vim25.PerfEntityMetricBase;
import com.vmware.vim25.PerfInterval;
import com.vmware.vim25.PerfMetricId;
import com.vmware.vim25.PerfProviderSummary;
import com.vmware.vim25.PerfQuerySpec;

public class PerfManagerMO extends BaseMO {
    public PerfManagerMO(VmwareContext context, ManagedObjectReference mor) {
        super(context, mor);
    }
    
    public PerfManagerMO(VmwareContext context, String morType, String morValue) {
        super(context, morType, morValue);
    }
    
    public void createPerfInterval(PerfInterval interval) throws Exception {
        _context.getService().createPerfInterval(_mor, interval);
    }
    
    public PerfMetricId[] queryAvailablePerfMetric(ManagedObjectReference morEntity, Calendar beginTime, 
        Calendar endTime, Integer intervalId) throws Exception {
        
        return _context.getService().queryAvailablePerfMetric(_mor, morEntity, beginTime, endTime, intervalId);
    }

    public PerfCompositeMetric queryPerfComposite(PerfQuerySpec spec) throws Exception {
        return _context.getService().queryPerfComposite(_mor, spec);
    }
    
    public PerfCounterInfo[] queryPerfCounter(int[] counterId) throws Exception {
        return _context.getService().queryPerfCounter(_mor, counterId);
    }
    
    public PerfCounterInfo[] queryPerfCounterByLevel(int level) throws Exception {
        return _context.getService().queryPerfCounterByLevel(_mor, level);
    }
    
    public PerfProviderSummary queryPerfProviderSummary(ManagedObjectReference morEntity) throws Exception {
        return _context.getService().queryPerfProviderSummary(_mor, morEntity); 
    }

    public PerfEntityMetricBase[] queryPerf(PerfQuerySpec[] specs) throws Exception {
        return _context.getService().queryPerf(_mor, specs);
    }
    
    public void removePerfInterval(int samplePeriod) throws Exception {
        _context.getService().removePerfInterval(_mor, samplePeriod);
    }
    
    public void updatePerfInterval(PerfInterval interval) throws Exception {
        _context.getService().updatePerfInterval(_mor, interval);
    }
    
    public PerfCounterInfo[] getCounterInfo() throws Exception {
        return (PerfCounterInfo[])_context.getServiceUtil().getDynamicProperty(_mor, "perfCounter");
    }
    
    public PerfInterval[] getIntervalInfo() throws Exception {
        return (PerfInterval[])_context.getServiceUtil().getDynamicProperty(_mor, "historicalInterval");
    }
}
