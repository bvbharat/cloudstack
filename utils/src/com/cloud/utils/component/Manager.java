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
package com.cloud.utils.component;

import java.util.Map;

import javax.naming.ConfigurationException;

/**
 * 
 * For now we only expose some simple methods. In the future, we can use this
 **/
public interface Manager {
    /**
     * Configuration with parameters. If there are background tasks, they
     * shouldn't be started yet. Wait for the start() call.
     * 
     * @param name
     *            The managers name.
     * @param params
     *            Configuration parameters.
     * @return true if the configuration was successful, false otherwise.
     */
    public boolean configure(String name, Map<String, Object> params) throws ConfigurationException;

    /**
     * Start any background tasks.
     * 
     * @return true if the tasks were started, false otherwise.
     */
    public boolean start();

    /**
     * Stop any background tasks.
     * 
     * @return true background tasks were stopped, false otherwise.
     */
    public boolean stop();

    /**
     * Get the name of this manager.
     * 
     * @return the name.
     */
    public String getName();
}
