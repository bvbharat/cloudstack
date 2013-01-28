// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package com.cloud.storage.upload;

import com.cloud.async.AsyncJobManager;
import com.cloud.host.HostVO;
import com.cloud.storage.Upload.Mode;
import com.cloud.storage.Upload.Status;
import com.cloud.storage.Upload.Type;
import com.cloud.storage.UploadVO;
import com.cloud.storage.VMTemplateHostVO;
import com.cloud.storage.VMTemplateVO;
import com.cloud.storage.VolumeVO;
import com.cloud.utils.component.Manager;

/**
 * Monitor upload progress of all entities.
 *
 */
public interface UploadMonitor extends Manager{		
	
	public void cancelAllUploads(Long templateId);

	public Long extractTemplate(VMTemplateVO template, String url,
			VMTemplateHostVO tmpltHostRef,Long dataCenterId, long eventId, long asyncJobId, AsyncJobManager asyncMgr);

    boolean isTypeUploadInProgress(Long typeId, Type type);

    void handleUploadSync(long sserverId);

    UploadVO createNewUploadEntry(Long hostId, Long typeId, Status uploadState,
            Type type, String errorString, Mode extractMode);

    void extractVolume(UploadVO uploadVolumeObj, HostVO sserver, VolumeVO volume, String url,
            Long dataCenterId, String installPath, long eventId,
            long asyncJobId, AsyncJobManager asyncMgr);

    UploadVO createEntityDownloadURL(VMTemplateVO template,
            VMTemplateHostVO vmTemplateHost, Long dataCenterId, long eventId);

    void createVolumeDownloadURL(Long entityId, String path, Type type,
            Long dataCenterId, Long uploadId);

}