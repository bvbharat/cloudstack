#!/usr/bin/python
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
 


import sys
import base64
import string 
import os
import tempfile
from subprocess import call

def vm_data(args):

    router_ip = args.pop('routerIP')
    vm_ip = args.pop('vmIP')

    for pair in args:
        pairList = pair.split(',')
        vmDataFolder = pairList[0]
        vmDataFile = pairList[1]
        vmDataValue = args[pair]
        cmd = ["/bin/bash", "/root/userdata.sh", "-v", vm_ip, "-F", vmDataFolder, "-f", vmDataFile]
        
        fd = None
        tmp_path = None
       
        try:
            fd,tmp_path = tempfile.mkstemp()
            tmpfile = open(tmp_path, 'w')

            if (vmDataFolder == "userdata" and vmDataValue != "none"):
                vmDataValue = base64.urlsafe_b64decode(vmDataValue)
            
            if vmDataValue != "none":
                tmpfile.write(vmDataValue)
            
            tmpfile.close()
            cmd.append("-d")
            cmd.append(tmp_path)
        except:
            if fd !=None:
                os.close(fd)
                os.remove(tmp_path)
                return ''
        
        try:
            call(cmd)
            txt = 'success'
        except:
            txt = ''

        if (fd != None):
            os.close(fd)
            os.remove(tmp_path)

    return txt

def parseFileData(fileName):
    args = {} 
    fd = open(fileName)

    line = fd.readline()
    while (line != ""):
        key=string.strip(line[:], '\n')
        if (key == ""):
            break
	  
        line=fd.readline()
        val=string.strip(line[:], '\n')
        args[key]=val
        line=fd.readline()
    return args

if __name__ == "__main__":
	vm_data(parseFileData("/tmp/" + sys.argv[1]))

