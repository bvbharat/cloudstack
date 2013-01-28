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

import java.util.HashMap;

import com.cloud.utils.db.SearchCriteria.Op;

public class Condition<T, K> {
    Where<T, K> _where;
    Attribute _attr;
    String _as;
    SearchCriteria.Op _op;
    String _paramName;
    
    protected Condition(Where<T, K> where, Attribute attr, String as) {
        assert (where != null) : "What am I going to return to the user when Where is null?";
        assert (attr != null) : "What's the point of giving me a null attribute?";
        _where = where;
        _attr = attr;
        _as = as;
    }
    
    protected NextWhere<T, K> set(Op op, String paramName) {
        _op = op;
        _paramName = paramName;
        Where<T, K> where = _where;
        _where = null;
        return where;
    }
    
    public NextWhere<T, K> eq(String paramName) {
        return set(Op.EQ, paramName);
    }
    
    public NextWhere<T, K> lt(String paramName) {
        return set(Op.LT, paramName);
    }
    
    public NextWhere<T, K> lteq(String paramName) {
        return set(Op.LTEQ, paramName);
    }
    
    public NextWhere<T, K> gt(String paramName) {
        return set(Op.GT, paramName);
    }
    
    public NextWhere<T, K> isNull() {
        return set(Op.NULL, null);
    }
    
    public NextWhere<T, K> isNotNull() {
        return set(Op.NNULL, null);
    }
    
    public NextWhere<T, K> in(String paramName) {
        _op = Op.IN;
        _paramName = paramName;
        return _where;
    }
    
    protected String getParamName() {
        assert (_paramName instanceof String) : "Well, how can we get back a parameter name if it was not assigned one?";
        return _paramName;
    }
    
    @Override
    public boolean equals(Object obj) {
        return _paramName.equals(obj);
    }
    
    @Override
    public int hashCode() {
        return _paramName.hashCode();
    }
    
    public void toSql(StringBuilder builder, HashMap<String, Object[]> values) {
        if (_as != null) {
            builder.append(_as);
        } else {
            builder.append(_attr.table);
        }
        builder.append(".").append(_attr.columnName);
    }
   
}
