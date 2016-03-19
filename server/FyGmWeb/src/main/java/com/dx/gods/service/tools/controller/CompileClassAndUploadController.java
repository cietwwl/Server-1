package com.dx.gods.service.tools.controller;

import io.netty.util.NetUtil;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.dx.gods.common.utils.GlobalValue;
import com.dx.gods.common.utils.NetUtils;
import com.dx.gods.common.utils.SvnUtil;
import com.dx.gods.common.utils.UtilTools;
import com.dx.gods.controller.admin.common.DXAdminController;
import com.dx.gods.service.fileupload.FtpManager;
import com.dx.gods.service.tools.ProjectVersion;
import com.dx.gods.service.tools.SVNService;
import com.dx.gods.service.tools.SVNWorkCopy;
import com.dx.gods.service.tools.UpdateClassInfo;
import com.dx.gods.service.tools.UpdateService;

public class CompileClassAndUploadController extends DXAdminController{
	
	private String svnName;
	
	public String processCompileAndUpload() {

		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			ProjectVersion projectVersion = SVNService.ProjectMap
					.get(svnName);

			StringBuilder sbResult = new StringBuilder();
			// 编译FSApp
			String compileFSAppResult = compileFSApp(projectVersion);
			sbResult.append(compileFSAppResult);
			// 编译FSUtil
			String compileFSUtilResult = compileFSUtil(projectVersion);
			sbResult.append(compileFSUtilResult);

			// 暂时固定选择文件更新
			// 移动文件 并压缩
			copyAndZipFile(projectVersion, request);
			sbResult.append("upload ftp success...");
			return NetUtils.packMessage(request, sbResult.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			return NetUtils.packMessage(request, ex.getMessage());
		}
	}
	
	/**
	 * 编译FSApp
	 */
	private String compileFSApp(ProjectVersion projectVersion)throws Exception{
		try{
			String class_svnId = projectVersion.getClass_svnId();
			SVNWorkCopy svn = SVNService.SVNMap.get(class_svnId);
		
		
			// 更新本地文件
			SVNClientManager svnManager = SvnUtil.readyReadSVN(svn.getWorkCopyPath(), svn);
			File svnFile = new File(svn.getWorkCopyPath());
			SvnUtil.updateSVN(svnFile.getPath());
//			SvnUtil.update(svnManager, svnFile, SVNRevision.HEAD, SVNDepth.INFINITY);
			// 编译文件
			String result = compileClass(svn.getWorkCopyPath() + "/" + GlobalValue.getCOMPILE_CLASS_BAT_NAME());
			return result;
		}catch(Exception ex){
			throw new Exception("编译FSApp异常 :"+ex.getMessage());
		}
	}
	
	/**
	 * 编译FSUtil
	 */
	private String compileFSUtil(ProjectVersion projectVersion)throws Exception{
		try{
			String util_svnId = projectVersion.getUtil_svnId();
			SVNWorkCopy svn = SVNService.SVNMap.get(util_svnId);
		
		
			// 更新本地文件
			SVNClientManager svnManager = SvnUtil.readyReadSVN(svn.getWorkCopyPath(), svn);
			File svnFile = new File(svn.getWorkCopyPath());
			SvnUtil.updateSVN(svnFile.getPath());
			// 编译文件
			String result = compileClass(svn.getWorkCopyPath() + "/" + GlobalValue.getCOMPILE_CLASS_BAT_NAME());
			return result;
		}catch(Exception ex){
			throw new Exception("编译FSUtil异常 :"+ex.getMessage());
		}
	}
	
	private String compileClass(String path)throws Exception{
		String scriptOriginalPath = GlobalValue.getCOMPILE_CLASS_BAT_PATH();
		String result = UtilTools.runScript(path, scriptOriginalPath);
		return result;
	}
	
	private void copyAndZipFile(ProjectVersion projectVersion, HttpServletRequest request)throws Exception{
		String zipFilePath="";
		try {
			String class_svnId = projectVersion.getClass_svnId();
			String util_svnId = projectVersion.getUtil_svnId();
			SVNWorkCopy classSVN = SVNService.SVNMap.get(class_svnId);
			SVNWorkCopy utilSVN = SVNService.SVNMap.get(util_svnId);
			UpdateClassInfo updateClassInfo = UpdateService.getInstance().getUpdateClassInfo();

			String classWorkCopyPath = classSVN.getWorkCopyPath();
			String utilWorkCopyPath = utilSVN.getWorkCopyPath();
			File root = new File(GlobalValue.UPLOAD_CLASS_SAVE_PATH);
			if(!root.exists()){
				root.mkdir();
			}
			UtilTools.clearFolder(GlobalValue.UPLOAD_CLASS_SAVE_PATH);

			File fileClass = new File(GlobalValue.UPLOAD_CLASS_SAVE_PATH + "/classes");
			if (!fileClass.mkdir()) {
				throw new Exception("copyAndZipFile mkdir classes folder fail!");
			}
			File fileLib = new File(GlobalValue.UPLOAD_CLASS_SAVE_PATH + "/lib");
			if (!fileLib.mkdir()) {
				throw new Exception("copyAndZipFile mkdir lib folder fail!");
			}

			List<String> classPath = updateClassInfo.getClassPath();
			for (String value : classPath) {
				File fValue = new File(classWorkCopyPath + value);
				String temp = value.substring(value.lastIndexOf("/"));
				if (fValue.isDirectory()) {
					UtilTools.copyFolder(fileClass.getAbsolutePath() + temp, classWorkCopyPath + value, classWorkCopyPath + value);
				} else {
					UtilTools.copyFile(classWorkCopyPath + value, fileClass.getAbsolutePath() + temp);
				}
			}
			List<String> libPath = updateClassInfo.getLibPath();
			for (String value : libPath) {
				File fValue = new File(utilWorkCopyPath + value);
				String temp = value.substring(value.lastIndexOf("/"));
				if (fValue.isDirectory()) {
					UtilTools.copyFolder(fileLib.getAbsolutePath() + temp,  utilWorkCopyPath + value,  utilWorkCopyPath + value);
				} else {
					UtilTools.copyFile(utilWorkCopyPath + value, fileLib.getAbsolutePath() + temp);
				}
				
			}
			String fileName = UtilTools.getDateTimeString("yyyyMMddHHmmss")+".zip";
			zipFilePath = request.getRealPath("") + "/" + fileName;
			UtilTools.zipCompressByAnd(zipFilePath, GlobalValue.UPLOAD_CLASS_SAVE_PATH);
			
			FtpManager.uploadFileToFtp(GlobalValue.CLASSES_FTP_PATH, zipFilePath, fileName);
			
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}finally{
			if(!zipFilePath.equals("")){
				File temp = new File(zipFilePath);
				temp.delete();
			}
		}
	}

	public String getSvnName() {
		return svnName;
	}

	public void setSvnName(String svnName) {
		this.svnName = svnName;
	}
}
