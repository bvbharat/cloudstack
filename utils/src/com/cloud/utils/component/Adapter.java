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
 * Adapter defines methods for pluggable code within the Cloud Stack. An
 * Adapters are a departure from regular structured programming.
 */
public interface Adapter {

    /**
     * configure is called when an adapter is initialized.
     * 
     * @param name
     *            The name of the adapter.
     * @param params
     *            A map of configuration parameters.
     * @return Returning false means the configuration did not go well and the
     *         adapter can not be used.
     */
    boolean configure(String name, Map<String, Object> params) throws ConfigurationException;

    /**
     * 
     */
    String getName();

    /**
     * startAdapter() signals the adapter that it can start.
     * 
     * @return true if the adapter can start, false otherwise.
     */
    boolean start();

    /**
     * stopAdapter() signals the adapter that it should be shutdown. Returns
     * false means that the adapter is not ready to be stopped and should be
     * called again.
     * 
     * @return true if the adapter can stop, false indicates the adapter is not
     *         ready to stop.
     */
    boolean stop();
}
