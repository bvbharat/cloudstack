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
package com.cloud.usage.dao;

import java.util.Date;
import java.util.List;

import com.cloud.event.UsageEventVO;
import com.cloud.exception.UsageServerException;
import com.cloud.usage.UsageVO;
import com.cloud.user.AccountVO;
import com.cloud.user.UserStatisticsVO;
import com.cloud.utils.db.Filter;
import com.cloud.utils.db.GenericDao;
import com.cloud.utils.db.SearchCriteria;

public interface UsageDao extends GenericDao<UsageVO, Long> {
    void deleteRecordsForAccount(Long accountId);
    List<UsageVO> searchAllRecords(SearchCriteria<UsageVO> sc, Filter filter);
    void saveAccounts(List<AccountVO> accounts) throws UsageServerException;
    void updateAccounts(List<AccountVO> accounts) throws UsageServerException;
    void saveUserStats(List<UserStatisticsVO> userStats) throws UsageServerException;
    void updateUserStats(List<UserStatisticsVO> userStats) throws UsageServerException;
    Long getLastAccountId() throws UsageServerException;
    Long getLastUserStatsId() throws UsageServerException;
    List<Long> listPublicTemplatesByAccount(long accountId);
}
