package com.dx.gods.service.tools.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.struts2.ServletActionContext;

import com.dx.gods.common.gameserver.GameServer;
import com.dx.gods.common.gameserver.GameServerManager;
import com.dx.gods.common.net.GSRequestAction;
import com.dx.gods.common.utils.GlobalValue;
import com.dx.gods.common.utils.MD5Util;
import com.dx.gods.common.utils.NetUtils;
import com.dx.gods.common.utils.UtilTools;
import com.dx.gods.controller.admin.common.DXAdminController;
import com.dx.gods.service.fileupload.FileUploadController;
import com.dx.gods.service.fileupload.FtpManager;
import com.dx.gods.service.fileupload.ResourceContainer;
import com.dx.gods.service.fileupload.ResourceVersion;
import com.dx.gods.service.tools.FileSVNCache;
import com.dx.gods.service.tools.FileSVNInfo;
import com.dx.gods.service.tools.ProjectVersion;
import com.dx.gods.service.tools.SVNService;
import com.dx.gods.service.tools.SVNWorkCopy;
import com.rw.service.http.response.BaseMsgResponse;
import com.sun.jna.platform.win32.WinBase.SYSTEM_INFO.PI;

public class SVNUploadServerController extends DXAdminController {

	public String handlerUploadSVNFile() {

		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String pId = request.getParameter("pId");
			ProjectVersion projectVersion = SVNService.ProjectMap.get(pId);
			String svnId = projectVersion.getExcelSvnId();
			SVNWorkCopy svn = SVNService.SVNMap.get(svnId);
			String workCopyPath = svn.getWorkCopyPath();
			String UploadPath = workCopyPath + "/"
					+ FileSVNCache.UPLOAD_FOLDER_NAME;
			String ZipPath = workCopyPath + "/"
					+ FileSVNCache.UPLOAD_ZIPFILE_FOLDER_NAME;
			File zipFolder = new File(ZipPath);
			if (!zipFolder.exists()) {
				zipFolder.mkdir();
			}
			StringBuilder sb = new StringBuilder();
			String result = uploadFtp(UploadPath, ZipPath, request,
					ResourceContainer.RESOURCE_TYPE_SERVER,
					GlobalValue.SERVER_FTP_PATH);
			sb.append(result + "</br>");
			List<GameServer> list = GameServerManager.VersionServerMap.get(pId);
			if (list != null) {
				for (GameServer gameServer : list) {
					String strUrl = gameServer.getHttpUrl() + ":"
							+ gameServer.getHttpPort();
					Map<String, Object> params = FileUploadController
							.patchServer(gameServer.getId());

					GSRequestAction requestAction = new GSRequestAction();
					requestAction.pushParams(Map.class, params);
					BaseMsgResponse msg = (BaseMsgResponse) requestAction
							.remoteCall(
									strUrl,
									"com.rw.service.http.requestHandler.ManagerServerHandler",
									"updateResHandler");
					sb.append(msg.getMsg());
				}
			}
			return NetUtils.packMessage(request, sb.toString());
		} catch (Exception ex) {
			return NetUtils.packMessage(request, ex.getMessage());
		}
	}
	
	public String handlerUploadClientSVNFile(){
		HttpServletRequest request = ServletActionContext.getRequest();
		String pId = request.getParameter("pId");
		ProjectVersion projectVersion = SVNService.ProjectMap.get(pId);
		String svnId = projectVersion.getExcelSvnId();
		SVNWorkCopy svn = SVNService.SVNMap.get(svnId);
		String workCopyPath = svn.getWorkCopyPath();
		String temp = FileSVNCache.UPLOAD_CLIENT_FOLDER_NAME.substring(0, FileSVNCache.UPLOAD_CLIENT_FOLDER_NAME.indexOf("/"));
		String UploadPath = workCopyPath + "/" + temp;
		String ZipPath = workCopyPath + "/" + FileSVNCache.UPLOAD_ZIPFILE_FOLDER_NAME;
		File zipFolder = new File(ZipPath);
		if (!zipFolder.exists()) {
			zipFolder.mkdir();
		}
		
		String result = uploadFtp(UploadPath, ZipPath, request, ResourceContainer.RESOURCE_TYPE_CLIENT, GlobalValue.CLIENT_FTP_PATH);
		return NetUtils.packMessage(request, result);
	}
	
	public static String uploadFtp(String UploadPath, String ZipPath, HttpServletRequest request, int resourceType, String ftpPath){
		try {
			String uploadResult = getUploadFileList(UploadPath);
			if(uploadResult == null || uploadResult.equals("")){
				throw new Exception("没有文件提交到ftp服务器");
			}
			String subVersionNo = UtilTools.getDateTimeString("yyyyMMddHHmmss");
			String zipFilePath = ZipPath + "/" + subVersionNo + ".zip";
			UtilTools.zipCompressByAnd(zipFilePath, UploadPath);

			File zipFile = new File(zipFilePath);
			uploadFile(resourceType, ftpPath, request, zipFile, subVersionNo);
			UtilTools.deletefolder(UploadPath);
			zipFile.delete();
			return "如下文件已成功提交ftp:<br/>" + uploadResult;
		} catch (Exception e) {
			return "Exception：" + e.getMessage();
		}
	}

	private static String getUploadFileList(String filePath){
		StringBuilder sb = new StringBuilder();
		List<File> files = UtilTools.getFiles(filePath, "");
		if(files.size() == 0){
			return null;
		}
		for (File file : files) {
			sb.append(file.getName()).append("<br/>");
		}
		return sb.toString();
	}
	
	private static String uploadFile(int type, String path,
			HttpServletRequest request, File upload, String subVersionNo) {
		String message = null;
		if (upload != null) {
			try {
				String uploadFileMD5Value = MD5Util.getFileMD5String(upload);

				String fileName = upload.getName();
				String result = FtpManager.uploadFileToFtp(path, upload.getAbsolutePath(),
						upload.getName());
				
				/**
				 * 临时大版本号为1(大版本号以后页面输入) fileName为小版本号
				 */
				ResourceVersion resVer = new ResourceVersion("1", subVersionNo,
						upload.length(), uploadFileMD5Value, fileName,
						type);

				updateResourceVersion(resVer, type);
				if (result != null) {
					message = result;
				} else {
					message = "上传成功!!!";
				}
			} catch (Exception e) {
				// TODO: handle exception
				message = "上传文件出现异常：" + e.getMessage();
				e.printStackTrace();

			}
		}
		return message;
	}
	
	private static void updateResourceVersion(ResourceVersion rv, int type) {
		if (type == ResourceContainer.RESOURCE_TYPE_CLIENT) {
			ResourceVersion currentResVersion = ResourceContainer
					.getCurrentResVersion(type);
			rv.setLastMainVersionNo(currentResVersion == null ? "-1" : currentResVersion.getMainVersionNo());
			rv.setLastSubVersionNo(currentResVersion == null ? "-1" : currentResVersion.getSubVersionNo());
		}
		ResourceContainer.setCurrentResVersion(type, rv);
	}
}
