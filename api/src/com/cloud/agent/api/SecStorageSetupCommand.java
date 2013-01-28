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
package com.cloud.agent.api;

import com.cloud.agent.api.LogLevel.Log4jLevel;

public class SecStorageSetupCommand extends Command {
	private String secUrl;
	private Certificates certs;

	public static class Certificates {
	    @LogLevel(Log4jLevel.Off)
		private String privKey;
	    @LogLevel(Log4jLevel.Off)
		private String privCert;
	    @LogLevel(Log4jLevel.Off)
		private String certChain;

	    public Certificates() {

	    }

	    public Certificates(String prvKey, String privCert, String certChain) {
	    	this.privKey = prvKey;
	    	this.privCert = privCert;
	    	this.certChain = certChain;
	    }

	    public String getPrivKey() {
	    	return this.privKey;
	    }

	    public String getPrivCert() {
	    	return this.privCert;
	    }

	    public String getCertChain() {
	    	return this.certChain;
	    }
	}

	public SecStorageSetupCommand() {
		super();
	}

	public SecStorageSetupCommand(String secUrl, Certificates certs) {
		super();
		this.secUrl = secUrl;
		this.certs = certs;
	}

	@Override
	public boolean executeInSequence() {
		return true;
	}

    public String getSecUrl() {
        return secUrl;
    }

    public Certificates getCerts() {
    	return this.certs;
    }

    public void setSecUrl(String secUrl) {
        this.secUrl = secUrl;

    }
}
