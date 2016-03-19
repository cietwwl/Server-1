package com.dx.gods.service.tools.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import antlr.Utils;

import com.dx.gods.common.log.GMLogger;
import com.dx.gods.common.utils.GlobalValue;
import com.dx.gods.common.utils.NetUtils;
import com.dx.gods.common.utils.UtilTools;
import com.dx.gods.controller.admin.common.DXAdminController;
import com.dx.gods.service.tools.FileSVNCache;
import com.dx.gods.service.tools.FileSVNInfo;
import com.dx.gods.service.tools.ProjectVersion;
import com.dx.gods.service.tools.SVNService;
import com.dx.gods.service.tools.SVNWorkCopy;

/**
 * Excel生成json控制器
 * @author lida
 *
 */
public class GenerateJsonController extends DXAdminController {
	private List<FileSVNInfo> genList;
	
	private String[] belongclient;
	private String[] belongserver;
	
	/**
	 * 生成单个excel to json
	 * @return
	 */
	public String handlerGenerateSingleExcel(){
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String pId = request.getParameter("pId2");
			String path = request.getParameter("path");
			ProjectVersion projectVersion = SVNService.ProjectMap.get(pId);
			String svnId = projectVersion.getExcelSvnId();
			ConcurrentHashMap<String, FileSVNInfo> concurrentHashMap = FileSVNCache.FILE_SVN_CACHE
					.get(svnId);
			FileSVNInfo fileSVNInfo = concurrentHashMap.get(path);
			List<FileSVNInfo> list = new ArrayList<FileSVNInfo>();
			list.add(fileSVNInfo);

			return generate(request, list, svnId);
		} catch (Exception ex) {
			return NetUtils.packMessage(request, ex.getMessage());
		}
		
	}
	
	/**
	 * 批处理多个 excel to json
	 * @return
	 */
	public String handlerGenerateExcels() {
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String pId = request.getParameter("pId1");
			ProjectVersion projectVersion = SVNService.ProjectMap.get(pId);
			String svnId = projectVersion.getExcelSvnId();
			genList = FileSVNCache.getNeedGenerateList(svnId);
			return generate(request, genList, svnId);
		} catch (Exception ex) {
			return NetUtils.packMessage(request, ex.getMessage());
		}
	}
	
	
	
	public static String generate(HttpServletRequest request, List<FileSVNInfo> list, String svnId)throws Exception {
		SVNWorkCopy svnWorkCopy = SVNService.SVNMap.get(svnId);
		String targetPath = svnWorkCopy.getWorkCopyPath() + "/"
				+ FileSVNCache.CACHE_FOLDER_NAME + "/";
		try {
			File cache = new File(targetPath);
			if (!cache.exists()) {
				cache.mkdir();
			}
			copyToWorkCopy(list, targetPath);
			String result = runGenScript(targetPath);

			// 更新blngenerate
			updateGenerateStatus(svnId, list);

			return NetUtils.packMessage(request, result);
		} catch (Exception ex) {
			GMLogger.error(ex.getMessage());
			throw new Exception(ex.getMessage());
		} finally {
			UtilTools.deleteFile(targetPath, new String[] { ".xlsx", ".xls" });
		}
	}
	
	private static void updateGenerateStatus(String svnId, List<FileSVNInfo> list){
		for (FileSVNInfo fileSVNInfo : list) {
			fileSVNInfo.setBlnGenerate(true);
			ConcurrentHashMap<String,FileSVNInfo> map = FileSVNCache.UN_GENERATE_MAP.get(svnId);
			map.remove(fileSVNInfo.getPath());
		}
		SVNWorkCopy svnWorkCopy = SVNService.SVNMap.get(svnId);
		FileSVNCache.saveFileSVNInfo(svnWorkCopy.getWorkCopyPath(), svnId);
	}
	
	private static void copyToWorkCopy(List<FileSVNInfo> list, String targetPath){
		
		for (FileSVNInfo info : list) {
			String targetFilePath = targetPath + info.getName();
			UtilTools.copyFile(info.getPath(), targetFilePath);
		}
	}
	
	private static String runGenScript(String targetPath) throws Exception{
		checkHaveScript(targetPath);
		
		//执行脚本
		String scriptPath = targetPath + GlobalValue.CONVERT_PY_RUN_TOOL_NAME;
		scriptPath = scriptPath.replace("\\", "/");
		File file = new File(scriptPath);
		Process p = Runtime.getRuntime().exec(file.getAbsolutePath());
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF8"));
		StringBuilder sb = new StringBuilder();
		String line;
		while((line = br.readLine()) !=null){
			sb.append(line).append("<br/>");
			System.out.println(line);
		}
		br.close();
		
		return sb.toString();
	}
	
	
	/**
	 * 检查脚本
	 * @param targetPath
	 */
	private static void checkHaveScript(String targetPath){
		if(UtilTools.getFiles(targetPath, GlobalValue.CONVERT_PY_TOOL_NAME).size()<=0){
			UtilTools.copyFile(GlobalValue.CONVERT_PY_PATH, targetPath + GlobalValue.CONVERT_PY_TOOL_NAME);
		}
		if(UtilTools.getFiles(targetPath, GlobalValue.CONVERT_PY_RUN_TOOL_NAME).size() <= 0){
			UtilTools.copyFile(GlobalValue.CONVERT_PY_RUN_PATH, targetPath + GlobalValue.CONVERT_PY_RUN_TOOL_NAME);
		}
	}
	
	public String getNeedGenerateList(){
		HttpServletRequest request = ServletActionContext.getRequest();
		String projectId = request.getParameter("projectId");
		ProjectVersion projectVersion = SVNService.ProjectMap.get(projectId);
		String svnId = projectVersion.getExcelSvnId();
		genList = FileSVNCache.getNeedGenerateList(svnId);
		if (genList == null) {
			return NetUtils.packMessage(request, "没有需要生成Excel，请更新对应的版本信息");
		} else {
			request.setAttribute("projectId", projectId);
			return SUCCESS;
		}
	}

	public List<FileSVNInfo> getGenList() {	
		return genList;
	}

	public void setGenList(List<FileSVNInfo> genList) {
		this.genList = genList;
	}

	public String[] getBelongclient() {
		return belongclient;
	}

	public void setBelongclient(String[] belongclient) {
		this.belongclient = belongclient;
	}

	public String[] getBelongserver() {
		return belongserver;
	}

	public void setBelongserver(String[] belongserver) {
		this.belongserver = belongserver;
	}
}
