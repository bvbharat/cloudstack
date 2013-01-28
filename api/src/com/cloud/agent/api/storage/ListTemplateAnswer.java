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
package com.cloud.agent.api.storage;

import java.util.Map;

import com.cloud.agent.api.Answer;

import com.cloud.storage.template.TemplateInfo;

public class ListTemplateAnswer extends Answer  {
    private String secUrl;
    private Map<String, TemplateInfo> templateInfos;

	public ListTemplateAnswer() {

	}

	public ListTemplateAnswer(String secUrl, Map<String, TemplateInfo> templateInfos) {
	    super(null, true, "success");
	    this.setSecUrl(secUrl);
	    this.templateInfos = templateInfos;
	}

	public Map<String, TemplateInfo> getTemplateInfo() {
	    return templateInfos;
	}

	public void setTemplateInfo(Map<String, TemplateInfo> templateInfos) {
	    this.templateInfos = templateInfos;
	}

    public void setSecUrl(String secUrl) {
        this.secUrl = secUrl;
    }

    public String getSecUrl() {
        return secUrl;
    }
}
