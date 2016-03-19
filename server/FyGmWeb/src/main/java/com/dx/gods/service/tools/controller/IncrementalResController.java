package com.dx.gods.service.tools.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.dx.gods.common.log.GMLogger;
import com.dx.gods.common.utils.GlobalValue;
import com.dx.gods.common.utils.MD5Util;
import com.dx.gods.common.utils.NetUtils;
import com.dx.gods.common.utils.SvnUtil;
import com.dx.gods.common.utils.UtilTools;
import com.dx.gods.service.fileupload.ResourceContainer;
import com.dx.gods.service.tools.ProjectVersion;
import com.dx.gods.service.tools.ResDiffInfo;
import com.dx.gods.service.tools.SVNService;
import com.dx.gods.service.tools.SVNWorkCopy;
import com.mysql.jdbc.Util;

public class IncrementalResController {
	
	public String incrementalResHandler(){
		
		HttpServletRequest request = ServletActionContext.getRequest();
		String pId = request.getParameter("pId");
		ProjectVersion projectVersion = SVNService.ProjectMap.get(pId);
		try{
			copyNewRes(projectVersion);
			compareCurrentRes();
			packPatchAndUpload(request);
			setCurrentRes();
			return NetUtils.packMessage(request, "生成差量上传成功");
		}catch(Exception ex){
			return NetUtils.packMessage(request, ex.getMessage());
		}finally{
			UtilTools.deletefolder(GlobalValue.COMPARE_FOLDER_PATH);
			UtilTools.deletefolder(GlobalValue.COMPARE_PATCH_PATH);
		}
	}
	
	private void copyNewRes(ProjectVersion projectVersion) throws Exception {
		try {
			String client_res_svnId = projectVersion.getClient_res_svnId();
			SVNWorkCopy svn = SVNService.SVNMap.get(client_res_svnId);
			SvnUtil.updateSVN(svn.getWorkCopyPath());
			UtilTools.clearFolder(GlobalValue.COMPARE_FOLDER_PATH);
			UtilTools.copyFolder(GlobalValue.COMPARE_FOLDER_PATH, svn.getWorkCopyPath(), svn.getWorkCopyPath());
		} catch (Exception ex) {
			throw new Exception("移动最新资源出错：" + ex.getMessage());
		}
	}
	
	/**
	 * 差量入口
	 * @throws Exception
	 */
	private void compareCurrentRes()throws Exception{
		try {
			HashMap<String, File> newMap = UtilTools.getFilesMapScreening(
					GlobalValue.COMPARE_FOLDER_PATH, new String[]{"svn"});
			HashMap<String, File> oldMap = UtilTools.getFilesMapScreening(
					GlobalValue.CURRENT_FOLDER_PATH, new String[]{"svn"});
			if(oldMap.size() == 0){
				throw new Exception("can not find current client resource!");
			}
			
			List<ResDiffInfo> diffList = new ArrayList<ResDiffInfo>();

			File patch = new File(GlobalValue.COMPARE_PATCH_PATH);
			if(!patch.exists()){
				UtilTools.createFolder(GlobalValue.COMPARE_PATCH_PATH);
			}else{
				UtilTools.clearFolder(GlobalValue.COMPARE_PATCH_PATH);
			}
			File compareFolder = new File(GlobalValue.COMPARE_FOLDER_PATH);
			for (Iterator<Entry<String, File>> iterator = newMap.entrySet()
					.iterator(); iterator.hasNext();) {
				Entry<String, File> next = iterator.next();
				File newFile = next.getValue();
				String fileName = next.getKey();
				
				String fileRelativePath = newFile.getAbsoluteFile().toString().replace(fileName, "").replace(compareFolder.getAbsolutePath(), "");
				String newMD5 = MD5Util.getFileMD5String(newFile);
				if (oldMap.containsKey(fileName)) {
					File oldFile = oldMap.get(fileName);

					
					String oldMD5 = MD5Util.getFileMD5String(oldFile);
					oldMap.remove(fileName);
					if(newMD5.equals(oldMD5)){
						continue;
					}
					String patchName = getPatchName(fileName);
					diffClientRes(newFile.getAbsolutePath(), oldFile.getAbsolutePath(), GlobalValue.COMPARE_PATCH_PATH+"/"+patchName);
					
					ResDiffInfo resDiffInfo = new ResDiffInfo(fileName, fileRelativePath, patchName, ResDiffInfo.RES_STATUS_UPDATE, newMD5);
					diffList.add(resDiffInfo);
				} else {
					// 新增资源
					//复制新增资源进patch包
					UtilTools.copyFile(newFile.getAbsolutePath(), GlobalValue.COMPARE_PATCH_PATH + "/" + fileRelativePath + "/" + fileName);
					ResDiffInfo resDiffInfo = new ResDiffInfo(fileName, fileRelativePath, "", ResDiffInfo.RES_STATUS_NEW, newMD5);
					diffList.add(resDiffInfo);
				}
			}
			for (Iterator<Entry<String, File>> iterator = oldMap.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, File> next = iterator.next();
				String fileName = next.getKey();
				File file = next.getValue();
				String fileRelativePath = file.getAbsoluteFile().toString().replace(fileName, "").replace(compareFolder.getAbsolutePath(), "");
				ResDiffInfo resDiffInfo = new ResDiffInfo(fileName, fileRelativePath, "", ResDiffInfo.RES_STATUS_DELETE, "");
				diffList.add(resDiffInfo);
			}
			if(diffList.size() == 0){
				throw new Exception("当前资源比较没有差异！");
			}
			genDiffXml(diffList);
			
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}
	
	private String getPatchName(String fileName){
		return fileName.substring(0, fileName.lastIndexOf(".")) + ".patch";
	}
	
	/**
	 * 生成差量patch 
	 * @param newResPath 新文件的路径
	 * @param oldResPaht 旧文件的路径
	 * @param patchPath  
	 * @return
	 */
	public void diffClientRes(String newResPath, String oldResPaht, String patchPath) throws Exception {
		try {
			GMLogger.error("newResPath:"+newResPath);
			File toolPath = new File(GlobalValue.CLIENT_DIFF_TOOL_PATH);
			String script = "\"" + toolPath.getAbsolutePath() + "\" \"" + newResPath + "\" \"" + oldResPaht + "\" \"" + patchPath + "\"";
			GMLogger.error("scrpt:" + script);
			Process p = Runtime.getRuntime().exec(script);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF8"));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("<br/>");
			}
			br.close();
		} catch (Exception ex) {
			throw new Exception("差量计算出现异常:" + ex.getMessage());
		}
	}
	
	/**
	 * 生成差量
	 * @param diffList
	 * @throws Exception
	 */
	private void genDiffXml(List<ResDiffInfo> diffList)throws Exception{
		SAXBuilder sb = new SAXBuilder();
		File file = new File(GlobalValue.COMPARE_PATCH_PATH + "/" + GlobalValue.CLIENT_PATCH_INFO_NAME);
		try{
			if(file.exists()){
				file.delete();
			}
			
			Document doc = new Document();
			Element rootElement = new Element("root");
			
			for (ResDiffInfo resDiffInfo : diffList) {
				Element ePatch = new Element("patch");
				Element eFileName = new Element("fileName");
				Element eFilePath = new Element("filePath");
				Element ePatchName = new Element("patchName");
				Element eStatus = new Element("status");
				Element eMd5Value = new Element("md5Value");
				ePatch.addContent(eFileName);
				ePatch.addContent(eFilePath);
				ePatch.addContent(ePatchName);
				ePatch.addContent(eStatus);
				ePatch.addContent(eMd5Value);
				eFileName.setAttribute("value", resDiffInfo.getFileName());
				eFilePath.setAttribute("value", resDiffInfo.getFilePath());
				ePatchName.setAttribute("value", resDiffInfo.getPatchName());
				eStatus.setAttribute("value", String.valueOf(resDiffInfo.getStatus()));
				eMd5Value.setAttribute("value", resDiffInfo.getMd5Value());
				rootElement.addContent(ePatch);
			}

			doc.setRootElement(rootElement);
			XMLOutputter out = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			out.setFormat(format);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			out.output(doc, fileOutputStream);
			fileOutputStream.close();
		}catch(Exception ex){
			throw new Exception("保存Diff xml出现异常:"+ex.getMessage());
		}
	}
	
	/**
	 * 打包并上传patch
	 * @param request
	 */
	private void packPatchAndUpload(HttpServletRequest request){
		
		SVNUploadServerController.uploadFtp(GlobalValue.COMPARE_PATCH_PATH, request.getRealPath(""), request, ResourceContainer.RESOURCE_TYPE_CLIENT, GlobalValue.CLIENT_FTP_PATH);
	}
	
	/**
	 * 设置当前资源
	 */
	private void setCurrentRes(){
		UtilTools.deletefolder(GlobalValue.CURRENT_FOLDER_PATH);
		File newFile = new File(GlobalValue.COMPARE_FOLDER_PATH);
		newFile.renameTo(new File(GlobalValue.CURRENT_FOLDER_PATH));
	}
}
