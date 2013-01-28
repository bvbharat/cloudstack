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

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

import com.cloud.utils.exception.CloudRuntimeException;

public class NumbersUtil {
    public static long parseLong(String s, long defaultValue) {
        if (s == null) {
            return defaultValue;
        }

        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static int parseInt(String s, int defaultValue) {
        if (s == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static float parseFloat(String s, float defaultValue) {
    	if (s == null) {
    		return defaultValue;
    	}
    	try {
    		return Float.parseFloat(s);
    	} catch (NumberFormatException e) {
    		return defaultValue;
    	}
    }

    
    /**
     * Converts bytes to short on input.
     */
    public static int bytesToShort(byte b[]) {
        return (b[1] & 0xff) | ((b[0] << 8) & 0xff00);
    }

    public static int bytesToShort(byte b[], int pos) {
        return (b[pos + 1] & 0xff) | ((b[pos] << 8) & 0xff00);
    }

    /**
     * Converts bytes to long on input.
     */
    public static int bytesToInt(byte b[]) {
        return bytesToInt(b, 0);
    }

    public static int bytesToInt(byte b[], int pos) {
        int value = b[pos + 3] & 0xff;
        value |= (b[pos + 2] << 8) & 0xff00;
        value |= (b[pos + 1] << 16) & 0xff0000;
        value |= (b[pos] << 24) & 0xff000000;
        return value;
    }

    /**
     * Converts a short to a series of bytes for output. Must be 2 bytes long.
     */
    public static byte[] shortToBytes(int n) {
        byte b[] = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) ((n >> 8) & 0xff);
        return b;
    }

    public static char encodeByte(int b) {
        if (b < 10) {
            return (char) (b + '0');
        } else if (b < 36) {
            return (char) (b - 10 + 'A');
        } else if (b < 62) {
            return (char) (b - 36 + 'a');
        } else if (b == 62) {
            return '(';
        } else if (b == 63) {
            return ')';
        }
        return (char) 255;
    }
    
    public static int decodeByte(char b) {
        if (b >= 'A' && b <= 'Z') {
            return b + 10 - 'A';
        } else if (b >= 'a' && b <= 'z') {
            return b + 36 - 'a';
        } else if (b >= '0' && b <= '9') {
            return b - '0';
        } else if (b == ')') {
            return 63;
        } else if (b == '(') {
            return 62;
        }
        return -1;
    }

    /**
     * Converts a long to a series of bytes for output. Must be 4 bytes long.
     */
    public static byte[] intToBytes(int n) {
        byte b[] = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) ((n >> 8) & 0xff);
        b[1] = (byte) ((n >> 16) & 0xff);
        b[0] = (byte) ((n >> 24) & 0xff);
        return b;
    }

    /**
     * Sorry for the bad naming but the longToBytes is already taken. Returns an 8 byte long byte array.
     **/
    public static byte[] longToBytes(long n) {
        byte b[] = new byte[8];
        b[7] = (byte) (n & 0xff);
        b[6] = (byte) ((n >> 8) & 0xff);
        b[5] = (byte) ((n >> 16) & 0xff);
        b[4] = (byte) ((n >> 24) & 0xff);
        b[3] = (byte) ((n >> 32) & 0xff);
        b[2] = (byte) ((n >> 40) & 0xff);
        b[1] = (byte) ((n >> 48) & 0xff);
        b[0] = (byte) ((n >> 56) & 0xff);
        return b;
    }

    /**
     * Converts bytes to long on input.
     */
    public static long bytesToLong(byte b[]) {
        return bytesToLong(b, 0);
    }

    public static long bytesToLong(byte b[], int pos) {
        ByteBuffer buf = ByteBuffer.wrap(b, pos, 8);
        return buf.getLong();
        /*
         * long value = b[pos + 7] & 0xff;
         * value |= (b[pos + 6] << 8) & 0xff00;
         * value |= (b[pos + 5] << 16) & 0xff0000;
         * value |= (b[pos + 4] << 24) & 0xff000000;
         * value |= (b[pos + 3] << 32) & 0xff00000000;
         * value |= (b[pos + 2] << 40) & 0xff0000000000;
         * value |= (b[pos + 1] << 48) & 0xff000000000000;
         * value |= (b[pos + 0] << 56) & 0xff00000000000000;
         * return value;
         */
    }

    /**
     * Converts a byte array to a hex readable string.
     **/
    public static String bytesToString(byte[] data, int start, int end) {
        StringBuilder buf = new StringBuilder();
        if (end > data.length) {
            end = data.length;
        }
        for (int i = start; i < end; i++) {
            buf.append(" ");
            buf.append(Integer.toHexString(data[i] & 0xff));
        }
        return buf.toString();
    }
    
    protected static final long KB = 1024;
    protected static final long MB = 1024 * KB;
    protected static final long GB = 1024 * MB;
    protected static final long TB = 1024 * GB;
    public static String toReadableSize(long bytes) {
        if (bytes <= KB && bytes >= 0) {
            return Long.toString(bytes) + " bytes";
        } else if (bytes <= MB) {
            StringBuilder builder = new StringBuilder();
            Formatter format = new Formatter(builder);
            format.format("%.2f KB", (float)bytes / (float)KB);
            return builder.toString();
        } else if (bytes <= GB) {
            StringBuilder builder = new StringBuilder();
            Formatter format = new Formatter(builder);
            format.format("%.2f MB", (float)bytes / (float)MB);
            return builder.toString();
        } else if (bytes <= TB) {
            StringBuilder builder = new StringBuilder();
            Formatter format = new Formatter(builder);
            format.format("%.2f GB", (float)bytes / (float)GB);
            return builder.toString();
        } else {
            StringBuilder builder = new StringBuilder();
            Formatter format = new Formatter(builder);
            format.format("%.4f TB", (float)bytes / (float)TB);
            return builder.toString();
        }
    }
    
    /**
     * Converts a string of the format 'yy-MM-dd'T'HH:mm:ss.SSS" into ms.
     * 
     * @param str containing the interval.
     * @param defaultValue value to return if str doesn't parse.  If -1, throws VmopsRuntimeException
     * @return interval in ms
     */
    public static long parseInterval(String str, long defaultValue) {
        SimpleDateFormat sdf = null;
        if (str.contains("D")) {
            sdf = new SimpleDateFormat("dd'D'HH'h'mm'M'ss'S'SSS'ms'");
        } else if (str.contains("h")) {
            sdf = new SimpleDateFormat("HH'h'mm'M'ss'S'SSS'ms'");
        } else if (str.contains("M")) {
            sdf = new SimpleDateFormat("mm'M'ss'S'SSS'ms'");
        } else if (str.contains("S")) {
            sdf = new SimpleDateFormat("ss'S'SSS'ms'");
        } else if (str.contains("ms")) {
        	sdf = new SimpleDateFormat("SSS'ms'");
        }
        Date date;
        try {
            if (str == null || sdf == null) {
                throw new ParseException("String is wrong", 0);
            }
            date = sdf.parse(str);
            return date.getTime();
        } catch (ParseException e) {
            if (defaultValue != -1) {
                return defaultValue;
            } else {
                throw new CloudRuntimeException("Unable to parse: " + str, e);
            }
        }
    }
    
    public static int hash(long value) {
        return (int)(value^(value>>>32));
    }
    
    public static void main(String[] args) {
        long interval = parseInterval(args[0], -1);
        System.out.println(args[0] + " is " + interval);
    }
}
