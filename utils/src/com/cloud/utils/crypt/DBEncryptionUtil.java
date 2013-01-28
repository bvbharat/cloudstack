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
// under the License.package com.cloud.utils.crypt;
package com.cloud.utils.crypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.properties.EncryptableProperties;

import com.cloud.utils.PropertiesUtil;
import com.cloud.utils.exception.CloudRuntimeException;

public class DBEncryptionUtil {
	
	public static final Logger s_logger = Logger.getLogger(DBEncryptionUtil.class);
	private static StandardPBEStringEncryptor s_encryptor = null;
	
    public static String encrypt(String plain){
    	if(!EncryptionSecretKeyChecker.useEncryption() || (plain == null) || plain.isEmpty()){
    		return plain;
    	}
    	if(s_encryptor == null){
    		initialize();
    	}
    	String encryptedString = null;
		try {
			encryptedString = s_encryptor.encrypt(plain);
		} catch (EncryptionOperationNotPossibleException e) {
			s_logger.debug("Error while encrypting: "+plain);
			throw e;
		}
    	return encryptedString;
    }
    
    public static String decrypt(String encrypted){
    	if(!EncryptionSecretKeyChecker.useEncryption() || (encrypted == null) || encrypted.isEmpty()){
    		return encrypted;
    	}
    	if(s_encryptor == null){
    		initialize();
    	}
    	
    	String plain = null;
    	try {
			plain = s_encryptor.decrypt(encrypted);
		} catch (EncryptionOperationNotPossibleException e) {
			s_logger.debug("Error while decrypting: "+encrypted);
			throw e;
		}
    	return plain;
    }
    
    private static void initialize(){
    	final File dbPropsFile = PropertiesUtil.findConfigFile("db.properties");
        final Properties dbProps; 
        
        if(EncryptionSecretKeyChecker.useEncryption()){
        	StandardPBEStringEncryptor encryptor = EncryptionSecretKeyChecker.getEncryptor();
        	dbProps = new EncryptableProperties(encryptor);
        	try {
				dbProps.load(new FileInputStream(dbPropsFile));
			} catch (FileNotFoundException e) {
				throw new CloudRuntimeException("db.properties file not found while reading DB secret key", e);
			} catch (IOException e) {
				throw new CloudRuntimeException("Erroe while reading DB secret key from db.properties", e);
			}
        	
        	String dbSecretKey = dbProps.getProperty("db.cloud.encrypt.secret");
        	if(dbSecretKey == null || dbSecretKey.isEmpty()){
        		throw new CloudRuntimeException("Empty DB secret key in db.properties");
        	}
        	
        	s_encryptor = new StandardPBEStringEncryptor();
        	s_encryptor.setAlgorithm("PBEWithMD5AndDES");
        	s_encryptor.setPassword(dbSecretKey);
        } else {
        	throw new CloudRuntimeException("Trying to encrypt db values when encrytion is not enabled");
        }
    }
}
