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

import java.util.List;
import java.util.Map;

import com.cloud.utils.component.ComponentLocator.ComponentInfo;
import com.cloud.utils.db.GenericDao;

/**
 * ComponentLibrary specifies the implementation classes that a server needs
 * attribute of the server element within components.xml.  ComponentLocator
 * first loads the implementations specified here, then, it loads the 
 * implementations from components.xml.  If an interface is specified in both
 * within the components.xml overrides the one within ComponentLibrary.
 *
 */
public interface ComponentLibrary {    
    /**
     * @return all of the daos
     */
    Map<String, ComponentInfo<GenericDao<?,?>>> getDaos();
    
    /**
     * @return all of the Managers
     */
    Map<String, ComponentInfo<Manager>> getManagers();
    
    /**
     * @return all of the adapters
     */
    Map<String, List<ComponentInfo<Adapter>>> getAdapters();
    
    Map<Class<?>, Class<?>> getFactories();
    
    /**
     * @return all the services
     * 
     */
    Map<String, ComponentInfo<PluggableService>> getPluggableServices();
}
