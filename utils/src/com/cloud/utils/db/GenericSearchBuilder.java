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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;

import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.cloud.utils.db.SearchCriteria.Func;
import com.cloud.utils.db.SearchCriteria.Op;
import com.cloud.utils.db.SearchCriteria.SelectType;
import com.cloud.utils.exception.CloudRuntimeException;

/**
 * GenericSearchBuilder is used to build a search based on a VO object
 * a convenience class provided called SearchBuilder that provides
 * exactly that functionality.
 *
 * @param <T> VO object this Search is build for.
 * @param <K> Result object that should contain the results.
 */
public class GenericSearchBuilder<T, K> implements MethodInterceptor {
    final protected Map<String, Attribute> _attrs;
    
    protected ArrayList<Condition> _conditions;
    protected HashMap<String, JoinBuilder<GenericSearchBuilder<?, ?>>> _joins;
    protected ArrayList<Select> _selects;
    protected GroupBy<T, K> _groupBy = null;
    protected Class<T> _entityBeanType;
    protected Class<K> _resultType;
    protected SelectType _selectType;
    
    protected T _entity;
    protected ArrayList<Attribute> _specifiedAttrs;
    
    @SuppressWarnings("unchecked")
    protected GenericSearchBuilder(T entity, Class<K> clazz, Map<String, Attribute> attrs) {
        _entityBeanType = (Class<T>)entity.getClass();
        _resultType = clazz;
        
        _attrs = attrs;
        _entity = entity;
        _conditions = new ArrayList<Condition>();
        _joins = null;
        _specifiedAttrs = new ArrayList<Attribute>();
    }
    
    public T entity() {
        return _entity;
    }
    
    protected Attribute getSpecifiedAttribute() {
        assert(_entity != null && _specifiedAttrs != null && _specifiedAttrs.size() == 1) : "Now now, better specify an attribute or else we can't help you";
        return _specifiedAttrs.get(0);
    }

    public GenericSearchBuilder<T, K> selectField(Object... useless) {
        assert _entity != null : "SearchBuilder cannot be modified once it has been setup";
        assert _specifiedAttrs.size() > 0 : "You didn't specify any attributes";
   
        if (_selects == null) {
            _selects = new ArrayList<Select>();
        }
        
        for (Attribute attr : _specifiedAttrs) {
            Field field = null;
            try {
                field = _resultType.getDeclaredField(attr.field.getName());
                field.setAccessible(true);
            } catch (SecurityException e) {
            } catch (NoSuchFieldException e) {
            }
            _selects.add(new Select(Func.NATIVE, attr, field, null));
        }
        
        _specifiedAttrs.clear();
        
        return this;
    }
    
//    public GenericSearchBuilder<T, K> selectField(String joinName, Object... entityFields) {
//        JoinBuilder<GenericSearchBuilder<?, ?>> jb = _joins.get(joinName);
//
//    }
    
    /**
     * Specifies the field to select.
     * 
     * @param fieldName The field name of the result object to put the value of the field selected.  This can be null if you're selecting only one field and the result is not a complex object.
     * @param func function to place.
     * @param useless column to select.  Call this with this.entity() method.
     * @param params parameters to the function.
     * @return a SearchBuilder to build more search parts.
     */
    public GenericSearchBuilder<T, K> select(String fieldName, Func func, Object useless, Object... params) {
        assert _entity != null : "SearchBuilder cannot be modified once it has been setup";
        assert _specifiedAttrs.size() <= 1 : "You can't specify more than one field to search on";
        assert func.getCount() == -1 || (func.getCount() == (params.length + 1)) : "The number of parameters does not match the function param count for " + func;
        
        if (_selects == null) {
            _selects = new ArrayList<Select>();
        }
        
        Field field = null;
        if (fieldName != null) {
            try {
                field = _resultType.getDeclaredField(fieldName);
                field.setAccessible(true);
            } catch (SecurityException e) {
                throw new CloudRuntimeException("Unable to find " + fieldName, e);
            } catch (NoSuchFieldException e) {
                throw new CloudRuntimeException("Unable to find " + fieldName, e);
            }
        } else {
            assert _selects.size() == 0 : "You're selecting more than one item and yet is not providing a container class to put these items in.  So what do you expect me to do.  Spin magic?";
        }
        
        Select select = new Select(func, _specifiedAttrs.size() == 0 ? null : _specifiedAttrs.get(0), field, params);
        _selects.add(select);
        
        _specifiedAttrs.clear();
        
        return this;
    }
    
//    public GenericSearchBuilder<T, K> select(String joinName, String fieldName, Func func, Object useless, Object... params) {
//
//    }
    
    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        String name = method.getName();
		if (method.getAnnotation(Transient.class) == null) {
			if (name.startsWith("get")) {
				String fieldName = Character.toLowerCase(name.charAt(3)) + name.substring(4);
				set(fieldName);
				return null;
			} else if (name.startsWith("is")) {
				String fieldName = Character.toLowerCase(name.charAt(2)) + name.substring(3);
				set(fieldName);
				return null;
			} else {
				name = name.toLowerCase();
				for (String fieldName : _attrs.keySet()) {
					if (name.endsWith(fieldName.toLowerCase())) {
						set(fieldName);
						return null;
					}
				}
				assert false : "Perhaps you need to make the method start with get or is?";
			}
		}
        return methodProxy.invokeSuper(object, args);
    }
    
    protected void set(String name) {
        Attribute attr = _attrs.get(name);
        assert (attr != null) : "Searching for a field that's not there: " + name;
        _specifiedAttrs.add(attr);
    }
   
    /**
     * Adds an AND condition to the SearchBuilder.
     * 
     * @param name param name you will use later to set the values in this search condition.
     * @param useless SearchBuilder.entity().get*() which refers to the field that you're searching on.
     * @param op operation to apply to the field.
     * @return this
     */
    public GenericSearchBuilder<T, K> and(String name, Object useless, Op op) {
        constructCondition(name, " AND ", _specifiedAttrs.get(0), op);
        return this;
    }
    
    public GenericSearchBuilder<T, K> and() {
        constructCondition(null, " AND ", null, null);
        return this;
    }
    
    public GenericSearchBuilder<T, K> where() {
        return and();
    }
    
    public GenericSearchBuilder<T, K> or() {
        constructCondition(null, " OR ", null, null);
        return this;
    }
    
    public GenericSearchBuilder<T, K> where(String name, Object useless, Op op) {
        return and(name, useless, op);
    }
    
    public GenericSearchBuilder<T, K> left(String name, Object useless, Op op) {
        constructCondition(name, " ( ", _specifiedAttrs.get(0), op);
        return this;
    }
    
    public GenericSearchBuilder<T, K> op(String name, Object useless, Op op) {
        return left(name, useless, op);
    }
    
    public GenericSearchBuilder<T, K> openParen(String name, Object useless, Op op) {
        return left(name, useless, op);
    }
    
    public GroupBy<T, K> groupBy(Object... useless) {
        assert _groupBy == null : "Can't do more than one group bys";
        _groupBy = new GroupBy<T, K>(this);
        
        return _groupBy;
    }
    
    protected List<Attribute> getSpecifiedAttributes() {
        return _specifiedAttrs;
    }
    
    /**
     * Adds an OR condition to the SearchBuilder.
     * 
     * @param name param name you will use later to set the values in this search condition.
     * @param useless SearchBuilder.entity().get*() which refers to the field that you're searching on.
     * @param op operation to apply to the field.
     * @return this
     */
    public GenericSearchBuilder<T, K> or(String name, Object useless, Op op) {
        constructCondition(name, " OR ", _specifiedAttrs.get(0), op);
        return this;
    }
    
    public GenericSearchBuilder<T, K> join(String name, GenericSearchBuilder<?, ?> builder, Object useless, Object useless2, JoinBuilder.JoinType joinType) {
        assert _entity != null : "SearchBuilder cannot be modified once it has been setup";
        assert _specifiedAttrs.size() == 1 : "You didn't select the attribute.";
        assert builder._entity != null : "SearchBuilder cannot be modified once it has been setup";
        assert builder._specifiedAttrs.size() == 1 : "You didn't select the attribute.";
        assert builder != this : "You can't add yourself, can you?  Really think about it!";
        
        JoinBuilder<GenericSearchBuilder<?, ?>> t = new JoinBuilder<GenericSearchBuilder<?, ?>>(builder, _specifiedAttrs.get(0), builder._specifiedAttrs.get(0), joinType);
        if (_joins == null) {
        	_joins = new HashMap<String, JoinBuilder<GenericSearchBuilder<?, ?>>>();
        }
        _joins.put(name, t);
        
        builder._specifiedAttrs.clear();
        _specifiedAttrs.clear();
        return this;
    }
    
    protected void constructCondition(String conditionName, String cond, Attribute attr, Op op) {
        assert _entity != null : "SearchBuilder cannot be modified once it has been setup";
        assert op == null || _specifiedAttrs.size() == 1 : "You didn't select the attribute.";
        assert op != Op.SC : "Call join";
        
        Condition condition = new Condition(conditionName, cond, attr, op);
        _conditions.add(condition);
        _specifiedAttrs.clear();
    }

    /**
     * creates the SearchCriteria so the actual values can be filled in.
     * 
     * @return SearchCriteria
     */
    public SearchCriteria<K> create() {
        if (_entity != null) {
            done();
        }
        return new SearchCriteria<K>(this);
    }
    
    public SearchCriteria<K> create(String name, Object... values) {
        SearchCriteria<K> sc = create();
        sc.setParameters(name, values);
        return sc;
    }
    
    public GenericSearchBuilder<T, K> right() {
        Condition condition = new Condition("rp", " ) ", null, Op.RP);
        _conditions.add(condition);
        return this;
    }
    
    public GenericSearchBuilder<T, K> cp() {
        return right();
    }
    
    public GenericSearchBuilder<T, K> closeParen() {
        return right();
    }
    
    public SelectType getSelectType() {
        return _selectType;
    }
    
    /**
     * Marks the SearchBuilder as completed in building the search conditions.
     */
    public synchronized void done() {
        if (_entity != null) {
            Factory factory = (Factory)_entity;
            factory.setCallback(0, null);
            _entity = null;
        }
        
        if (_joins != null) {
        	for (JoinBuilder<GenericSearchBuilder<?, ?>> join : _joins.values()) {
        		join.getT().done();
            }
        }
        
        if (_selects == null || _selects.size() == 0) {
            _selectType = SelectType.Entity;
            assert _entityBeanType.equals(_resultType) : "Expecting " + _entityBeanType + " because you didn't specify any selects but instead got " + _resultType;
            return;
        }
        
        for (Select select : _selects) {
            if (select.field == null) {
                assert (_selects.size() == 1) : "You didn't specify any fields to put the result in but you're specifying more than one select so where should I put the selects?";
                _selectType = SelectType.Single;
                return;
            }
            if (select.func != null) {
                _selectType = SelectType.Result;
                return;
            }
        }
        
        _selectType = SelectType.Fields;
    }
    
    protected static class Condition {
        protected final String name;
        protected final String cond;
        protected final Op op;
        protected final Attribute attr;
        
        protected Condition(String name) {
            this(name, null, null, null);
        }
        
        public Condition(String name, String cond, Attribute attr, Op op) {
            this.name = name;
            this.attr = attr;
            this.cond = cond;
            this.op = op;
        }
        
        public void toSql(StringBuilder sql, Object[] params, int count) {
            if (count > 0) {
                sql.append(cond);
            }
            
            if (op == null) {
                return;
            }
            
            if (op == Op.SC) {
                sql.append(" (").append(((SearchCriteria<?>)params[0]).getWhereClause()).append(") ");
                return;
            }
            
            if (attr == null) {
                return;
            }
            
            sql.append(attr.table).append(".").append(attr.columnName).append(op.toString());
            if (op == Op.IN && params.length == 1) {
                sql.delete(sql.length() - op.toString().length(), sql.length());
                sql.append("=?");
            } else if (op == Op.NIN && params.length == 1) {
                sql.delete(sql.length() - op.toString().length(), sql.length());
                sql.append("!=?");
            } else if (op.getParams() == -1) {
                for (int i = 0; i < params.length; i++) {
                    sql.insert(sql.length() - 2, "?,");
                }
                sql.delete(sql.length() - 3, sql.length() - 2); // remove the last ,
            } else if (op  == Op.EQ && (params == null || params.length == 0 || params[0] == null)) {
                sql.delete(sql.length() - 4, sql.length());
                sql.append(" IS NULL ");
            } else if (op == Op.NEQ && (params == null || params.length == 0 || params[0] == null)) {
                sql.delete(sql.length() - 5, sql.length());
                sql.append(" IS NOT NULL ");
            } else {
                assert((op.getParams() == 0 && params == null) || (params.length == op.getParams())) : "Problem with condition: " + name;
            }
        }
        
        @Override
        public int hashCode() {
            return name.hashCode();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Condition)) {
                return false;
            }
            
            Condition condition = (Condition)obj;
            return name.equals(condition.name);
        }
    }
    
    protected static class Select {
        public Func func;
        public Attribute attr;
        public Object[] params;
        public Field field;
        
        protected Select() {
        }
        
        public Select(Func func, Attribute attr, Field field, Object[] params) {
            this.func = func;
            this.attr = attr;
            this.params = params;
            this.field = field;
        }
    }
}
