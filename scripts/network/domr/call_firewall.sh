#!/usr/bin/env bash
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


# $Id: call_firewall.sh 9132 2010-06-04 20:17:43Z manuel $ $HeadURL: svn://svn.lab.vmops.com/repos/branches/2.0.0/java/scripts/vm/hypervisor/xenserver/patch/call_firewall.sh $
# firewall.sh -- allow some ports / protocols to vm instances
usage() {
  printf "Usage for Firewall rule  : %s: <domR eth1 ip> -F " $(basename $0) >&2
  printf "Usage for other purposes : %s: <domR eth1 ip> (-A|-D) -i <domR eth1 ip>  -r <target-instance-ip> -P protocol (-p port_range | -t icmp_type_code)  -l <public ip address> -d <target port> [-f <firewall ip> -u <firewall user> -y <firewall password> -z <firewall enable password> ] \n" $(basename $0) >&2
}

#set -x

check_gw() {
  ping -c 1 -n -q $1 > /dev/null
  if [ $? -gt 0 ]
  then
    sleep 1
    ping -c 1 -n -q $1 > /dev/null
  fi
  return $?;
}

cert="/root/.ssh/id_rsa.cloud"
domRIp=$1
shift

check_gw "$domRIp"
if [ $? -gt 0 ]
then
  exit 1
fi
fflag=
eflag=
while getopts ':FE' OPTION
do
  case $OPTION in 
  F)    fflag=1
      	  ;;
  E) eflag=1
	  ;;
  \?)  ;;
  esac
done

if [ -n "$eflag" ]
then
	ssh -p 3922 -q -o StrictHostKeyChecking=no -i $cert root@$domRIp "/root/firewallRule_egress.sh $*"
elif [ -n "$fflag" ]
then
	ssh -p 3922 -q -o StrictHostKeyChecking=no -i $cert root@$domRIp "/root/firewall_rule.sh $*"
else
	ssh -p 3922 -q -o StrictHostKeyChecking=no -i $cert root@$domRIp "/root/firewall.sh $*"
fi
exit $?
