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
package com.cloud.cluster;

/**
 * 
 * task.  The state is serialized and stored.  When cleanup is required
 * CleanupMaid is instantiated from the stored data and cleanup() is called.
 *
 */
public interface CleanupMaid {
    /**
     * cleanup according the state that was stored.
     * 
     * @return 0 indicates cleanup was successful.  Negative number
     * indicates the cleanup was unsuccessful but don't retry.  Positive number
     * indicates the cleanup was unsuccessful and retry in this many seconds.
     */
    int cleanup(CheckPointManager checkPointMgr);
    
    
    /**
     * returned here is recorded. 
     * @return
     */
    String getCleanupProcedure();
}
