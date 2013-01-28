// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package com.cloud.cluster;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.cloud.cluster.dao.StackMaidDao;
import com.cloud.cluster.dao.StackMaidDaoImpl;
import com.cloud.serializer.SerializerHelper;
import com.cloud.utils.CleanupDelegate;
import com.cloud.utils.db.Transaction;

public class StackMaid {
    protected final static Logger s_logger = Logger.getLogger(StackMaid.class);
	
	private static ThreadLocal<StackMaid> threadMaid = new ThreadLocal<StackMaid>();
	
	private static long msid_setby_manager = 0;

	private StackMaidDao maidDao = new StackMaidDaoImpl(); 
	private int currentSeq = 0;
	private Map<String, Object> context = new HashMap<String, Object>();

	public static void init(long msid) {
		msid_setby_manager = msid;
	}
	
	public static StackMaid current() {
		StackMaid maid = threadMaid.get();
		if(maid == null) {
			maid = new StackMaid();
			threadMaid.set(maid);
		}
		return maid;
	}
	
	public void registerContext(String key, Object contextObject) {
		assert(!context.containsKey(key)) : "Context key has already been registered";
		context.put(key, contextObject);
	}
	
	public Object getContext(String key) {
		return context.get(key);
	}
	
	public void expungeMaidItem(long maidId) {
		// this is a bit ugly, but when it is not loaded by component locator, this is just a workable way for now
		Transaction txn = Transaction.open(Transaction.CLOUD_DB);
		try {
			maidDao.expunge(maidId);
		} finally {
			txn.close();
		}
	}

	public int push(String delegateClzName, Object context) {
		assert(msid_setby_manager != 0) : "Fatal, make sure StackMaidManager is loaded";
		if(msid_setby_manager == 0)
			s_logger.error("Fatal, make sure StackMaidManager is loaded");
		
		return push(msid_setby_manager, delegateClzName, context);
	}
	
	public int push(long currentMsid, String delegateClzName, Object context) {
		int savePoint = currentSeq;
		maidDao.pushCleanupDelegate(currentMsid, currentSeq++, delegateClzName, context);
		return savePoint;
	}

	public void pop(int savePoint) {
		assert(msid_setby_manager != 0) : "Fatal, make sure StackMaidManager is loaded";
		if(msid_setby_manager == 0)
			s_logger.error("Fatal, make sure StackMaidManager is loaded");
		
		pop(msid_setby_manager, savePoint);
	}
	
	public void pop() {
	    if(currentSeq > 0)
	        pop(currentSeq -1);
	}
	
	/**
	 * must be called within thread context
	 * @param currentMsid
	 */
	public void pop(long currentMsid, int savePoint) {
		while(currentSeq > savePoint) {
			maidDao.popCleanupDelegate(currentMsid);
			currentSeq--;
		}
	}
	
	public void exitCleanup() {
		exitCleanup(msid_setby_manager);
	}
	
	public void exitCleanup(long currentMsid) {
		if(currentSeq > 0) {
			CheckPointVO maid = null;
			while((maid = maidDao.popCleanupDelegate(currentMsid)) != null) {
				doCleanup(maid);
			}
			currentSeq = 0;
		}
		
		context.clear();
	}
	
	public static boolean doCleanup(CheckPointVO maid) {
		if(maid.getDelegate() != null) {
			try {
				Class<?> clz = Class.forName(maid.getDelegate());
				Object delegate = clz.newInstance();
				if(delegate instanceof CleanupDelegate) {
					return ((CleanupDelegate)delegate).cleanup(SerializerHelper.fromSerializedString(maid.getContext()), maid);
				} else {
					assert(false);
				}
			} catch (final ClassNotFoundException e) {
				s_logger.error("Unable to load StackMaid delegate class: " + maid.getDelegate(), e);
			} catch (final SecurityException e) {
				s_logger.error("Security excetion when loading resource: " + maid.getDelegate());
            } catch (final IllegalArgumentException e) {
            	s_logger.error("Illegal argument excetion when loading resource: " + maid.getDelegate());
            } catch (final InstantiationException e) {
            	s_logger.error("Instantiation excetion when loading resource: " + maid.getDelegate());
            } catch (final IllegalAccessException e) {
            	s_logger.error("Illegal access exception when loading resource: " + maid.getDelegate());
            } 
            
            return false;
		}
		return true;
	}
}
