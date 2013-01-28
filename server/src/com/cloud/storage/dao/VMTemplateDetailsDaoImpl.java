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
package com.cloud.storage.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import com.cloud.storage.VMTemplateDetailVO;
import com.cloud.utils.db.GenericDaoBase;
import com.cloud.utils.db.SearchBuilder;
import com.cloud.utils.db.SearchCriteria;
import com.cloud.utils.db.Transaction;

@Local(value=VMTemplateDetailsDao.class)
public class VMTemplateDetailsDaoImpl extends GenericDaoBase<VMTemplateDetailVO, Long> implements VMTemplateDetailsDao {

    protected final SearchBuilder<VMTemplateDetailVO> TemplateSearch;
    protected final SearchBuilder<VMTemplateDetailVO> DetailSearch;
    
	protected VMTemplateDetailsDaoImpl() {
		TemplateSearch = createSearchBuilder();
		TemplateSearch.and("templateId", TemplateSearch.entity().getTemplateId(), SearchCriteria.Op.EQ);
		TemplateSearch.done();
		
		DetailSearch = createSearchBuilder();
        DetailSearch.and("templateId", DetailSearch.entity().getTemplateId(), SearchCriteria.Op.EQ);
        DetailSearch.and("name", DetailSearch.entity().getName(), SearchCriteria.Op.EQ);
        DetailSearch.done();
	}
	
	@Override
	public void deleteDetails(long templateId) {
        SearchCriteria<VMTemplateDetailVO> sc = TemplateSearch.create();
        sc.setParameters("templateId", templateId);
        
        List<VMTemplateDetailVO> results = search(sc, null);
        for (VMTemplateDetailVO result : results) {
        	remove(result.getId());
        }		
	}

	@Override
	public VMTemplateDetailVO findDetail(long templateId, String name) {
        SearchCriteria<VMTemplateDetailVO> sc = DetailSearch.create();
        sc.setParameters("templateId", templateId);
        sc.setParameters("name", name);
		
        return findOneBy(sc);
	}

	@Override
	public Map<String, String> findDetails(long templateId) {
        SearchCriteria<VMTemplateDetailVO> sc = TemplateSearch.create();
        sc.setParameters("templateId", templateId);
        
        List<VMTemplateDetailVO> results = search(sc, null);
        Map<String, String> details = new HashMap<String, String>(results.size());
        for (VMTemplateDetailVO result : results) {
            details.put(result.getName(), result.getValue());
        }
        
        return details;
	}

	@Override
	public void persist(long templateId, Map<String, String> details) {
        Transaction txn = Transaction.currentTxn();
        txn.start();
        SearchCriteria<VMTemplateDetailVO> sc = TemplateSearch.create();
        sc.setParameters("templateId", templateId);
        expunge(sc);
        
        for (Map.Entry<String, String> detail : details.entrySet()) {
            VMTemplateDetailVO vo = new VMTemplateDetailVO(templateId, detail.getKey(), detail.getValue());
            persist(vo);
        }
        txn.commit();		
	}
}
