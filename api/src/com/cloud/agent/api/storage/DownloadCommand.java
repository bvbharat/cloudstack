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

import java.net.URI;

import com.cloud.storage.Volume;
import com.cloud.storage.Storage.ImageFormat;
import com.cloud.template.VirtualMachineTemplate;
import org.apache.cloudstack.api.InternalIdentity;


public class DownloadCommand extends AbstractDownloadCommand implements InternalIdentity {
	public static class PasswordAuth {
		String userName;
		String password;
		public PasswordAuth() {

		}
		public PasswordAuth(String user, String password) {
			this.userName = user;
			this.password = password;
		}
		public String getUserName() {
			return userName;
		}
		public String getPassword() {
			return password;
		}
	}

    public static enum ResourceType {
        VOLUME, TEMPLATE
    }

	public static class Proxy {
		private String _host;
		private int _port;
		private String _userName;
		private String _password;

		public Proxy() {

		}

		public Proxy(String host, int port, String userName, String password) {
			this._host = host;
			this._port = port;
			this._userName = userName;
			this._password = password;
		}

		public Proxy(URI uri) {
			this._host = uri.getHost();
			this._port = uri.getPort() == -1 ? 3128 : uri.getPort();
			String userInfo = uri.getUserInfo();
			if (userInfo != null) {
				String[] tokens = userInfo.split(":");
				if (tokens.length == 1) {
					this._userName = userInfo;
					this._password = "";
				} else if (tokens.length == 2) {
					this._userName = tokens[0];
					this._password = tokens[1];
				}
			}
		}

		public String getHost() {
			return _host;
		}

		public int getPort() {
			return _port;
		}

		public String getUserName() {
			return _userName;
		}

		public String getPassword() {
			return _password;
		}
	}
	private boolean hvm;
	private String description;
	private String checksum;
	private PasswordAuth auth;
	private Proxy _proxy;
	private Long maxDownloadSizeInBytes = null;
	private long id;
	private ResourceType resourceType = ResourceType.TEMPLATE;

	protected DownloadCommand() {
	}


	public DownloadCommand(DownloadCommand that) {
	    super(that);
	    this.hvm = that.hvm;
	    this.checksum = that.checksum;
	    this.id = that.id;
	    this.description = that.description;
	    this.auth = that.getAuth();
	    this.setSecUrl(that.getSecUrl());
	    this.maxDownloadSizeInBytes = that.getMaxDownloadSizeInBytes();
	    this.resourceType = that.resourceType;
	}

	public DownloadCommand(String secUrl, VirtualMachineTemplate template, Long maxDownloadSizeInBytes) {
	    super(template.getUniqueName(), template.getUrl(), template.getFormat(), template.getAccountId());
	    this.hvm = template.isRequiresHvm();
	    this.checksum = template.getChecksum();
	    this.id = template.getId();
	    this.description = template.getDisplayText();
	    this.setSecUrl(secUrl);
	    this.maxDownloadSizeInBytes = maxDownloadSizeInBytes;
	}

	public DownloadCommand(String secUrl, Volume volume, Long maxDownloadSizeInBytes, String checkSum, String url, ImageFormat format) {
	    super(volume.getName(), url, format, volume.getAccountId());
	    //this.hvm = volume.isRequiresHvm();
	    this.checksum = checkSum;
	    this.id = volume.getId();
	    this.setSecUrl(secUrl);
	    this.maxDownloadSizeInBytes = maxDownloadSizeInBytes;
	    this.resourceType = ResourceType.VOLUME;
	}

	public DownloadCommand(String secUrl, String url, VirtualMachineTemplate template, String user, String passwd, Long maxDownloadSizeInBytes) {
	    super(template.getUniqueName(), url, template.getFormat(), template.getAccountId());
        this.hvm = template.isRequiresHvm();
        this.checksum = template.getChecksum();
        this.id = template.getId();
        this.description = template.getDisplayText();
        this.setSecUrl(secUrl);
        this.maxDownloadSizeInBytes = maxDownloadSizeInBytes;
		auth = new PasswordAuth(user, passwd);
	}

	public long getId() {
	    return id;
	}

	public void setHvm(boolean hvm) {
		this.hvm = hvm;
	}

	public boolean isHvm() {
		return hvm;
	}

	public String getDescription() {
		return description;
	}

	public String getChecksum() {
		return checksum;
	}

    public void setDescription(String description) {
		this.description = description;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

    @Override
    public boolean executeInSequence() {
        return false;
    }


	public PasswordAuth getAuth() {
		return auth;
	}

	public void setCreds(String userName, String passwd) {
		auth = new PasswordAuth(userName, passwd);
	}

	public Proxy getProxy() {
		return _proxy;
	}

	public void setProxy(Proxy proxy) {
		_proxy = proxy;
	}

	public Long getMaxDownloadSizeInBytes() {
		return maxDownloadSizeInBytes;
	}


	public ResourceType getResourceType() {
		return resourceType;
	}


	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}
}
