package com.dx.gods.service.sycnres;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.springframework.aop.ThrowsAdvice;

import com.dx.gods.common.log.GMLogger;
import com.dx.gods.common.utils.GlobalValue;
import com.dx.gods.common.utils.NetUtils;
import com.dx.gods.common.utils.UtilTools;
import com.dx.gods.controller.admin.common.DXAdminController;
import com.dx.gods.service.fileupload.FtpManager;
import com.dx.gods.service.fileupload.ResourceContainer;
import com.dx.gods.service.fileupload.ResourceVersion;

public class SycnresController extends DXAdminController {

	/**
	 * 处理资源同步
	 * @return
	 */
	public String handleSyncRes() {
		String message = "";

		HttpServletRequest request = ServletActionContext.getRequest();

		String remoteFilePath = GlobalValue.VERSION_FILE_FTP_PATH + "/"
				+ GlobalValue.VERSION_CONFIG_FILE_NAME;
		String localFilePath = request.getRealPath("") + "/"
				+ GlobalValue.VERSION_CONFIG_FILE_NAME;
		
		if(GlobalValue.GM_TYPE == GlobalValue.GM_TYPE_OUT){
			remoteFilePath = FtpManager.getLatestUpdateRescourceConfigFile(GlobalValue.VERSION_FILE_FTP_PATH);
		}else if(GlobalValue.GM_TYPE == GlobalValue.GM_TYPE_INTERNET){
			remoteFilePath = GlobalValue.VERSION_CONFIG_FILE_NAME;
		}
		
		if(remoteFilePath == null){
			return NetUtils.packMessage(request, "获取版本文件失败");
		}
		// 下载内网ftp的版本信息
		GMLogger.error(GlobalValue.VERSION_FILE_FTP_PATH + "/" + remoteFilePath);
		message = FtpManager.downloadFileInFtp(GlobalValue.VERSION_FILE_FTP_PATH+"/"+remoteFilePath, localFilePath, GlobalValue.VERSION_CONFIG_FILE_NAME);
		
		if (message != null) {
			return NetUtils.packMessage(request, message);
		} else {
			// 加载 ftp 下载配置表
			try {
				List<ResourceVersion> readResourceFile = ResourceContainer.readResourceFile(localFilePath);
				GMLogger.error("readResourceFile size:" + readResourceFile.size());
				UtilTools.deletefolder(localFilePath);
				for (ResourceVersion ver : readResourceFile) {
					if (ResourceContainer.ClientResMap.containsKey(ver
							.getMainVersionNo())) {
						Map<String, ResourceVersion> map = ResourceContainer.ClientResMap
								.get(ver.getMainVersionNo());
						if (map.containsKey(ver.getSubVersionNo())) {
							continue;
						} else {
							syncRes(ver, request, remoteFilePath, localFilePath);
						}
					} else {
						ResourceVersion serverVersion = ResourceContainer.CurrentResMap
								.get(ResourceContainer.RESOURCE_TYPE_SERVER);
						if (serverVersion != null
								&& serverVersion.getMainVersionNo().equals(
										ver.getMainVersionNo())
								&& serverVersion.getSubVersionNo().equals(
										ver.getSubVersionNo())) {
							continue;
						} else {
							syncRes(ver, request, remoteFilePath, localFilePath);
						}
					}
				}
				return NetUtils.packMessage(request, "同步成功");
			} catch (Exception ex) {
				return NetUtils.packMessage(request, "同步异常:" + ex.getMessage());
			}
		}
	}
	
	private void syncRes(ResourceVersion ver, HttpServletRequest request, String remoteFilePath, String localFilePath) throws Exception{
		if(GlobalValue.GM_TYPE == GlobalValue.GM_TYPE_OUT){
			processSyncRes(ver, request, remoteFilePath, localFilePath);
		}
		if(GlobalValue.GM_TYPE == GlobalValue.GM_TYPE_INTERNET){
			GMLogger.error("sync new resource info");
			processSyncResInternet(ver);
		}
	}

	private void processSyncRes(ResourceVersion ver,
			HttpServletRequest request, String remoteFilePath,
			String localFilePath)throws Exception {
		

		String localFilePath1 = request.getRealPath("") + "//"
				+ ver.getFileName();
		String remoteFilePath1 = "";
		if (ver.getResourceType() == ResourceContainer.RESOURCE_TYPE_SERVER) {
			remoteFilePath = GlobalValue.SERVER_FTP_PATH + "\\"
					+ ver.getFileName();
			remoteFilePath1 = GlobalValue.SERVER_FTP_PATH;
		} else {
			remoteFilePath = GlobalValue.CLIENT_FTP_PATH + "\\"
					+ ver.getFileName();
			remoteFilePath1 = GlobalValue.CLIENT_FTP_PATH;
		}
		
		// 移动资源
		String downResult1 = FtpManager.downloadFileInFtp(remoteFilePath,
				localFilePath1, ver.getFileName());

		if (downResult1 != null) {
			throw new Exception(downResult1);
		} else {

			String uploadResult = FtpManager.uploadFileToFtp(remoteFilePath1,
					localFilePath1, ver.getFileName());

			if (uploadResult == null) {
				ResourceContainer.addToCache(ver, false);
			} else {
				throw new Exception(uploadResult);
			}

		}

	}
	
	private void processSyncResInternet(ResourceVersion ver) throws Exception {
		ResourceContainer.addToCache(ver, false);
	}
}
