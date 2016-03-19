package com.dx.gods.service.tools.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.struts2.ServletActionContext;
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
import com.dx.gods.service.tools.FileSVNInfo;
import com.dx.gods.service.tools.ProjectVersion;
import com.dx.gods.service.tools.SVNService;
import com.dx.gods.service.tools.SVNWorkCopy;

public class SVNController extends DXAdminController {

	private List<SVNWorkCopy> svnList;
	private File file;
	private String path;
	private String svnId;
	private List<FileSVNInfo> excellist;
	private final static String fileName = "temp.xls";

	public String handlerCommitJsonFile() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String realPath = ServletActionContext.getRequest()
				.getRealPath("");
		String localPath = realPath + "/WEB-INF/tools/";
		String message = "";
		try {
			
			if (file == null) {
				message = "请选择要上传的文件！";
			} else {

				String userName = UtilTools.getCurrentUserName();
				// 保存本地文件
				FileInputStream in = new FileInputStream(file);
				File tempFile = new File(localPath + fileName);
				FileOutputStream out = new FileOutputStream(tempFile);
				IOUtils.copy(in, out);
				in.close();
				out.close();

				// 调用生成工具执行生成json
				String scriptPath = (localPath + "convert.py").replace("\\",
						"/");
				String targetFilePath = (localPath + fileName).replace("\\",
						"/");
				String script = "python " + "\"" + scriptPath + "\" \""
						+ targetFilePath + "\"";

				Process process = Runtime.getRuntime().exec(script);
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(process.getInputStream()));
				String str;
				while ((str = bufferedReader.readLine()) != null) {
					System.out.println(str);
				}
				process.waitFor();

				// 更新一下work copy
				SVNWorkCopy svn = SVNService.SVNMap.get(svnId);
				String workCopyPath = svn.getWorkCopyPath();
				SVNURL svnurl = SvnUtil.parseSVNURL(svn.getSvnPath());
				SVNClientManager svnManager = SvnUtil.readyReadSVN(
						workCopyPath, svn);

				// 拷贝文件到指定work copy文件夹
				List<File> files = UtilTools.getFiles(localPath, ".json");
				List<File> updateWorkCopyFile = new ArrayList<File>();
				HashMap<File, File> FileMatch = new HashMap<File, File>();
				for(File file : files){
					List<File> templist = UtilTools.getFiles(
							svn.getWorkCopyPath(), file.getName());
					for (File temp : templist) {
						if(temp.getName().equals(file.getName())){
							FileMatch.put(file, temp);
							break;
						}
					}
					if(templist.size() <=0){
						throw new Exception("提交了svn服务器没有的文件，请检查");
					}
					updateWorkCopyFile.addAll(templist);
				}
				File[] aryUpdateWorkCopyFile = new File[updateWorkCopyFile.size()];
				updateWorkCopyFile.toArray(aryUpdateWorkCopyFile);
				SvnUtil.update(svnManager, aryUpdateWorkCopyFile,
						SVNRevision.HEAD, SVNDepth.INFINITY);
				
				List<File> commitFileList = new ArrayList<File>();
				for (File file : files) {
					File copyFile = UtilTools.copyFile(file
							.getAbsolutePath(), FileMatch.get(file)
							.getAbsolutePath());
					String path = svn.getSvnPath()
							+ copyFile.getAbsolutePath()
									.replace(svn.getWorkCopyPath(), "")
									.replace("\\", "/");
					svnurl = SVNURL.parseURIEncoded(path);
					
					// 提交文件到svn服务器
					if (SvnUtil.checkIsNewDirectoryOrFile(svnManager,
							copyFile)) {

						SvnUtil.importDirectory(svnManager, copyFile,
								svnurl, userName, true);
					}else{
						commitFileList.add(copyFile);
					}
				}
				File[] aryCommitFile = new File[commitFileList.size()];
				commitFileList.toArray(aryCommitFile);
				SvnUtil.commit(svnManager, aryCommitFile, true, userName);
				
				message = "上传成功";
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			message = ex.getMessage();
		} finally{
			// 删除临时文件
			UtilTools.deletefolder(localPath + "gen");
			UtilTools.deletefolder(localPath + fileName);
		}

		return NetUtils.packMessage(request, message);
	}

	
	/**
	 * 更新svn文件
	 * @return
	 */
	public String handlerUpdateSVNFile(){
		
		HttpServletRequest request = ServletActionContext.getRequest();
		String projectId = request.getParameter("projectId");
		ProjectVersion projectVersion = SVNService.ProjectMap.get(projectId);
		svnId = projectVersion.getExcelSvnId();
		SVNWorkCopy svn = SVNService.SVNMap.get(projectVersion.getExcelSvnId());
		String workCopyPath = svn.getWorkCopyPath();
		SVNURL svnurl = SvnUtil.parseSVNURL(svn.getSvnPath());
		SVNClientManager svnManager = SvnUtil.readyReadSVN(workCopyPath, svn);
		try {
			ConcurrentHashMap<String, FileSVNInfo> fileMap = new ConcurrentHashMap<String, FileSVNInfo>();
			if (!FileSVNCache.FILE_SVN_CACHE.containsKey(svnId)) {
				boolean blnInitFileSvnXml = FileSVNCache
						.readFileSVNInfo(workCopyPath,svnId);
				
				if (!blnInitFileSvnXml) {
					FileSVNCache.getFileSVNInfo(svnManager, workCopyPath,
							fileMap, new String[] { ".xlsx", ".xls" }, 1);
					FileSVNCache.FILE_SVN_CACHE.put(svnId, fileMap);
					FileSVNCache.saveFileSVNInfo(workCopyPath, svnId);
				}else{
					fileMap = FileSVNCache.FILE_SVN_CACHE.get(svnId);
				}
			} else {
				fileMap = FileSVNCache.FILE_SVN_CACHE.get(svnId);
			}
			
			//update svn服务器文件 to work copy
			File fworkCopy = new File(svn.getWorkCopyPath());
			SvnUtil.update(svnManager, fworkCopy, SVNRevision.HEAD, SVNDepth.INFINITY);
//			SvnUtil.updateSVN(fworkCopy.getAbsolutePath());
			//比较原来的svn version 文件 更新状态如有修改并保存
			ConcurrentHashMap<String, FileSVNInfo> newFileMap = new ConcurrentHashMap<String, FileSVNInfo>();
			FileSVNCache.getFileSVNInfo(svnManager, workCopyPath,
					newFileMap, new String[] { ".xlsx", ".xls" }, 1);
			ConcurrentHashMap<String, FileSVNInfo> needGenerateMap = new ConcurrentHashMap<String, FileSVNInfo>();
			ConcurrentHashMap<String, FileSVNInfo> updateMap = new ConcurrentHashMap<String, FileSVNInfo>();
			boolean blnNeedSave = false;
			for (Iterator<Entry<String, FileSVNInfo>> iterator = newFileMap.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, FileSVNInfo> next = iterator.next();
				FileSVNInfo svnInfo = next.getValue();
				if(fileMap.containsKey(svnInfo.getPath())){
					FileSVNInfo fileSVNInfo = fileMap.get(svnInfo.getPath());
					if (svnInfo.getSvnVersion() > fileSVNInfo.getSvnVersion()) {
						
						fileSVNInfo.setSvnVersion(svnInfo.getSvnVersion());
						fileSVNInfo.setBlnGenerate(false);
						needGenerateMap.put(fileSVNInfo.getPath(), fileSVNInfo);
						updateMap.put(fileSVNInfo.getPath(), fileSVNInfo);
						blnNeedSave = true;
					} else {
						if (!fileSVNInfo.isBlnGenerate()) {
							needGenerateMap.put(fileSVNInfo.getPath(),
									fileSVNInfo);
							blnNeedSave = true;
						}
					}
				}else{
					fileMap.put(svnInfo.getPath(), svnInfo);
					needGenerateMap.put(svnInfo.getPath(),
							svnInfo);
					updateMap.put(svnInfo.getPath(), svnInfo);
				}
			}
			if(blnNeedSave){
				FileSVNCache.saveFileSVNInfo(workCopyPath, svnId);
			}
			FileSVNCache.UN_GENERATE_MAP.put(svnId, needGenerateMap);
			if(excellist == null){
				excellist = new ArrayList<FileSVNInfo>();
			}
			excellist.clear();
			excellist.addAll(updateMap.values());
			return SUCCESS;
		} catch (Exception ex) {
			ex.printStackTrace();
			GMLogger.error("更新失败" + ex.getMessage());
			return NetUtils.packMessage(request, "更新失败" + ex.getMessage());
		}
	
	}

	public String listSVNList() {
		Collection<SVNWorkCopy> values = SVNService.SVNMap.values();
		svnList = new ArrayList<SVNWorkCopy>(values);
		return SUCCESS;
	}

	public void newMessage() {

	}

	public List<SVNWorkCopy> getSvnList() {
		return svnList;
	}

	public void setSvnList(List<SVNWorkCopy> svnList) {
		this.svnList = svnList;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}


	public String getSvnId() {
		return svnId;
	}


	public void setSvnId(String svnId) {
		this.svnId = svnId;
	}


	public List<FileSVNInfo> getExcellist() {
		return excellist;
	}


	public void setExcellist(List<FileSVNInfo> excellist) {
		this.excellist = excellist;
	}


}
