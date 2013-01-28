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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.cloud.utils.component.AnnotationInterceptor;

public class DatabaseCallback implements MethodInterceptor, AnnotationInterceptor<Transaction> {

    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Transaction txn = interceptStart(method);
        try {
            return methodProxy.invokeSuper(object, args);
        } finally {
            interceptComplete(method, txn);
        }
    }

    @Override
    public boolean needToIntercept(AnnotatedElement element) {
        if (!(element instanceof Method)) {
            return false;
            
        }
        Method method = (Method)element;
        DB db = method.getAnnotation(DB.class);
        if (db != null) {
            return db.txn();
        }
        
        Class<?> clazz = method.getDeclaringClass();
        do {
            db = clazz.getAnnotation(DB.class);
            if (db != null) {
                return db.txn();
            }
            clazz = clazz.getSuperclass();
        } while (clazz != Object.class && clazz != null);
        
        return false;
    }

    @Override
    public Transaction interceptStart(AnnotatedElement element) {
        return Transaction.open(((Method)element).getName());
    }

    @Override
    public void interceptComplete(AnnotatedElement element, Transaction txn) {
        txn.close();
    }

    @Override
    public void interceptException(AnnotatedElement element, Transaction txn) {
        txn.close();
    }

    @Override
    public Callback getCallback() {
        return this;
    }
    
}
