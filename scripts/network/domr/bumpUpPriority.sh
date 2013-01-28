#!/bin/bash
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


usage() {
  printf "Usage:\n %s <domR eth1 ip> \n" $(basename $0) >&2
  printf " %s <domR eth1 ip> \n" $(basename $0) >&2
}

cert="/root/.ssh/id_rsa.cloud"
domRIp=$1
shift

check_gw() {
  ping -c 1 -n -q $1 > /dev/null
  if [ $? -gt 0 ]
  then
    sleep 1
    ping -c 1 -n -q $1 > /dev/null
  fi
  return $?;
}


check_gw "$domRIp"
if [ $? -gt 0 ]
then
  exit 1
fi

ssh -p 3922 -q -o StrictHostKeyChecking=no -i $cert root@$domRIp "/root/bumpup_priority.sh"
exit $?
