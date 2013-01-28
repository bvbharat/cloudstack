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
package com.cloud.utils.db;

import java.lang.reflect.Field;

public class Select<S, T> {
    QueryBuilder<S,T> _builder;
    Class<T> _clazz;
    Attribute _attr;
    String _as;
    Field _field;
    
    protected Select(QueryBuilder<S, T> builder, Class<T> clazz, Attribute attr) {
        _builder = builder;
        _clazz = clazz;
        _attr = attr;
    }
    
    public QueryBuilder<S, T> into(String fieldName) {
        if (fieldName != null) {
            try {
                _field = _clazz.getDeclaredField(fieldName);
                _field.setAccessible(true);
            } catch (SecurityException e) {
                throw new RuntimeException("Unable to find " + fieldName + " in " + _clazz.getName(), e);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Unable to find " + fieldName + " in " + _clazz.getName(), e);
            }
        }
        return _builder;
    }
    
    public QueryBuilder<S, T> as(String as) {
        _as = as;
        return _builder;
    }
}
