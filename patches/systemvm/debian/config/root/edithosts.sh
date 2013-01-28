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


 
# edithosts.sh -- edit the dhcphosts file on the routing domain
# $mac : the mac address
# $ip : the associated ip address
# $host : the hostname
# $4 : default router
# $5 : nameserver on default nic
# $6 : comma separated static routes

mac=$1
ip=$2
host=$3
dflt=$4
dns=$5
routes=$6

DHCP_HOSTS=/etc/dhcphosts.txt
DHCP_OPTS=/etc/dhcpopts.txt
DHCP_LEASES=/var/lib/misc/dnsmasq.leases
HOSTS=/etc/hosts

source /root/func.sh

lock="biglock"
locked=$(getLockFile $lock)
if [ "$locked" != "1" ]
then
    exit 1
fi

grep "redundant_router=1" /var/cache/cloud/cmdline > /dev/null
no_redundant=$?

wait_for_dnsmasq () {
  local _pid=$(pidof dnsmasq)
  for i in 0 1 2 3 4 5 6 7 8 9 10
  do
    sleep 1
    _pid=$(pidof dnsmasq)
    [ "$_pid" != "" ] && break;
  done
  [ "$_pid" != "" ] && return 0;
  logger -t cloud "edithosts: timed out waiting for dnsmasq to start"
  return 1
}

logger -t cloud "edithosts: update $1 $2 $3 to hosts"

[ ! -f $DHCP_HOSTS ] && touch $DHCP_HOSTS
[ ! -f $DHCP_OPTS ] && touch $DHCP_OPTS
[ ! -f $DHCP_LEASES ] && touch $DHCP_LEASES

#delete any previous entries from the dhcp hosts file
sed -i  /$mac/d $DHCP_HOSTS 
sed -i  /$ip,/d $DHCP_HOSTS 
sed -i  /$host,/d $DHCP_HOSTS 


#put in the new entry
echo "$mac,$ip,$host,infinite" >>$DHCP_HOSTS

#delete leases to supplied mac and ip addresses
sed -i  /$mac/d $DHCP_LEASES 
sed -i  /"$ip "/d $DHCP_LEASES 
sed -i  /"$host "/d $DHCP_LEASES 

#put in the new entry
echo "0 $mac $ip $host *" >> $DHCP_LEASES

#edit hosts file as well
sed -i  /"$ip "/d $HOSTS
sed -i  /" $host$"/d $HOSTS
echo "$ip $host" >> $HOSTS

if [ "$dflt" != "" ]
then
  #make sure dnsmasq looks into options file
  sed -i /dhcp-optsfile/d /etc/dnsmasq.conf
  echo "dhcp-optsfile=$DHCP_OPTS" >> /etc/dnsmasq.conf

  tag=$(echo $ip | tr '.' '_')
  sed -i /$tag/d $DHCP_OPTS
  if [ "$dflt" != "0.0.0.0" ]
  then
      logger -t cloud "$0: setting default router for $ip to $dflt"
      echo "$tag,3,$dflt" >> $DHCP_OPTS
  else
      logger -t cloud "$0: unset default router for $ip"
      echo "$tag,3," >> $DHCP_OPTS
  fi
  if [ "$dns" != "" ] 
  then
    logger -t cloud "$0: setting dns server for $ip to $dns"
    echo "$tag,6,$dns" >> $DHCP_OPTS
  fi
  [ "$routes" != "" ] && echo "$tag,121,$routes" >> $DHCP_OPTS
  #delete entry we just put in because we need a tag
  sed -i  /$mac/d $DHCP_HOSTS 
  #put it back with a tag
  echo "$mac,set:$tag,$ip,$host,infinite" >>$DHCP_HOSTS
fi

# make dnsmasq re-read files
pid=$(pidof dnsmasq)
if [ "$pid" != "" ]
then
  service dnsmasq restart
else
  if [ $no_redundant -eq 1 ]
  then
      wait_for_dnsmasq
  else
      logger -t cloud "edithosts: skip wait dnsmasq due to redundant virtual router"
  fi
fi

ret=$?
unlock_exit $ret $lock $locked
