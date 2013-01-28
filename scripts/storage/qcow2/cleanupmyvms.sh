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

 

# $Id: cleanupmyvms.sh 9804 2010-06-22 18:36:49Z alex $ $HeadURL: svn://svn.lab.vmops.com/repos/vmdev/java/scripts/storage/qcow2/cleanupmyvms.sh $
# @VERSION@

if [ "$1" == "" ]
then
  echo "Usage: $(basename $0) <instance>"
  exit 3
fi

instance=$1

for i in $(virsh list | grep $instance | awk '{print $2}' );  
do   
  files=$(virsh dumpxml $i | grep file | grep source | awk '{print $2}' | awk -F"=" '{print $2}'  );   
  echo "Destroying VM: $i"
  virsh destroy $i
  virsh undefine $i
  sleep 2
  for f in $files; 
  do  
     f=${f%%/>}
     echo "Destroying disk: $f"
     rm -f $f
  done; 
done

#double check, if there are vms in shut-off state
for i in $(virsh list --all |grep $instance| awk '{print $2}')
do
  files=$(virsh dumpxml $i | grep file | grep source | awk '{print $2}' | awk -F"=" '{print $2}'  );   
  echo "Undefine VM: $i"
  virsh undefine $i
  for f in $files;
  do
     f=${f%%/>}
     if [ -f $f ]
     then
     	f=${f%%/>}
     	echo "Destroying disk: $f"
     	rm -f $f
     fi
  done
done

#cleanup the bridge
br=`virsh net-list|grep $instance |awk '{print $1}'`
if [ -n "$br" ]
then
virsh net-destroy $br
fi
#double check
br=`brctl show |grep $instance|awk '{print $1}'`
if [ -n "$br" ]
then
   ifconfig $br down
   sleep 1
   brctl delbr $br
   sleep 1
fi

dnsmasq=`cat /var/run/libvirt/network/vmops-$instance-private.pid`
kill -9 $dnsmasq 2>/dev/null

