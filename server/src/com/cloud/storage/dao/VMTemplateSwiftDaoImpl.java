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

import java.util.Collections;
import java.util.List;

import javax.ejb.Local;

import org.apache.log4j.Logger;

import com.cloud.storage.VMTemplateSwiftVO;
import com.cloud.utils.db.GenericDaoBase;
import com.cloud.utils.db.SearchBuilder;
import com.cloud.utils.db.SearchCriteria;

/**
 * 
 * 
 */

@Local(value = { VMTemplateSwiftDao.class })
public class VMTemplateSwiftDaoImpl extends GenericDaoBase<VMTemplateSwiftVO, Long> implements VMTemplateSwiftDao {
    public static final Logger s_logger = Logger.getLogger(VMTemplateSwiftDaoImpl.class.getName());

    protected final SearchBuilder<VMTemplateSwiftVO> AllFieldSearch;

    public VMTemplateSwiftDaoImpl() {
        AllFieldSearch = createSearchBuilder();
        AllFieldSearch.and("swift_id", AllFieldSearch.entity().getSwiftId(), SearchCriteria.Op.EQ);
        AllFieldSearch.and("template_id", AllFieldSearch.entity().getTemplateId(), SearchCriteria.Op.EQ);
        AllFieldSearch.done();

    }

    @Override
    public List<VMTemplateSwiftVO> listBySwiftId(long id) {
        SearchCriteria<VMTemplateSwiftVO> sc = AllFieldSearch.create();
        sc.setParameters("swift_id", id);
        return listBy(sc);
    }

    @Override
    public List<VMTemplateSwiftVO> listByTemplateId(long templateId) {
        SearchCriteria<VMTemplateSwiftVO> sc = AllFieldSearch.create();
        sc.setParameters("template_id", templateId);
        return listBy(sc);
    }

    @Override
    public VMTemplateSwiftVO findOneByTemplateId(long templateId) {
        SearchCriteria<VMTemplateSwiftVO> sc = AllFieldSearch.create();
        sc.setParameters("template_id", templateId);
        List<VMTemplateSwiftVO> list = listBy(sc);
        if (list == null || list.size() < 1) {
            return null;
        } else {
            Collections.shuffle(list);
            return list.get(0);
        }
    }

    @Override
    public VMTemplateSwiftVO findBySwiftTemplate(long swiftId, long templateId) {
        SearchCriteria<VMTemplateSwiftVO> sc = AllFieldSearch.create();
        sc.setParameters("swift_id", swiftId);
        sc.setParameters("template_id", templateId);
        return findOneBy(sc);
    }

}
