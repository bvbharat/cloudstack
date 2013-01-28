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
package com.cloud.utils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import com.cloud.utils.exception.CloudRuntimeException;

public class UriUtils {
    public static String formNfsUri(String host, String path) {
        try {
            URI uri = new URI("nfs", host, path, null);
            return uri.toString();
        } catch (URISyntaxException e) {
            throw new CloudRuntimeException("Unable to form nfs URI: " + host + " - " + path);
        }
    }
    
    public static String formIscsiUri(String host, String iqn, Integer lun) {
        try {
            String path = iqn;
            if (lun != null) {
                path += "/" + lun.toString();
            }
            URI uri = new URI("iscsi", host, path, null);
            return uri.toString();
        } catch (URISyntaxException e) {
            throw new CloudRuntimeException("Unable to form iscsi URI: " + host + " - " + iqn + " - " + lun);
        }
    }

    public static String formFileUri(String path) {
        File file = new File(path);
        
        return file.toURI().toString();
    }
    
    // a simple URI component helper (Note: it does not deal with URI paramemeter area)
    public static String encodeURIComponent(String url) {
    	int schemeTail = url.indexOf("://");
   	
    	int pathStart = 0;
    	if(schemeTail > 0)
    		pathStart = url.indexOf('/', schemeTail + 3);
    	else
    		pathStart = url.indexOf('/');
    	
    	if(pathStart > 0) {
    		String[] tokens = url.substring(pathStart + 1).split("/");
    		if(tokens != null) {
    			StringBuffer sb = new StringBuffer();
    			sb.append(url.substring(0, pathStart));
    			for(String token : tokens) {
    				sb.append("/").append(URLEncoder.encode(token));
    			}
    			
    			return sb.toString();
    		}
    	}
    	
		// no need to do URL component encoding
		return url;
    }
}
