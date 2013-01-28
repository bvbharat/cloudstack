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
package com.cloud.user;

import com.cloud.server.ManagementService;
import com.cloud.utils.component.ComponentLocator;

public class UserContext {

    private static ThreadLocal<UserContext> s_currentContext = new ThreadLocal<UserContext>();
    private static final ComponentLocator locator = ComponentLocator.getLocator(ManagementService.Name);
    private static final AccountService _accountMgr = locator.getManager(AccountService.class);

    private long userId;
    private String sessionId;
    private Account account;
    private long startEventId = 0;
    private long accountId;
    private String eventDetails;

    private boolean apiServer;

    private static UserContext s_adminContext = new UserContext(_accountMgr.getSystemUser().getId(), _accountMgr.getSystemAccount(), null, false);

    public UserContext() {
    }

    public UserContext(long userId, Account accountObject, String sessionId, boolean apiServer) {
        this.userId = userId;
        this.account = accountObject;
        this.sessionId = sessionId;
        this.apiServer = apiServer;
    }

    public long getCallerUserId() {
        return userId;
    }

    public User getCallerUser() {
        return _accountMgr.getActiveUser(userId);
    }

    public void setCallerUserId(long userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Account getCaller() {
        return account;
    }

    public void setCaller(Account accountObject) {
        this.account = accountObject;
    }

    public void setSessionKey(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isApiServer() {
        return apiServer;
    }

    public void setApiServer(boolean apiServer) {
        this.apiServer = apiServer;
    }

    public static UserContext current() {
        UserContext context = s_currentContext.get();
        if (context == null) {
            //
            // TODO: we should enforce explicit UserContext setup at major entry-points for security concerns,
            // however, there are many places that run background jobs assume the system context.
            //
            // If there is a security concern, all entry points from user (including the front end that takes HTTP
// request in and
            // the core async-job manager that runs commands from user) have explicitly setup the UserContext.
            //
            return s_adminContext;
        }
        return context;
    }

    public static void updateContext(long userId, Account accountObject, String sessionId) {
        UserContext context = current();
        assert (context != null) : "Context should be already setup before you can call this one";

        context.setCallerUserId(userId);
        context.setCaller(accountObject);
        context.setSessionKey(sessionId);
    }

    public static void registerContext(long userId, Account accountObject, String sessionId, boolean apiServer) {
        s_currentContext.set(new UserContext(userId, accountObject, sessionId, apiServer));
    }

    public static void unregisterContext() {
        s_currentContext.set(null);
    }

    public void setStartEventId(long startEventId) {
        this.startEventId = startEventId;
    }

    public long getStartEventId() {
        return startEventId;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public void setEventDetails(String eventDetails) {
        this.eventDetails = eventDetails;
    }

    public String getEventDetails() {
        return eventDetails;
    }
}
