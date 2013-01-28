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
package com.cloud.utils.testcase;

import java.lang.annotation.Annotation;

import com.cloud.utils.component.ComponentLocator;

public class ComponentTestCase extends Log4jEnabledTestCase {
	@Override
    protected void setUp() {
		super.setUp();
		
		Annotation[] annotations = getClass().getAnnotations();
		if(annotations != null) {
			for(Annotation annotation : annotations) {
				if(annotation instanceof ComponentSetup) {
					ComponentLocator.getLocator(
						((ComponentSetup)annotation).managerName(),
						((ComponentSetup)annotation).setupXml(),
						((ComponentSetup)annotation).log4j()
					);
					
					break;
				}
			}
		}
	}
}

