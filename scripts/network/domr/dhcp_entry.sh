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


# $Id: dhcp_entry.sh 9804 2010-06-22 18:36:49Z alex $ $HeadURL: svn://svn.lab.vmops.com/repos/vmdev/java/scripts/network/domr/dhcp_entry.sh $
# dhcp_entry.sh -- add dhcp entry on domr
# @VERSION@

usage() {
  printf "Usage: %s: -r <domr-ip> -m <vm mac> -v <vm ip> -n <vm name>\n" $(basename $0) >&2
  exit 2
}

cert="/root/.ssh/id_rsa.cloud"

add_dhcp_entry() {
  local domr=$1
  local mac=$2
  local ip=$3
  local vm=$4
  local dfltrt=$5
  local ns=$6
  local staticrt=$7
  ssh -p 3922 -o StrictHostKeyChecking=no -i $cert root@$domr "/root/edithosts.sh $mac $ip $vm $dfltrt $ns $staticrt" >/dev/null
  return $?
}

domrIp=
vmMac=
vmIp=
vmName=
staticrt=
dfltrt=
dns=

while getopts 'r:m:v:n:d:s:N:' OPTION
do
  case $OPTION in
  r)	domrIp="$OPTARG"
		;;
  v)	vmIp="$OPTARG"
		;;
  m)	vmMac="$OPTARG"
		;;
  n)	vmName="$OPTARG"
		;;
  s)	staticrt="$OPTARG"
		;;
  d)	dfltrt="$OPTARG"
		;;
  N)	dns="$OPTARG"
		;;
  ?)    usage
		exit 1
		;;
  esac
done

add_dhcp_entry $domrIp $vmMac $vmIp $vmName $dfltrt $dns $staticrt

exit $?
