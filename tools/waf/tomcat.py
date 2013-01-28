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
	if not conf.env.DATADIR:
		conf.fatal("DATADIR not found in the environment.  Did you run conf.check_tool('gnu_dirs') before running check_tool('tomcat')?")
	conf.check_message_1('Detecting Tomcat')
	conf.env.TOMCATHOME = ''
	tomcathome = getattr(Options.options, 'TOMCATHOME', '')
	if tomcathome:
		conf.env.TOMCATHOME = tomcathome
		method = "forced through --with-tomcat"
	else:
		if    "TOMCAT_HOME" in conf.environ and conf.environ['TOMCAT_HOME'].strip():
			conf.env.TOMCATHOME = conf.environ["TOMCAT_HOME"]
			method = 'got through environment variable %TOMCAT_HOME%'
		elif  "CATALINA_HOME" in conf.environ and conf.environ['CATALINA_HOME'].strip():
			conf.env.TOMCATHOME = conf.environ['CATALINA_HOME']
			method = 'got through environment variable %CATALINA_HOME%'
		elif os.path.isdir(os.path.join(conf.env.DATADIR,"tomcat6")):
			conf.env.TOMCATHOME = os.path.join(conf.env.DATADIR,"tomcat6")
			method = 'detected existence of Tomcat directory under $DATADIR'
		elif os.path.isdir("/usr/share/tomcat6"):
			conf.env.TOMCATHOME = "/usr/share/tomcat6"
			method = 'detected existence of standard Linux system directory'
	if not conf.env.TOMCATHOME:
		conf.fatal("Could not detect Tomcat")
	elif not os.path.isdir(conf.env.TOMCATHOME):
		conf.fatal("Tomcat cannot be found at %s"%conf.env.TOMCATHOME)
	else:
		conf.check_message_2("%s (%s)"%(conf.env.TOMCATHOME,method),"GREEN")

def set_options(opt):
        inst_dir = opt.get_option_group('--datadir') # get the group that contains bindir
        if not inst_dir: raise Utils.WafError, "DATADIR not set.  Did you load the gnu_dirs tool options with opt.tool_options('gnu_dirs') before running opt.tool_options('tomcat')?"
	inst_dir.add_option('--with-tomcat', # add javadir to the group that contains bindir
		help = 'Path to installed Tomcat 6 environment [Default: ${DATADIR}/tomcat6 (unless %%CATALINA_HOME%% is set)]',
		default = '',
		dest = 'TOMCATHOME')
