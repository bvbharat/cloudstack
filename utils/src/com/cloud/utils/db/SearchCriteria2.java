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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Transient;

import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.cloud.utils.db.GenericSearchBuilder.Condition;
import com.cloud.utils.db.GenericSearchBuilder.Select;
import com.cloud.utils.db.SearchCriteria.Func;
import com.cloud.utils.db.SearchCriteria.Op;
import com.cloud.utils.db.SearchCriteria.SelectType;

public class SearchCriteria2<T, K> implements SearchCriteriaService<T, K>, MethodInterceptor{
	GenericDao<? extends Serializable, ? extends Serializable> _dao;
	final protected Map<String, Attribute> _attrs;
	protected ArrayList<Attribute> _specifiedAttrs;
	protected T _entity;
	protected ArrayList<GenericSearchBuilder.Condition> _conditions;
	protected ArrayList<Select> _selects;
	private final HashMap<String, Object[]> _params = new HashMap<String, Object[]>();
	protected Class<K> _resultType;
    protected SelectType _selectType;
    protected Class<T> _entityBeanType;
	
	protected SearchCriteria2(T entity, Class<K> resultType, final Map<String, Attribute> attrs, GenericDao<? extends Serializable, ? extends Serializable> dao) {
		_entityBeanType = (Class<T>)entity.getClass();
		_dao = dao;
		_resultType = resultType;
		_attrs = attrs;
		_entity = entity;
		_conditions = new ArrayList<Condition>();
		_specifiedAttrs = new ArrayList<Attribute>();
    }
	
	static public <T, K> SearchCriteria2<T, K> create(Class<T> entityType, Class<K> resultType) {
		GenericDao<? extends Serializable, ? extends Serializable> dao = (GenericDao<? extends Serializable, ? extends Serializable>)GenericDaoBase.getDao(entityType);
		assert dao != null : "Can not find DAO for " + entityType.getName();
		SearchCriteria2<T, K> sc = dao.createSearchCriteria2(resultType);
		return sc;
	}
	
	static public <T, K> SearchCriteria2<T, K> create(Class<T> entityType) {
		GenericDao<? extends Serializable, ? extends Serializable> dao = (GenericDao<? extends Serializable, ? extends Serializable>)GenericDaoBase.getDao(entityType);
		assert dao != null : "Can not find DAO for " + entityType.getName();
		SearchCriteria2<T, K> sc = dao.createSearchCriteria2();
		return sc;
	}
	
	@Override
	public void selectField(Object... useless) {
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
    }
	
    private void constructCondition(String conditionName, String cond, Attribute attr, Op op) {
        assert _entity != null : "SearchBuilder cannot be modified once it has been setup";
        assert op == null || _specifiedAttrs.size() == 1 : "You didn't select the attribute.";
        assert op != Op.SC : "Call join";
        
        GenericSearchBuilder.Condition condition = new GenericSearchBuilder.Condition(conditionName, cond, attr, op);
        _conditions.add(condition);
        _specifiedAttrs.clear();
    }
    
    private void setParameters(String conditionName, Object... params) {
        assert _conditions.contains(new Condition(conditionName)) : "Couldn't find " + conditionName;
        _params.put(conditionName, params);
    }
    
	@Override
	public void addAnd(Object useless, Op op, Object...values) {
		String uuid = UUID.randomUUID().toString();
		constructCondition(uuid, " AND ", _specifiedAttrs.get(0), op);
		setParameters(uuid, values);
	}
	
	@Override
	public List<K> list() {
		done();
		SearchCriteria sc1 = createSearchCriteria();
		if (isSelectAll()) {
			return (List<K>)_dao.search(sc1, null);
		} else {
			return (List<K>)_dao.customSearch(sc1, null);
		}
	}
	
	private boolean isSelectAll() {
        return _selects == null || _selects.size() == 0;
    }
	
	@Override
	public T getEntity() {
		return (T) _entity; 
	}

	private SearchCriteria<K> createSearchCriteria() {
		return new SearchCriteria<K>(_attrs, _conditions, _selects, _selectType, _resultType, _params);
	}
	
    private void set(String name) {
        Attribute attr = _attrs.get(name);
        assert (attr != null) : "Searching for a field that's not there: " + name;
        _specifiedAttrs.add(attr);
    }
    
    private void done() {
        if (_entity != null) {
            Factory factory = (Factory)_entity;
            factory.setCallback(0, null);
            _entity = null;
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

	@Override
    public <K> K find() {
		assert isSelectAll() : "find doesn't support select search";
		done();
		SearchCriteria sc1 = createSearchCriteria();
		return (K)_dao.findOneBy(sc1);
    }

}
