// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// the License.  You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package com.cloud.utils.backoff.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.ejb.Local;

import com.cloud.utils.NumbersUtil;
import com.cloud.utils.backoff.BackoffAlgorithm;

/**
 * Implementation of the Agent Manager.  This class controls the connection
 * 
 * @config
 * {@table 
 *    || Param Name | Description | Values | Default ||
 *    || seconds    | seconds to sleep | integer | 5 ||
 *  }
 **/ 
@Local(value={BackoffAlgorithm.class})
public class ConstantTimeBackoff implements BackoffAlgorithm, ConstantTimeBackoffMBean {
    int _count = 0;
    long _time;
    String _name;
    ConcurrentHashMap<String, Thread> _asleep = new ConcurrentHashMap<String, Thread>();

    @Override
    public void waitBeforeRetry() {
        _count++;
        try {
            Thread current = Thread.currentThread();
            _asleep.put(current.getName(), current);
            Thread.sleep(_time);
            _asleep.remove(current.getName());
        } catch(InterruptedException e) {
            
        }
        return;
    }

    @Override
    public void reset() {
        _count = 0;
    }

    @Override
    public boolean configure(String name, Map<String, Object> params) {
        _name = name;
        _time = NumbersUtil.parseLong((String)params.get("seconds"), 5) * 1000;
        return true;
    }

    @Override
    public String getName() {
        return _name;
    }
    
    @Override
    public Collection<String> getWaiters() {
        return _asleep.keySet();
    }
    
    @Override
    public boolean wakeup(String threadName) {
        Thread th = _asleep.get(threadName);
        if (th != null) {
            th.interrupt();
            return true;
        }
        
        return false;
    }

    @Override
    public boolean start() {
        _count = 0;
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public long getTimeToWait() {
        return _time;
    }

    @Override
    public void setTimeToWait(long seconds) {
        _time = seconds * 1000;
    }
}
