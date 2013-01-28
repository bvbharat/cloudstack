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
package com.cloud.agent;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.cloud.agent.AgentShell;
import com.cloud.utils.testcase.Log4jEnabledTestCase;

public class TestAgentShell extends Log4jEnabledTestCase {
    protected final static Logger s_logger = Logger.getLogger(TestAgentShell.class);
    
    public void testWget() {
        File file = null;
        try {
            file = File.createTempFile("wget", ".html");
            AgentShell.wget("http://www.google.com/", file);
            
            if (s_logger.isDebugEnabled()) {
                s_logger.debug("file saved to " + file.getAbsolutePath());
            }
            
        } catch (final IOException e) {
            s_logger.warn("Exception while downloading agent update package, ", e);
        }
    }
}
