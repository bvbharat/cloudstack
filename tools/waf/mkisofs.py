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
import Utils
from TaskGen import feature, before
from Configure import ConfigurationError
import Task
import os

# fixme: this seems to hang waf with 100% CPU

def detect(conf):
	conf.find_program("mkisofs",var='MKISOFS')
	if not conf.env.MKISOFS: conf.find_program("genisoimage",mandatory=True,var='MKISOFS')

def iso_up(task):
	tgt = task.outputs[0].bldpath(task.env)
	if os.path.exists(tgt): os.unlink(tgt)
	inps = []
	for inp in task.inputs:
		if inp.id&3==Node.BUILD:
			src = inp.bldpath(task.env)
			srcname = src
			srcname = sep.join(srcname.split(sep)[1:]) # chop off default/
		else:
			src = inp.srcpath(task.env)
			srcname = src
			srcname = sep.join(srcname.split(sep)[1:]) # chop off ../
                if task.generator.rename: srcname = task.generator.rename(srcname)
                inps.append(srcname+'='+src)
	ret = Utils.exec_command(
		[
			task.generator.env.MKISOFS,
			"-quiet",
			"-r",
                        "-graft-points",
			"-o",tgt,
		] + inps, shell=False)
	if ret != 0: return ret

def apply_iso(self):
	Utils.def_attrs(self,fun=iso_up)
	self.default_install_path=0
	lst=self.to_list(self.source)
	self.meths.remove('apply_core')
	self.dict=getattr(self,'dict',{})
	out = self.path.find_or_declare(self.target)
	ins = []
	for x in Utils.to_list(self.source):
		node = self.path.find_resource(x)
		if not node:raise Utils.WafError('cannot find input file %s for processing'%x)
		ins.append(node)
	if self.dict and not self.env['DICT_HASH']:
		self.env=self.env.copy()
		keys=list(self.dict.keys())
		keys.sort()
		lst=[self.dict[x]for x in keys]
		self.env['DICT_HASH']=str(Utils.h_list(lst))
	tsk=self.create_task('iso',ins,out)
	tsk.fun=self.fun
	tsk.dict=self.dict
	tsk.dep_vars=['DICT_HASH']
	tsk.install_path=self.install_path
	tsk.chmod=self.chmod
	if not tsk.env:
		tsk.debug()
		raise Utils.WafError('task without an environment')

Task.task_type_from_func('iso',func=iso_up)
feature('iso')(apply_iso)
before('apply_core')(apply_iso)
