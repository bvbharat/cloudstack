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


# $Id: call_loadbalancer.sh 9132 2010-06-04 20:17:43Z manuel $ $HeadURL: svn://svn.lab.vmops.com/repos/branches/2.0.0/java/scripts/vm/hypervisor/xenserver/patch/call_loadbalancer.sh $
# loadbalancer.sh -- reconfigure loadbalancer rules

usage() {
  printf "Usage: %s:  -i <domR eth1 ip>  -a <added public ip address> -d <removed> -f <load balancer config> -s <stats guest ip >\n" $(basename $0) >&2
}

set -x

check_gw() {
  ping -c 1 -n -q $1 > /dev/null
  if [ $? -gt 0 ]
  then
    sleep 1
    ping -c 1 -n -q $1 > /dev/null
  fi
  return $?;
}

copy_haproxy() {
  local domRIp=$1
  local cfg=$2

  scp -P 3922 -q -o StrictHostKeyChecking=no -i $cert $cfg root@$domRIp:/etc/haproxy/haproxy.cfg.new
  return $?
}

iflag=
aflag=
dflag=
fflag=
sflag=

while getopts 'i:a:d:f:s:' OPTION
do
  case $OPTION in
  i)	iflag=1
		domRIp="$OPTARG"
		;;
  a)	aflag=1
		addedIps="$OPTARG"
		;;
  d)	dflag=1
		removedIps="$OPTARG"
		;;
  f)	fflag=1
		cfgfile="$OPTARG"
		;;
  s)	sflag=1
		statsIps="$OPTARG"
		;;
  ?)	usage
		exit 2
		;;
  esac
done

cert="/root/.ssh/id_rsa.cloud"

if [ "$iflag$fflag" != "11" ]
then
  usage
  exit 2
fi

# Check if DomR is up and running. If it isn't, exit 1.
check_gw "$domRIp"
if [ $? -gt 0 ]
then
  exit 1
fi

copy_haproxy $domRIp $cfgfile

if [ $? -gt 0 ]
then
  printf "Reconfiguring loadbalancer failed\n"
  exit 1
fi
	
ssh -p 3922 -q -o StrictHostKeyChecking=no -i $cert root@$domRIp "/root/loadbalancer.sh $*"
exit $?	
