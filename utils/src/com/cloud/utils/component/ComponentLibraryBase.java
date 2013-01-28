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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cloud.utils.Pair;
import com.cloud.utils.component.ComponentLocator.ComponentInfo;
import com.cloud.utils.db.GenericDao;

public abstract class ComponentLibraryBase implements ComponentLibrary {
    
    protected final Map<String, ComponentInfo<GenericDao<?, ? extends Serializable>>> _daos = new LinkedHashMap<String, ComponentInfo<GenericDao<?, ? extends Serializable>>>();

    protected ComponentInfo<? extends GenericDao<?, ? extends Serializable>> addDao(String name, Class<? extends GenericDao<?, ? extends Serializable>> clazz) {
        return addDao(name, clazz, new ArrayList<Pair<String, Object>>(), true);
    }

    protected ComponentInfo<? extends GenericDao<?, ? extends Serializable>> addDao(String name, Class<? extends GenericDao<?, ? extends Serializable>> clazz, List<Pair<String, Object>> params, boolean singleton) {
        ComponentInfo<GenericDao<?, ? extends Serializable>> componentInfo = new ComponentInfo<GenericDao<?, ? extends Serializable>>(name, clazz, params, singleton);
        for (String key : componentInfo.getKeys()) {
            _daos.put(key, componentInfo);
        }
        return componentInfo;
    }

    protected Map<String, ComponentInfo<Manager>> _managers = new LinkedHashMap<String, ComponentInfo<Manager>>();
    protected Map<String, List<ComponentInfo<Adapter>>> _adapters = new LinkedHashMap<String, List<ComponentInfo<Adapter>>>();
    protected Map<String, ComponentInfo<PluggableService>> _pluggableServices = new LinkedHashMap<String, ComponentInfo<PluggableService>>();

    protected ComponentInfo<Manager> addManager(String name, Class<? extends Manager> clazz, List<Pair<String, Object>> params, boolean singleton) {
        ComponentInfo<Manager> info = new ComponentInfo<Manager>(name, clazz, params, singleton);
        for (String key : info.getKeys()) {
            _managers.put(key, info);
        }
        return info;
    }
    
    protected ComponentInfo<Manager> addManager(String name, Class<? extends Manager> clazz) {
        return addManager(name, clazz, new ArrayList<Pair<String, Object>>(), true);
    }
    
    protected <T> List<ComponentInfo<Adapter>> addAdapterChain(Class<T> interphace, List<Pair<String, Class<? extends T>>> adapters) {
        ArrayList<ComponentInfo<Adapter>> lst = new ArrayList<ComponentInfo<Adapter>>(adapters.size());
        for (Pair<String, Class<? extends T>> adapter : adapters) {
            @SuppressWarnings("unchecked")
            Class<? extends Adapter> clazz = (Class<? extends Adapter>)adapter.second();
            lst.add(new ComponentInfo<Adapter>(adapter.first(), clazz));
        }
        _adapters.put(interphace.getName(), lst);
        return lst;
    }
    
    protected <T> void addAdapter(Class<T> interphace, String name, Class<? extends T> adapterClass) {
    	List<ComponentInfo<Adapter>> lst = _adapters.get(interphace.getName());
    	if (lst == null) {
    		addOneAdapter(interphace, name, adapterClass);
    	} else {
    		@SuppressWarnings("unchecked")
    		Class<? extends Adapter> clazz = (Class<? extends Adapter>)adapterClass;
    		lst.add(new ComponentInfo<Adapter>(name, clazz));
    	}
    }
    
    protected <T> ComponentInfo<Adapter> addOneAdapter(Class<T> interphace, String name, Class<? extends T> adapterClass) {
        List<Pair<String, Class<? extends T>>> adapters = new ArrayList<Pair<String, Class<? extends T>>>();
        adapters.add(new Pair<String, Class<? extends T>>(name, adapterClass));
        return addAdapterChain(interphace, adapters).get(0);
    }
    

    protected <T> ComponentInfo<PluggableService> addService(String name, Class<T> serviceInterphace, Class<? extends PluggableService> clazz, List<Pair<String, Object>> params, boolean singleton) {
        ComponentInfo<PluggableService> info = new ComponentInfo<PluggableService>(name, clazz, params, singleton);
        _pluggableServices.put(serviceInterphace.getName(), info);
        return info;
    }
    
    protected <T> ComponentInfo<PluggableService> addService(String name, Class<T> serviceInterphace, Class<? extends PluggableService> clazz) {
        return addService(name, serviceInterphace, clazz, new ArrayList<Pair<String, Object>>(), true);
    }
 }
