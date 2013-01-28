#!/usr/bin/env python
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

import Options, Utils
import os

def detect(conf):
	conf.check_message_1('Detecting JAVADIR')
	javadir = getattr(Options.options, 'JAVADIR', '')
	if javadir:
		conf.env.JAVADIR = javadir
		conf.check_message_2("%s (forced through --javadir)"%conf.env.JAVADIR,"GREEN")
	else:
		conf.env.JAVADIR = os.path.join(conf.env.DATADIR,'java')
		conf.check_message_2("%s (using default ${DATADIR}/java directory)"%conf.env.JAVADIR,"GREEN")

def set_options(opt):
        inst_dir = opt.get_option_group('--datadir') # get the group that contains bindir
        if not inst_dir: raise Utils.WafError, "DATADIR not set.  Did you load the gnu_dirs tool options with opt.tool_options('gnu_dirs') before running opt.tool_options('javadir')?"
	inst_dir.add_option('--javadir', # add javadir to the group that contains bindir
		help = 'Java class and jar files [Default: ${DATADIR}/java]',
		default = '',
		dest = 'JAVADIR')
