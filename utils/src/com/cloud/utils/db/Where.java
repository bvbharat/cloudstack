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

import java.util.ArrayList;
import java.util.List;

/**
 * Where implements any list of search conditions.
 *
 */
public class Where<T, K> implements FirstWhere<T, K>, NextWhere<T, K> {
    QueryBuilder<T, K> _builder;
    List<Object> _conditions = new ArrayList<Object>();
    
    protected Where(QueryBuilder<T, K> builder) {
        _builder = builder;
    }
    
    @Override
    public Condition<T, K> field(Object useless, String as) {
        Attribute attr = _builder.getSpecifiedAttribute();
        Condition<T, K> cond = new Condition<T, K>(this, attr, as);
        _conditions.add(cond);
        return cond;
    }
    
    @Override
    public Where<T, K> and() {
        _conditions.add(" (");
        return this;
    }
    
    @Override
    public Where<T, K> or() {
        _conditions.add(" OR ");
        return this;
    }

    @Override
    public NextWhere<T, K> not() {
        _conditions.add(" NOT ");
        return this;
    }

    @Override
    public NextWhere<T, K> text(String text, String... paramNames) {
        assert ((paramNames.length == 0 && !text.contains("?")) || (text.matches("\\?.*{" + paramNames.length + "}")));
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Condition<T, K> field(Object useless) {
        return field(useless, null);
    }

    @Override
    public FirstWhere<T, K> op() {
        _conditions.add("(");
        return this;
    }

    @Override
    public void done() {
    }
}
