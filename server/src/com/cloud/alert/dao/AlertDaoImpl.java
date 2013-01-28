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
package com.cloud.alert.dao;

import java.util.List;

import javax.ejb.Local;

import com.cloud.alert.AlertVO;
import com.cloud.utils.db.Filter;
import com.cloud.utils.db.GenericDaoBase;
import com.cloud.utils.db.SearchCriteria;

@Local(value = { AlertDao.class })
public class AlertDaoImpl extends GenericDaoBase<AlertVO, Long> implements AlertDao {
    @Override
    public AlertVO getLastAlert(short type, long dataCenterId, Long podId, Long clusterId) {
        Filter searchFilter = new Filter(AlertVO.class, "createdDate", Boolean.FALSE, Long.valueOf(0), Long.valueOf(1));
        SearchCriteria<AlertVO> sc = createSearchCriteria();

        sc.addAnd("type", SearchCriteria.Op.EQ, Short.valueOf(type));
        sc.addAnd("dataCenterId", SearchCriteria.Op.EQ, Long.valueOf(dataCenterId));
        if (podId != null) {
            sc.addAnd("podId", SearchCriteria.Op.EQ, podId);
        }
        if (clusterId != null) {
            sc.addAnd("clusterId", SearchCriteria.Op.EQ, clusterId);
        }

        List<AlertVO> alerts = listBy(sc, searchFilter);
        if ((alerts != null) && !alerts.isEmpty()) {
            return alerts.get(0);
        }
        return null;
    }
    
    @Override
    public AlertVO getLastAlert(short type, long dataCenterId, Long podId) {
        Filter searchFilter = new Filter(AlertVO.class, "createdDate", Boolean.FALSE, Long.valueOf(0), Long.valueOf(1));
        SearchCriteria<AlertVO> sc = createSearchCriteria();

        sc.addAnd("type", SearchCriteria.Op.EQ, Short.valueOf(type));
        sc.addAnd("dataCenterId", SearchCriteria.Op.EQ, Long.valueOf(dataCenterId));
        if (podId != null) {
            sc.addAnd("podId", SearchCriteria.Op.EQ, podId);
        }        

        List<AlertVO> alerts = listBy(sc, searchFilter);
        if ((alerts != null) && !alerts.isEmpty()) {
            return alerts.get(0);
        }
        return null;
    }
}
