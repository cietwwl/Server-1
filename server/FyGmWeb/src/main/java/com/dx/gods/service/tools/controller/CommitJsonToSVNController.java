package com.dx.gods.service.tools.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.dx.gods.common.log.GMLogger;
import com.dx.gods.common.utils.NetUtils;
import com.dx.gods.common.utils.SvnUtil;
import com.dx.gods.common.utils.UtilTools;
import com.dx.gods.controller.admin.common.DXAdminController;
import com.dx.gods.service.tools.FileSVNCache;
import com.dx.gods.service.tools.ProjectVersion;
import com.dx.gods.service.tools.SVNService;
import com.dx.gods.service.tools.SVNWorkCopy;

public class CommitJsonToSVNController extends DXAdminController{
	
	
	public String handlerCommitJsonToSVN(){
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectId = request.getParameter("pId");
			String commitMessage = request.getParameter("commitMessage");
			return commitSVN(request, commitMessage, projectId);
		} catch (Exception ex) {
			return NetUtils.packMessage(request, "提交失败:" + ex.getMessage());
		}
	}
	
	public static String commitSVN(HttpServletRequest request,
			String commitMessage, String projectId)throws Exception {
		List<File> clist = null;
		try {
			if (commitMessage == null || commitMessage.equals("")) {
				throw new Exception("请输入提交注释");
			}
			ProjectVersion projectVersion = SVNService.ProjectMap.get(projectId);
			String svnId = projectVersion.getExcelSvnId();

			// 找到生成的文件夹，区分客户端和服务器
			SVNWorkCopy svn = SVNService.SVNMap.get(svnId);
			String workCopyPath = svn.getWorkCopyPath();
			String CachePath = workCopyPath + "/" + FileSVNCache.CACHE_FOLDER_NAME;
			SVNClientManager svnManager = SvnUtil.readyReadSVN(workCopyPath, svn);
			clist = UtilTools.getFolders(CachePath, "gen");

			if (clist.size() <= 0) {
				throw new Exception("没有文件提交");
			}
			StringBuilder resultSB = new StringBuilder();
			// 更新客户端svn文件
			if (clist.size() > 0) {
				File file = clist.get(0);
				List<File> cJsonFiles = UtilTools.getFiles(file.getAbsolutePath(), ".json");

				if (cJsonFiles.size() > 0) {
					String clientSvnId = projectVersion.getClientSvnId();
					SVNWorkCopy cSvn = SVNService.SVNMap.get(clientSvnId);
					File[] commitSVN = commitSVN(cJsonFiles, cSvn, commitMessage);
					resultSB.append(getFilePath(commitSVN));
					copyFileToUploadClientFolder(commitSVN, svn, cSvn);
					String serverSvnId = projectVersion.getServerSvnId();
					SVNWorkCopy sSvn = SVNService.SVNMap.get(serverSvnId);
					File[] commitServerSVN = commitSVN(cJsonFiles, sSvn, commitMessage);
					resultSB.append(getFilePath(commitServerSVN));
					copyFileToUploadFolder(commitServerSVN, svn, sSvn);
				}

				List<File> cCSVFiles = UtilTools.getFiles(file.getAbsolutePath(), ".csv");

				if (cCSVFiles.size() > 0) {
					String clientSvnId = projectVersion.getClientSvnId();
					SVNWorkCopy cSvn = SVNService.SVNMap.get(clientSvnId);
					File[] commitSVN = commitSVN(cCSVFiles, cSvn, commitMessage);
					resultSB.append(getFilePath(commitSVN));
					copyFileToUploadClientFolder(commitSVN, svn, cSvn);
					String serverSvnId = projectVersion.getServerSvnId();
					SVNWorkCopy sSvn = SVNService.SVNMap.get(serverSvnId);
					File[] commitServerSVN = commitSVN(cCSVFiles, sSvn, commitMessage);
					resultSB.append(getFilePath(commitServerSVN));
					copyFileToUploadFolder(commitServerSVN, svn, sSvn);
				}
			}

			return NetUtils.packMessage(request,
					"提交成功<br/>" + resultSB.toString());
		} catch (Exception ex) {
			throw new Exception("提交失败:" + ex.getMessage());
		} finally {
			if (clist != null) {
				for (File file : clist) {
					UtilTools.deletefolder(file.getAbsolutePath());
				}
			}
		}
	}
	
	private static String getFilePath(File[] files){
		if(files == null){
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (File file : files) {
			
			sb.append(file.getName()+":"+file.getAbsolutePath()).append("<br/>");
		}
		return sb.toString();
	}
	
	private static void copyFileToUploadFolder(File[] commitServerSVN, SVNWorkCopy excelSvn, SVNWorkCopy serverSvn) {
		
		String workCopyPath = excelSvn.getWorkCopyPath();
		String UploadPath = workCopyPath + "/" + FileSVNCache.UPLOAD_FOLDER_NAME;
		File fUploadPath = new File(UploadPath);
		if(!fUploadPath.exists()){
			
			fUploadPath.mkdir();
		}
		String rootPath = serverSvn.getWorkCopyPath();
		
		String targetFilePath = "";
		for (File tempFile : commitServerSVN) {
			String absolutePath = tempFile.getAbsolutePath();
			absolutePath = absolutePath.replace("\\", "/");
			rootPath = rootPath.replace("\\", "/");
			targetFilePath = UploadPath + absolutePath.replace(rootPath, "");
			UtilTools.copyFile(tempFile.getAbsolutePath(), targetFilePath);
		}
		
	}
	
	private static void copyFileToUploadClientFolder(File[] commitServerSVN, SVNWorkCopy excelSvn, SVNWorkCopy clientSvn){
		String workCopyPath = excelSvn.getWorkCopyPath();
		String UploadPath = workCopyPath + "/" + FileSVNCache.UPLOAD_CLIENT_FOLDER_NAME;
		File fUploadPath = new File(UploadPath);
		if(!fUploadPath.exists()){
			UtilTools.createFolder(UploadPath);
		}
		String rootPath = clientSvn.getWorkCopyPath();
		
		String targetFilePath = "";
		for (File tempFile : commitServerSVN) {
			String absolutePath = tempFile.getAbsolutePath();
			absolutePath = absolutePath.replace("\\", "/");
			rootPath = rootPath.replace("\\", "/");
			targetFilePath = UploadPath + absolutePath.replace(rootPath, "");
			UtilTools.copyFile(tempFile.getAbsolutePath(), targetFilePath);
		}
	}
	
	/**
	 * 上传SVN
	 * @param files
	 * @param svn
	 * @throws Exception
	 */
	private static File[] commitSVN(List<File> files, SVNWorkCopy svn,
			String commitMessage) throws Exception {
		try {
			String userName = UtilTools.getCurrentUserName();
			String workCopyPath = svn.getWorkCopyPath();
			SVNClientManager svnManager = SvnUtil.readyReadSVN(workCopyPath,
					svn);

			List<File> updateWorkCopyFile = new ArrayList<File>();
			HashMap<File, File> FileMatch = new HashMap<File, File>();
			for (File file : files) {
				List<File> templist = UtilTools.getFiles(svn.getWorkCopyPath(), file.getName());
				for (File temp : templist) {
					if (temp.getName().equals(file.getName())) {
						FileMatch.put(file, temp);
						break;
					}
				}
				if (templist.size() <= 0) {
					continue;
				}
				updateWorkCopyFile.addAll(templist);
			}

			File[] aryUpdateWorkCopyFile = new File[updateWorkCopyFile.size()];
			updateWorkCopyFile.toArray(aryUpdateWorkCopyFile);
			SvnUtil.update(svnManager, aryUpdateWorkCopyFile, SVNRevision.HEAD, SVNDepth.INFINITY);

			List<File> commitFileList = new ArrayList<File>();
			for (File file : files) {
				if (!FileMatch.containsKey(file)) {
					continue;
				}
				File copyFile = UtilTools.copyFile(file.getAbsolutePath(), FileMatch.get(file).getAbsolutePath());
				if (SvnUtil.checkFileIsModify(svnManager, copyFile)) {
					commitFileList.add(copyFile);
				}
			}
			File[] aryCommitFile = new File[commitFileList.size()];
			commitFileList.toArray(aryCommitFile);
			if (aryCommitFile.length > 0) {
				SvnUtil.commit(svnManager, aryCommitFile, true, userName + ":" + commitMessage);
			}
			return aryCommitFile;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("上传svn出现异常:" + ex.getMessage());
		}
	}
}
