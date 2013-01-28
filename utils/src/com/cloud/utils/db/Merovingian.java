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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.cloud.utils.Pair;
import com.cloud.utils.Ternary;
import com.cloud.utils.exception.CloudRuntimeException;
import com.cloud.utils.net.MacAddress;
import com.cloud.utils.time.InaccurateClock;

public class Merovingian {
	private static final Logger s_logger = Logger.getLogger(Merovingian.class);
	
	private static final String ACQUIRE_SQL = "INSERT IGNORE INTO op_lock (op_lock.key, op_lock.mac, op_lock.ip, op_lock.thread) VALUES (?, ?, ?, ?)";
	private static final String INQUIRE_SQL = "SELECT op_lock.ip FROM op_lock WHERE op_lock.key = ?";
	private static final String RELEASE_SQL = "DELETE FROM op_lock WHERE op_lock.key = ?";
	private static final String CLEAR_SQL = "DELETE FROM op_lock WHERE op_lock.mac = ? AND op_lock.ip = ?";
	
	private final static HashMap<String, Pair<Lock, Integer>> s_memLocks = new HashMap<String, Pair<Lock, Integer>>(1027);
	
	private final LinkedHashMap<String, Ternary<Savepoint, Integer, Long>> _locks = new LinkedHashMap<String, Ternary<Savepoint, Integer, Long>>();
	private int _previousIsolation = Connection.TRANSACTION_NONE;
	
	private final static String s_macAddress;
	private final static String s_ipAddress;
	static {
		s_macAddress = MacAddress.getMacAddress().toString(":");
		String address = null;
		try {
			InetAddress addr = InetAddress.getLocalHost();
			address = addr.getHostAddress().toString();
		} catch (UnknownHostException e) {
			address = "127.0.0.1";
		}
		
		s_ipAddress = address;
	}
	
	Connection _conn = null;
	
	public Merovingian(short dbId) {
	    _conn = null;
	}
	
	protected void checkIsolationLevel(Connection conn) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT @@global.tx_isolation, @@session.tx_isolation;");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            s_logger.info("global isolation = " + rs.getString(1));
            s_logger.info("session isolation = " + rs.getString(2));
        }
	}
	
	protected Connection getConnection(String key, boolean test) {
		try {
		    if (_conn != null) {
		        return _conn;
		    }
		    
		    _conn = Transaction.getStandaloneConnection();
			if (_previousIsolation == Connection.TRANSACTION_NONE) {
				_previousIsolation = _conn.getTransactionIsolation();
				_conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
				if (!test && !_conn.getAutoCommit()) {
				    _conn.setAutoCommit(false);
				}
			}
			return _conn;
		} catch (SQLException e) {
		    try {
		        _conn.rollback();
		    } catch (SQLException e1) {
		    }
			throw new CloudRuntimeException("Unable to acquire db connection for locking " + key, e);
		}
	}
	
	public boolean acquire(String key, int timeInSeconds) {
		Pair<Lock, Integer> memLock = null;
	    boolean acquiredDbLock = false;
	    boolean acquiredMemLock = false;
	    try {
    	    synchronized(s_memLocks) {
    	        memLock = s_memLocks.get(key);
    	        if (memLock == null) {
    	            Lock l = new ReentrantLock(true);
    	            memLock = new Pair<Lock, Integer>(l, 0);
    	            s_memLocks.put(key, memLock);
    	        }
    	        
    	        memLock.second(memLock.second() + 1);
    	    }
    	    
    	    if (!memLock.first().tryLock(timeInSeconds, TimeUnit.SECONDS)) {
    	        return false;
    	    }
    	    acquiredMemLock = true;
    	    
    		Ternary<Savepoint, Integer, Long> lock = _locks.get(key);
    		if (lock != null) {
    			lock.second(lock.second() + 1);
    			if (s_logger.isTraceEnabled()) {
    				s_logger.trace("Lock: Reacquiring " + key + " Count: " + lock.second());
    			}
    			acquiredDbLock = true;
    			return true;
    		}
    		
    		long startTime = InaccurateClock.getTime();
    		while ((InaccurateClock.getTime() - startTime) < (timeInSeconds * 1000)) {
    			if (isLocked(key)) {
    				try {
    					Thread.sleep(1000);
    				} catch (InterruptedException e) {
    				}
    			} else {
    				acquiredDbLock = doAcquire(key);
    				if (acquiredDbLock) {
    					return true;
    				}
    			}
    		}
    		if (s_logger.isTraceEnabled()) {
    			s_logger.trace("Lock: Timed out on acquiring lock " + key);
    		}
    		return false;
	    } catch (InterruptedException e) {
	        s_logger.debug("Interrupted while trying to acquire " + key);
	        return false;
	    } finally {
	        if (!acquiredMemLock || !acquiredDbLock) {
	            synchronized(s_memLocks) {
	                if (memLock.second(memLock.second() - 1) <= 0) {
	                    s_memLocks.remove(key);
	                }
	            }
            }
	        
            if (acquiredMemLock && !acquiredDbLock) {
                memLock.first().unlock();
            }
	    }
	}
	
	protected boolean doAcquire(String key) {
	    Connection conn = getConnection(key, true);
		PreparedStatement pstmt = null;
        Savepoint sp = null;
        try {
            sp = conn.setSavepoint(key);
        } catch (SQLException e) {
            s_logger.warn("Unable to set save point " + key);
            return false;
        }

		try {
            long startTime = InaccurateClock.getTime();
            try {
    			pstmt = conn.prepareStatement(ACQUIRE_SQL);
    			pstmt.setString(1, key);
    			pstmt.setString(2, s_macAddress);
    			pstmt.setString(3, s_ipAddress);
    			pstmt.setString(4, Thread.currentThread().getName());
    			String exceptionMessage = null;
				int rows = pstmt.executeUpdate();
				if (rows == 1) {
					if (s_logger.isTraceEnabled()) {
						s_logger.trace("Lock: lock acquired for " + key);
					}
					Ternary<Savepoint, Integer, Long> lock = new Ternary<Savepoint, Integer, Long>(sp, 1, InaccurateClock.getTime());
					_locks.put(key, lock);
					return true;
				}
			} catch(SQLException e) {
				s_logger.warn("Lock: Retrying lock " + key + ".  Waited " + (InaccurateClock.getTime() - startTime), e);
			}
			
			conn.rollback(sp);
			s_logger.trace("Lock: Unable to acquire DB lock " + key);
		} catch (SQLException e) {
			s_logger.warn("Lock: Unable to acquire db connection for locking " + key, e);
		} finally {
		    if (pstmt != null) {
		        try {
		            pstmt.close();
		        } catch (SQLException e) {
		        }
		    }
		}
		return false;
	}
	
	public boolean isLocked(String key) {
		Connection conn = getConnection(key, false);
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(INQUIRE_SQL);
			pstmt.setString(1, key);
			rs = pstmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			s_logger.warn("SQL exception " + e.getMessage(), e);
			throw new CloudRuntimeException("SQL Exception on inquiry", e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException e) {
				s_logger.warn("Unexpected SQL exception " + e.getMessage(), e);
			}
		}
	}
	
	public void clear() {
		if (_locks.size() == 0) {
			return;
		}
		
		Set<String> keys = new HashSet<String>(_locks.keySet());

		//
		// disable assertion, when assert support is enabled, it throws an exception
		// which eventually eats the following on important messages for diagnostic
		//
		
		// assert (false) : "Who acquired locks but didn't release them? " + keys.toArray(new String[keys.size()]);
		
		for (String key : keys) {
			s_logger.warn("Lock: This is not good guys!  Automatically releasing lock: " + key);
			release(key);
		}
		
		_locks.clear();
	}
	
	public boolean release(String key) {
		boolean validLock = false;
	    try {
    		assert _locks.size() > 0 : "There are no locks here. Why are you trying to release " + key;
    		
    		Ternary<Savepoint, Integer, Long> lock = _locks.get(key);
    		if (lock != null) {
    			validLock = true;

    			if (lock.second() > 1) {
        			lock.second(lock.second() - 1);
        			if (s_logger.isTraceEnabled()) {
        				s_logger.trace("Lock: Releasing " + key + " but not in DB " + lock.second());
        			}
        			return false;
        		}
        		
        		
                if (s_logger.isDebugEnabled() && !_locks.keySet().iterator().next().equals(key)) {
                    s_logger.trace("Lock: Releasing out of order for " + key);
                }
                _locks.remove(key);
        		if (s_logger.isTraceEnabled()) {
        			s_logger.trace("Lock: Releasing " + key + " after " + (InaccurateClock.getTime() - lock.third()));
        		}
        		Connection conn = getConnection(key, true);
        		
        		conn.rollback(lock.first());
    		} else {
    			s_logger.warn("Merovingian.release() is called against key " + key + " but the lock of this key does not exist!");
    		}
    		
    		if (_locks.size() == 0) {
    			closeConnection();
    		}
    		
	    } catch (SQLException e) {
	        s_logger.warn("unable to rollback for " + key);
	    } finally {
    		synchronized(s_memLocks) {
    		    Pair<Lock, Integer> memLock = s_memLocks.get(key);
    		    if(memLock != null) {
	    		    memLock.second(memLock.second() - 1);
	    		    if (memLock.second() <= 0) {
	    		        s_memLocks.remove(key);
	    		    }
	    		    
	    		    if(validLock)
	    		    	memLock.first().unlock();
    		    } else {
    		    	throw new CloudRuntimeException("Merovingian.release() is called for key " + key + ", but its memory lock no longer exist! This is not good, guys");
    		    }
    		}
	    }
		return true;
	}
	
	public void closeConnection() {
		try {
	        if (_conn == null) {
	            _previousIsolation = Connection.TRANSACTION_NONE;
	            return;
	        }
            if (_previousIsolation != Connection.TRANSACTION_NONE) {
                _conn.setTransactionIsolation(_previousIsolation);
            }
            try { // rollback just in case but really there shoul be nothing.
                _conn.rollback();
            } catch (SQLException e) {
            }
            _conn.setAutoCommit(true);
            _previousIsolation = Connection.TRANSACTION_NONE;
            _conn.close();
            _conn = null;
		} catch (SQLException e) {
			s_logger.warn("Unexpected SQL exception " + e.getMessage(), e);
		}
	}
}
