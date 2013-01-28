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
package com.cloud.async;


import java.util.List;

import org.apache.log4j.Logger;

import junit.framework.Assert;

import com.cloud.async.AsyncJobVO;
import com.cloud.cluster.StackMaid;
import com.cloud.cluster.CheckPointVO;
import com.cloud.cluster.dao.StackMaidDao;
import com.cloud.cluster.dao.StackMaidDaoImpl;
import com.cloud.serializer.Param;
import com.cloud.utils.ActionDelegate;
import com.cloud.utils.Pair;
import com.cloud.utils.db.Transaction;
import com.cloud.utils.testcase.Log4jEnabledTestCase;


public class TestAsync extends Log4jEnabledTestCase {
    private static final Logger s_logger = Logger.getLogger(TestAsync.class);

    /*
	public static class SampleAsyncResult {
		@Param(name="name", propName="name")
		private final String _name;
		
		@Param
		private final int count;
		
		public SampleAsyncResult(String name, int count) {
			_name = name;
			this.count = count;
		}
		
		public String getName() { return _name; }
		public int getCount() { return count; }
	}

	public void testDao() {
		AsyncJobDao dao = new AsyncJobDaoImpl();
		AsyncJobVO job = new AsyncJobVO(1, 1, "TestCmd", null);
		job.setInstanceType("user_vm");
		job.setInstanceId(1000L);
		
		char[] buf = new char[1024];
		for(int i = 0; i < 1024; i++)
			buf[i] = 'a';
			
		job.setResult(new String(buf));
		dao.persist(job);
		
		AsyncJobVO jobVerify = dao.findById(job.getId());
		
		Assert.assertTrue(jobVerify.getCmd().equals(job.getCmd()));
		Assert.assertTrue(jobVerify.getUserId() == 1);
		Assert.assertTrue(jobVerify.getAccountId() == 1);
		
		String result = jobVerify.getResult();
		for(int i = 0; i < 1024; i++)
			Assert.assertTrue(result.charAt(i) == 'a');
		
		jobVerify = dao.findInstancePendingAsyncJob("user_vm", 1000L);
		Assert.assertTrue(jobVerify != null);
		Assert.assertTrue(jobVerify.getCmd().equals(job.getCmd()));
		Assert.assertTrue(jobVerify.getUserId() == 1);
		Assert.assertTrue(jobVerify.getAccountId() == 1);
	}
	
	public void testSerialization() {
		List<Pair<String, Object>> l;
		int value = 1;
		l = SerializerHelper.toPairList(value, "result");
		Assert.assertTrue(l.size() == 1);
		Assert.assertTrue(l.get(0).first().equals("result"));
		Assert.assertTrue(l.get(0).second().equals("1"));
		l.clear();
		
		SampleAsyncResult result = new SampleAsyncResult("vmops", 1);
		l = SerializerHelper.toPairList(result, "result");
		
		Assert.assertTrue(l.size() == 2);
		Assert.assertTrue(l.get(0).first().equals("name"));
		Assert.assertTrue(l.get(0).second().equals("vmops"));
		Assert.assertTrue(l.get(1).first().equals("count"));
		Assert.assertTrue(l.get(1).second().equals("1"));
	}
	
	public void testAsyncResult() {
		AsyncJobResult result = new AsyncJobResult(1);
		
		result.setResultObject(100);
		Assert.assertTrue(result.getResult().equals("java.lang.Integer/100"));
		
		Object obj = result.getResultObject();
		Assert.assertTrue(obj instanceof Integer);
		Assert.assertTrue(((Integer)obj).intValue() == 100);
	}

	public void testTransaction() {
		Transaction txn = Transaction.open("testTransaction");
		try {
			txn.start();
			
			AsyncJobDao dao = new AsyncJobDaoImpl();
			AsyncJobVO job = new AsyncJobVO(1, 1, "TestCmd", null);
			job.setInstanceType("user_vm");
			job.setInstanceId(1000L);
			job.setResult("");
			dao.persist(job);
			txn.rollback();
		} finally {
			txn.close();
		}
	}
	
	public void testMorevingian() {
		int threadCount = 10;
		final int testCount = 10;
		
		Thread[] threads = new Thread[threadCount];
		for(int i = 0; i < threadCount; i++) {
			final int threadNum = i + 1;
			threads[i] = new Thread(new Runnable() {
				public void run() {
					for(int i = 0; i < testCount; i++) {
						Transaction txn = Transaction.open(Transaction.CLOUD_DB);
						try {
							AsyncJobDao dao = new AsyncJobDaoImpl();
							
							s_logger.info("Thread " + threadNum + " acquiring lock");
							AsyncJobVO job = dao.acquire(1L, 30);
							if(job != null) {
								s_logger.info("Thread " + threadNum + " acquired lock");
								
								try {
									Thread.sleep(Log4jEnabledTestCase.getRandomMilliseconds(1000, 3000));
								} catch (InterruptedException e) {
								}
								
								s_logger.info("Thread " + threadNum + " acquiring lock nestly");
								AsyncJobVO job2 = dao.acquire(1L, 30);
								if(job2 != null) {
									s_logger.info("Thread " + threadNum + " acquired lock nestly");
									
									try {
										Thread.sleep(Log4jEnabledTestCase.getRandomMilliseconds(1000, 3000));
									} catch (InterruptedException e) {
									}
									
									s_logger.info("Thread " + threadNum + " releasing lock (nestly acquired)");
									dao.release(1L);
									s_logger.info("Thread " + threadNum + " released lock (nestly acquired)");
									
								} else {
									s_logger.info("Thread " + threadNum + " was unable to acquire lock nestly");
								}
								
								s_logger.info("Thread " + threadNum + " releasing lock");
								dao.release(1L);
								s_logger.info("Thread " + threadNum + " released lock");
							} else {
								s_logger.info("Thread " + threadNum + " was unable to acquire lock");
							}
						} finally {
							txn.close();
						}
						
						try {
							Thread.sleep(Log4jEnabledTestCase.getRandomMilliseconds(1000, 10000));
						} catch (InterruptedException e) {
						}
					}
				}
			});
		}
		
		for(int i = 0; i < threadCount; i++) {
			threads[i].start();
		}
		
		for(int i = 0; i < threadCount; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
			}
		}
	}
	*/
	
	public void testMaid() {
		Transaction txn = Transaction.open(Transaction.CLOUD_DB);
		
		StackMaidDao dao = new StackMaidDaoImpl();
		dao.pushCleanupDelegate(1L, 0, "delegate1", "Hello, world");
		dao.pushCleanupDelegate(1L, 1, "delegate2", new Long(100));
		dao.pushCleanupDelegate(1L, 2, "delegate3", null);
		
		CheckPointVO item = dao.popCleanupDelegate(1L);
		Assert.assertTrue(item.getDelegate().equals("delegate3"));
		Assert.assertTrue(item.getContext() == null);
		
		item = dao.popCleanupDelegate(1L);
		Assert.assertTrue(item.getDelegate().equals("delegate2"));
		s_logger.info(item.getContext());

		item = dao.popCleanupDelegate(1L);
		Assert.assertTrue(item.getDelegate().equals("delegate1"));
		s_logger.info(item.getContext());
		
		txn.close();
	}
	
	public void testMaidClear() {
		Transaction txn = Transaction.open(Transaction.CLOUD_DB);
		
		StackMaidDao dao = new StackMaidDaoImpl();
		dao.pushCleanupDelegate(1L, 0, "delegate1", "Hello, world");
		dao.pushCleanupDelegate(1L, 1, "delegate2", new Long(100));
		dao.pushCleanupDelegate(1L, 2, "delegate3", null);
		
		dao.clearStack(1L);
		Assert.assertTrue(dao.popCleanupDelegate(1L) == null);
		txn.close();
	}
	
	public void testMaidExitCleanup() {
		StackMaid.current().push(1L, "com.cloud.async.CleanupDelegate", "Hello, world1");
		StackMaid.current().push(1L, "com.cloud.async.CleanupDelegate", "Hello, world2");
		
		StackMaid.current().exitCleanup(1L);
	}
	
	public void testMaidLeftovers() {

		Thread[] threads = new Thread[3];
		for(int i = 0; i < 3; i++) {
			final int threadNum = i+1;
			threads[i] = new Thread(new Runnable() {
				public void run() {
					Transaction txn = Transaction.open(Transaction.CLOUD_DB);
					
					StackMaidDao dao = new StackMaidDaoImpl();
					dao.pushCleanupDelegate(1L, 0, "delegate-" + threadNum, "Hello, world");
					dao.pushCleanupDelegate(1L, 1, "delegate-" + threadNum, new Long(100));
					dao.pushCleanupDelegate(1L, 2, "delegate-" + threadNum, null);
					
					txn.close();
				}
			});
			
			threads[i].start();
		}
		
		for(int i = 0; i < 3; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
			}
		}

		
		Transaction txn = Transaction.open(Transaction.CLOUD_DB);
		
		StackMaidDao dao = new StackMaidDaoImpl();
		List<CheckPointVO> l = dao.listLeftoversByMsid(1L);
		for(CheckPointVO maid : l) {
			s_logger.info("" + maid.getThreadId() + " " + maid.getDelegate() + " " + maid.getContext());
		}
		
		txn.close();
	}
}
