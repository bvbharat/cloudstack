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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Running with assertions on, will find all classes that are
 * 
 * 1. Annotate method that starts and commits DB transactions.
 *    Transaction txn = Transaction.currentTxn();
 *    txn.start();
 *    ...
 *    txn.commit();
 * 
 * 2. Annotate methods that uses a DAO's acquire method.
 *    _dao.acquireInLockTable(id);
 *    ...
 *    _dao.releaseFromLockTable(id);
 * 
 * 3. Annotate methods that are inside a DAO but doesn't use
 *    the Transaction class.  Generally, these are methods
 *    that are utility methods for setting up searches.  In
 *    this case use @DB(txn=false) to annotate the method.
 *    While this is not required, it helps when you're debugging
 *    the code and it saves on method calls during runtime.
 *
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface DB {
    /**
     * (Optional) Specifies that the method
     * does not use transaction.  This is useful for
     * utility methods within DAO classes which are
     * automatically marked with @DB.  By marking txn=false,
     * the method is not surrounded with transaction code.
     */
    boolean txn() default true;
}
