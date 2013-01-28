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
package com.cloud.storage.template;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import javax.ejb.Local;
import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;

import com.cloud.exception.InternalErrorException;
import com.cloud.storage.Storage.ImageFormat;
import com.cloud.storage.StorageLayer;
import com.cloud.utils.NumbersUtil;

/**
 * VhdProcessor processes the downloaded template for VHD.  It
 * currently does not handle any type of template conversion
 * into the VHD format.
 *
 */
@Local(value=Processor.class)
public class VhdProcessor implements Processor {
    
    private static final Logger s_logger = Logger.getLogger(VhdProcessor.class);
    String _name;
    StorageLayer _storage;
    private int vhd_footer_size = 512;
    private int vhd_footer_creator_app_offset = 28;
    private int vhd_footer_creator_ver_offset = 32;
    private int vhd_footer_current_size_offset = 48;
    private byte[][] citrix_creator_app = {{0x74, 0x61, 0x70, 0x00},{0x43, 0x54, 0x58, 0x53}}; /*"tap ", and "CTXS"*/

    @Override
    public FormatInfo process(String templatePath, ImageFormat format, String templateName) throws InternalErrorException {
        if (format != null) {
            s_logger.debug("We currently don't handle conversion from " + format + " to VHD.");
            return null;
        }
        
        String vhdPath = templatePath + File.separator + templateName + "." + ImageFormat.VHD.getFileExtension();
       
        if (!_storage.exists(vhdPath)) {
            s_logger.debug("Unable to find the vhd file: " + vhdPath);
            return null;
        }
        
        FormatInfo info = new FormatInfo();
        info.format = ImageFormat.VHD;
        info.filename = templateName + "." + ImageFormat.VHD.getFileExtension();
        
        File vhdFile = _storage.getFile(vhdPath);
        
        info.size = _storage.getSize(vhdPath);
        FileInputStream strm = null;
        byte[] currentSize = new byte[8];
        byte[] creatorApp = new byte[4];
        try {
            strm = new FileInputStream(vhdFile);
            strm.skip(info.size - vhd_footer_size + vhd_footer_creator_app_offset);
            strm.read(creatorApp);
            strm.skip(vhd_footer_current_size_offset - vhd_footer_creator_ver_offset);
            strm.read(currentSize);           
        } catch (Exception e) {
            s_logger.warn("Unable to read vhd file " + vhdPath, e);
            throw new InternalErrorException("Unable to read vhd file " + vhdPath + ": " + e);
        } finally {
            if (strm != null) {
                try {
                    strm.close();
                } catch (IOException e) {
                }
            }
        }
        
        //imageSignatureCheck(creatorApp);
        
        long templateSize = NumbersUtil.bytesToLong(currentSize);
        info.virtualSize = templateSize;

        return info;
    }

    @Override
    public boolean configure(String name, Map<String, Object> params) throws ConfigurationException {
        _name = name;
        _storage = (StorageLayer)params.get(StorageLayer.InstanceConfigKey);
        if (_storage == null) {
            throw new ConfigurationException("Unable to get storage implementation");
        }
        
        return true;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
    
    private void imageSignatureCheck(byte[] creatorApp) throws InternalErrorException {
    	boolean findKnownCreator = false;
    	for (int i = 0; i < citrix_creator_app.length; i++) {
    		if (Arrays.equals(creatorApp, citrix_creator_app[i])) {
    			findKnownCreator = true;
    			break;
    		}
    	}
    	if (!findKnownCreator) {
    		/*Only support VHD image created by citrix xenserver, and xenconverter*/
    		String readableCreator = "";
    		for (int j = 0; j < creatorApp.length; j++) {
    			readableCreator += (char)creatorApp[j];
    		}
    		throw new InternalErrorException("Image creator is:" + readableCreator +", is not supported");
    	}
    }
}
