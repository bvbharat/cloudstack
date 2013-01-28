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
package com.cloud.agent.configuration;

import java.util.List;
import java.util.Map;

import com.cloud.utils.component.Adapter;
import com.cloud.utils.component.ComponentLibraryBase;
import com.cloud.utils.component.ComponentLocator.ComponentInfo;
import com.cloud.utils.component.Manager;
import com.cloud.utils.component.PluggableService;
import com.cloud.utils.db.GenericDao;

public class AgentComponentLibraryBase extends ComponentLibraryBase {
    @Override
    public Map<String, ComponentInfo<GenericDao<?, ?>>> getDaos() {
        return null;
    }

    @Override
    public Map<String, ComponentInfo<Manager>> getManagers() {
        if (_managers.size() == 0) {
            populateManagers();
        }
        return _managers;
    }

    @Override
    public Map<String, List<ComponentInfo<Adapter>>> getAdapters() {
        if (_adapters.size() == 0) {
            populateAdapters();
        }
        return _adapters;
    }

    @Override
    public Map<Class<?>, Class<?>> getFactories() {
        return null;
    }

    protected void populateManagers() {
        // addManager("StackMaidManager", StackMaidManagerImpl.class);
    }

    protected void populateAdapters() {

    }

    protected void populateServices() {

    }

    @Override
    public Map<String, ComponentInfo<PluggableService>> getPluggableServices() {
        if (_pluggableServices.size() == 0) {
            populateServices();
        }
        return _pluggableServices;
    }

}
