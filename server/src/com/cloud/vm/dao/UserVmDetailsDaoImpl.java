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
package com.cloud.vm.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import com.cloud.utils.db.GenericDaoBase;
import com.cloud.utils.db.SearchBuilder;
import com.cloud.utils.db.SearchCriteria;
import com.cloud.utils.db.Transaction;
import com.cloud.vm.UserVmDetailVO;

@Local(value=UserVmDetailsDao.class)
public class UserVmDetailsDaoImpl extends GenericDaoBase<UserVmDetailVO, Long> implements UserVmDetailsDao {
    protected final SearchBuilder<UserVmDetailVO> VmSearch;
    protected final SearchBuilder<UserVmDetailVO> DetailSearch;

	protected UserVmDetailsDaoImpl() {
		VmSearch = createSearchBuilder();
		VmSearch.and("vmId", VmSearch.entity().getVmId(), SearchCriteria.Op.EQ);
        VmSearch.done();
		
		DetailSearch = createSearchBuilder();
        DetailSearch.and("vmId", DetailSearch.entity().getVmId(), SearchCriteria.Op.EQ);
        DetailSearch.and("name", DetailSearch.entity().getName(), SearchCriteria.Op.EQ);
        DetailSearch.done();
	}
    
	@Override
	public void deleteDetails(long vmId) {
        SearchCriteria<UserVmDetailVO> sc = VmSearch.create();
        sc.setParameters("vmId", vmId);
        
        List<UserVmDetailVO> results = search(sc, null);
        for (UserVmDetailVO result : results) {
        	remove(result.getId());
        }		
	}

	@Override
	public UserVmDetailVO findDetail(long vmId, String name) {
        SearchCriteria<UserVmDetailVO> sc = DetailSearch.create();
        sc.setParameters("vmId", vmId);
        sc.setParameters("name", name);
		
        return findOneBy(sc);
	}

	@Override
	public Map<String, String> findDetails(long vmId) {
        SearchCriteria<UserVmDetailVO> sc = VmSearch.create();
        sc.setParameters("vmId", vmId);
        
        List<UserVmDetailVO> results = search(sc, null);
        Map<String, String> details = new HashMap<String, String>(results.size());
        for (UserVmDetailVO result : results) {
            details.put(result.getName(), result.getValue());
        }
        
        return details;
	}

	@Override
	public void persist(long vmId, Map<String, String> details) {
        Transaction txn = Transaction.currentTxn();
        txn.start();
        SearchCriteria<UserVmDetailVO> sc = VmSearch.create();
        sc.setParameters("vmId", vmId);
        expunge(sc);
        
        for (Map.Entry<String, String> detail : details.entrySet()) {
            UserVmDetailVO vo = new UserVmDetailVO(vmId, detail.getKey(), detail.getValue());
            persist(vo);
        }
        txn.commit();		
	}

}
