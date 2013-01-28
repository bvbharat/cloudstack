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
package com.cloud.resource.storage;

import com.cloud.agent.api.storage.CreateAnswer;
import com.cloud.agent.api.storage.CreateCommand;
import com.cloud.agent.api.storage.DestroyAnswer;
import com.cloud.agent.api.storage.DestroyCommand;
import com.cloud.agent.api.storage.DownloadAnswer;
import com.cloud.agent.api.storage.PrimaryStorageDownloadCommand;
import com.cloud.resource.ServerResource;

/**
 * a primary storage.
 *
 */
public interface PrimaryStorageHeadResource extends ServerResource {
    /**
     * Downloads the template to the primary storage.
     * @param cmd
     * @return
     */
    DownloadAnswer execute(PrimaryStorageDownloadCommand cmd);
    
    /**
     * Creates volumes for the VM.
     * @param cmd
     * @return
     */
    CreateAnswer execute(CreateCommand cmd);
    
    /**
     * Destroys volumes for the VM.
     * @param cmd
     * @return
     */
    DestroyAnswer execute(DestroyCommand cmd);
}
