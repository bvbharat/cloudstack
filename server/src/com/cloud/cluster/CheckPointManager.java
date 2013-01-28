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
 * TaskManager helps business logic deal with clustering failover.
 * Say you're writing code that introduces an inconsistent state over
 * of your operation?  Who will come back to cleanup this state?  TaskManager
 * with different content during your process.  If the server dies, TaskManager
 * running elsewhere.  If there are no clustered servers, then TaskManager will
 * cleanup when the dead server resumes.
 *
 */
public interface CheckPointManager {
    /**
     * responsible for cleaning up.
     * 
     * @param context context information to be stored.
     * @return Check point id.
     */
    long pushCheckPoint(CleanupMaid context);
    
    /**
     * update the task with new context
     * @param taskId
     * @param updatedContext new updated context.
     */
    void updateCheckPointState(long taskId, CleanupMaid updatedContext);
    
    
    /**
     * removes the task as it is completed.
     * 
     * @param taskId
     */
    void popCheckPoint(long taskId);
}
