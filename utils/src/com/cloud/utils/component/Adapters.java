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

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cloud.utils.EnumerationImpl;
import com.cloud.utils.component.ComponentLocator.ComponentInfo;

/**
 * the iterator even during dynamic reloading.
 * 
 **/
public class Adapters<T> implements Iterable<T> {
    protected Map<String, T> _map; 
    protected List<ComponentInfo<Adapter>> _infos;
    
    protected String      _name;

    public Adapters(String name, List<ComponentInfo<Adapter>> adapters) {
        _name = name;
        set(adapters);
    }

    /**
     * Get the adapter list name.
     * 
     * @return the name of the list of adapters.
     */
    public String getName() {
        return _name;
    }

    public Enumeration<T> enumeration() {
        return new EnumerationImpl<T>(_map.values().iterator());
    }
    
    @Override
    public Iterator<T> iterator() {
        return new EnumerationImpl<T>(_map.values().iterator());
    }

    protected Collection<T> get() {
        return _map.values();
    }
    
    protected void set(List<ComponentInfo<Adapter>> adapters) {
        HashMap<String, T> map = new LinkedHashMap<String, T>(adapters.size());
        for (ComponentInfo<Adapter> adapter : adapters) {
            @SuppressWarnings("unchecked")
            T t = (T)adapter.instance;
            map.put(adapter.getName(), t);
        }
        this._map = map;
        this._infos = adapters;
    }
    
    public T get(String name) {
        return _map.get(name);
    }

    public boolean isSet() {
        return _map.size() != 0;
    }
}
