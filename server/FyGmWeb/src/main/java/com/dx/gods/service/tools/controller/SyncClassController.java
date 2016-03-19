package com.dx.gods.service.tools.controller;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.dx.gods.common.utils.GlobalValue;
import com.dx.gods.common.utils.NetUtils;
import com.dx.gods.controller.admin.common.DXAdminController;
import com.dx.gods.service.fileupload.FtpManager;
import com.dx.gods.service.fileupload.ResourceContainer;

public class SyncClassController extends DXAdminController{

	
	public String handlerSyncClass(){
		String message = "";
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String remoteFileName = FtpManager.getLatestUpdateFile(GlobalValue.CLASSES_FTP_PATH);
			if (remoteFileName == null || remoteFileName.equals("")) {
				throw new Exception("获取不到需要同步的class文件");
			}
			processSyncClass(remoteFileName, request);
			return NetUtils.packMessage(request, "同步成功");
		} catch (Exception ex) {
			return NetUtils.packMessage(request, ex.getMessage());
		}
	}
	
	private void processSyncClass(String fileName, HttpServletRequest request) throws Exception{
		
		String localFilePath = request.getRealPath("") + "/" + fileName;
		String remoteFilePath = GlobalValue.CLASSES_FTP_PATH + "/" + fileName;
		String remoteFilePath1 = GlobalValue.CLASSES_FTP_PATH;
		try {
			String downResult = FtpManager.downloadFileInFtp(remoteFilePath,
					localFilePath, fileName);

			if (downResult != null) {
				throw new Exception(downResult);
			} else {
				String uploadResult = FtpManager.uploadFileToFtp(
						remoteFilePath1, localFilePath, "classes.zip");
				if (uploadResult != null) {
					throw new Exception(uploadResult);
				}
			}
		} finally {
			File tempFile = new File(localFilePath);
			tempFile.delete();
		}
	}
}
