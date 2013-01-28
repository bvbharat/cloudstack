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
import Options
import tarfile
from TaskGen import feature, before
import Task
import os, sys

# construct a tar file containing them
# for as long as the build node appears later than the source node (this is an implementation detail of waf we are relying on)
def tar_up(task):
	tgt = task.outputs[0].bldpath(task.env)
	if os.path.exists(tgt): os.unlink(tgt)
        if tgt.lower().endswith(".bz2"): z = tarfile.open(tgt,"w:bz2")
        elif tgt.lower().endswith(".gz"): z = tarfile.open(tgt,"w:gz")
        elif tgt.lower().endswith(".tgz"): z = tarfile.open(tgt,"w:gz")
	else: z = tarfile.open(tgt,"w")
	fileset = {}
	for inp in task.inputs:
		src = inp.srcpath(task.env)
		if src.startswith(".."):
			srcname = Utils.relpath(src,os.path.join("..",".")) # file in source dir
		else:
			srcname = Utils.relpath(src,os.path.join(task.env.variant(),".")) # file in artifacts dir
		srcname = srcname.split(os.path.sep,len(task.generator.root.split(os.path.sep)))[-1]
		if task.generator.rename: srcname = task.generator.rename(srcname)
		fileset[srcname] = src
	for srcname,src in fileset.items():
		ti = tarfile.TarInfo(srcname)
		ti.mode = 0755
		ti.size = os.path.getsize(src)
                openmode = 'r'
                if Options.platform == 'win32': openmode = openmode + 'b'
                f = file(src,openmode)
		z.addfile(ti,fileobj=f)
		f.close()
	z.close()
	if task.chmod: os.chmod(tgt,task.chmod)
	return 0

def apply_tar(self):
	Utils.def_attrs(self,fun=tar_up)
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
	tsk=self.create_task('tar',ins,out)
	tsk.fun=self.fun
	tsk.dict=self.dict
	tsk.install_path=self.install_path
	tsk.chmod=self.chmod
	if not tsk.env:
		tsk.debug()
		raise Utils.WafError('task without an environment')

Task.task_type_from_func('tar',func=tar_up)
feature('tar')(apply_tar)
before('apply_core')(apply_tar)
