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
import Utils, Build
from TaskGen import feature, before
from Configure import ConfigurationError
import Options
import Task
import os

def detect(conf):
	if Options.platform == 'win32': raise Utils.WafError('the usermgmt tool only works on Linux')
	if Options.platform == 'darwin': raise Utils.WafError('the usermgmt tool only works on Linux')
	path_list = ["/usr/local/sbin","/usr/sbin","/sbin"] + os.environ.get('PATH','').split(os.pathsep)
	conf.find_program("useradd",var='USERADD',mandatory=True,path_list=path_list)
	conf.find_program("userdel",var='USERDEL',mandatory=True,path_list=path_list)

def set_options(opt):
	if Options.platform == 'win32': raise Utils.WafError('the usermgmt tool only works on Linux')
	if Options.platform == 'darwin': raise Utils.WafError('the usermgmt tool only works on Linux')
	og = opt.get_option_group('--force')
	og.add_option('--nochown',
		action = 'store_true',
		help = 'do not create or remove user accounts or change file ownership on installed files',
		default = False,
		dest = 'NOUSERMGMT')

def _subst_add_destdir(x,bld):
	a = "${DESTDIR}" + x
	a = a.replace("${DESTDIR}",Options.options.destdir)
	a = Utils.subst_vars(a,bld.env)
	if a.startswith("//"): a = a[1:]
	return a
Build.BuildContext.subst_add_destdir = staticmethod(_subst_add_destdir)

def _setownership(ctx,path,owner,group,mode=None):
	if Options.platform == 'win32': return
	if Options.platform == 'darwin': return
	if not hasattr(os,"getuid"): return
	if os.getuid() != 0: return
	if Options.options.NOUSERMGMT: return

	import pwd
	import grp
	import stat
	from os import chown as _chown, chmod as _chmod

	def f(bld,path,owner,group,mode):
		
		try: uid = pwd.getpwnam(owner).pw_uid
		except KeyError,e:
			raise Utils.WafError("Before using setownership() you have to create the user with bld.createuser(username...)")
		try: gid = grp.getgrnam(group).gr_gid
		except KeyError,e:
			raise Utils.WafError("Before using setownership() you have to create the user with bld.createuser(username...)")
		
		path = bld.subst_add_destdir(path,bld)
		current_uid,current_gid = os.stat(path).st_uid,os.stat(path).st_gid
		if current_uid != uid:
			Utils.pprint("GREEN","* setting owner of %s to UID %s"%(path,uid))
			_chown(path,uid,current_gid)
			current_uid = uid
		if current_gid != gid:
			Utils.pprint("GREEN","* setting group of %s to GID %s"%(path,gid))
			_chown(path,current_uid,gid)
			current_gid = gid
		if mode is not None:
			current_mode = stat.S_IMODE(os.stat(path).st_mode)
			if current_mode != mode:
				Utils.pprint("GREEN","* adjusting permissions on %s to mode %o"%(path,mode))
				_chmod(path,mode)
				current_mode = mode
	
	if ctx.is_install > 0:
		ctx.add_post_fun(lambda ctx: f(ctx,path,owner,group,mode))
Build.BuildContext.setownership = _setownership

def _createuser(ctx,user,homedir,shell):
	if Options.platform == 'win32': return
	if Options.platform == 'darwin': return
	if not hasattr(os,"getuid"): return
	if os.getuid() != 0: return
	if Options.options.NOUSERMGMT: return
	
	def f(ctx,user,homedir,shell):
		import pwd
		try:
			pwd.getpwnam(user).pw_uid
			user_exists = True
		except KeyError,e:
			user_exists = False
		if user_exists: return
		
		Utils.pprint("GREEN","* creating user %s"%user)
		cmd = [
		  ctx.env.USERADD,
		  '-M',
		  '-r',
		  '-s',shell,
		  '-d',homedir,
		  user,
		]
		ret = Utils.exec_command(cmd)
		if ret: raise Utils.WafError("Failed to run command %s"%cmd)
	
	def g(ctx,user,homedir,shell):
		import pwd
		try:
			pwd.getpwnam(user).pw_uid
			user_exists = True
		except KeyError,e:
			user_exists = False
		if not user_exists: return
		
		Utils.pprint("GREEN","* removing user %s"%user)
		cmd = [
		  ctx.env.USERDEL,
		  user,
		]
		ret = Utils.exec_command(cmd)
		if ret: raise Utils.WafError("Failed to run command %s"%cmd)
	
	if ctx.is_install > 0:
		ctx.add_pre_fun(lambda ctx: f(ctx,user,homedir,shell))
	elif ctx.is_install < 0:
		ctx.add_pre_fun(lambda ctx: g(ctx,user,homedir,shell))
Build.BuildContext.createuser = _createuser
