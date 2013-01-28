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
package com.cloud.consoleproxy;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * A simple password based encyrptor based on DES. It can serialize simple POJO object into URL safe string
 * and deserialize it back.
 * 
 */
public class ConsoleProxyPasswordBasedEncryptor {
    private static final Logger s_logger = Logger.getLogger(ConsoleProxyPasswordBasedEncryptor.class);
    
    private String password;
    private Gson gson;
    
    public ConsoleProxyPasswordBasedEncryptor(String password) {
        this.password = password;
        gson = new GsonBuilder().create();
    }
    
    public String encryptText(String text) {
        if(text == null || text.isEmpty())
            return text;
        
        assert(password != null);
        assert(!password.isEmpty());
        
        try {
            Cipher cipher = Cipher.getInstance("DES");
            int maxKeySize = 8;
            SecretKeySpec keySpec = new SecretKeySpec(normalizeKey(password.getBytes(), maxKeySize), "DES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedBytes = cipher.doFinal(text.getBytes());
            return Base64.encodeBase64URLSafeString(encryptedBytes);
        } catch (NoSuchAlgorithmException e) {
            s_logger.error("Unexpected exception ", e);
            return null;
        } catch (NoSuchPaddingException e) {
            s_logger.error("Unexpected exception ", e);
            return null;
        } catch (IllegalBlockSizeException e) {
            s_logger.error("Unexpected exception ", e);
            return null;
        } catch (BadPaddingException e) {
            s_logger.error("Unexpected exception ", e);
            return null;
        } catch (InvalidKeyException e) {
            s_logger.error("Unexpected exception ", e);
            return null;
        }
    }

    public String decryptText(String encryptedText) {
        if(encryptedText == null || encryptedText.isEmpty())
            return encryptedText;

        assert(password != null);
        assert(!password.isEmpty());

        try {
            Cipher cipher = Cipher.getInstance("DES");
            int maxKeySize = 8;
            SecretKeySpec keySpec = new SecretKeySpec(normalizeKey(password.getBytes(), maxKeySize), "DES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            
            byte[] encryptedBytes = Base64.decodeBase64(encryptedText);
            return new String(cipher.doFinal(encryptedBytes));
        } catch (NoSuchAlgorithmException e) {
            s_logger.error("Unexpected exception ", e);
            return null;
        } catch (NoSuchPaddingException e) {
            s_logger.error("Unexpected exception ", e);
            return null;
        } catch (IllegalBlockSizeException e) {
            s_logger.error("Unexpected exception ", e);
            return null;
        } catch (BadPaddingException e) {
            s_logger.error("Unexpected exception ", e);
            return null;
        } catch (InvalidKeyException e) {
            s_logger.error("Unexpected exception ", e);
            return null;
        }
    }
    
    public <T> String encryptObject(Class<?> clz, T obj) {
        if(obj == null)
            return null;
        
        String json = gson.toJson(obj);
        return encryptText(json);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T decryptObject(Class<?> clz, String encrypted) {
        if(encrypted == null || encrypted.isEmpty())
            return null;
        
        String json = decryptText(encrypted);
        return (T)gson.fromJson(json, clz);
    }
    
    private static byte[] normalizeKey(byte[] keyBytes, int keySize) {
        assert(keySize > 0);
        byte[] key = new byte[keySize];
        
        for(int i = 0; i < keyBytes.length; i++)
            key[i%keySize] ^= keyBytes[i];
        
        return key;
    }
}
