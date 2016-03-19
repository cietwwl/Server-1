package com.dx.gods.service.tools.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.dx.gods.common.log.GMLogger;
import com.dx.gods.common.utils.NetUtils;
import com.dx.gods.common.utils.SvnUtil;
import com.dx.gods.controller.admin.common.DXAdminController;
import com.dx.gods.service.tools.FileSVNCache;
import com.dx.gods.service.tools.FileSVNInfo;
import com.dx.gods.service.tools.ProjectVersion;
import com.dx.gods.service.tools.SVNService;
import com.dx.gods.service.tools.SVNWorkCopy;

public class AutoController extends DXAdminController{

	private List<FileSVNInfo> excellist;
	
	public String handlerCompileAndUpload(){
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectId = request.getParameter("pId");
			String commitMessage = request.getParameter("commitMessage");

			ProjectVersion projectVersion = SVNService.ProjectMap
					.get(projectId);
			String svnId = projectVersion.getExcelSvnId();
			List<FileSVNInfo> genList = FileSVNCache.getNeedGenerateList(svnId);
			if(genList.size() == 0){
				throw new Exception("没有文件需要提交");
			}
			StringBuilder sb = new StringBuilder();
			GenerateJsonController.generate(request, genList, svnId);
			sb.append(request.getAttribute("mes").toString()).append("<br/>");

			if (genList != null) {
				for (Iterator<FileSVNInfo> iterator = genList.iterator(); iterator
						.hasNext();) {
					FileSVNInfo fileSVNInfo = iterator.next();
					if (fileSVNInfo.getName().indexOf("skillCfg.xlsx") == -1) {
						iterator.remove();
					}
				}
				GenerateCSVController.generate(request, genList, svnId);
				sb.append(request.getAttribute("mes").toString()).append("<br/>");
			}

			CommitJsonToSVNController.commitSVN(request, commitMessage,
					projectId);
			sb.append(request.getAttribute("mes").toString()).append("<br/>");
			return NetUtils.packMessage(request, sb.toString());
		} catch (Exception ex) {
			return NetUtils.packMessage(request, ex.getMessage());
		}
	}
}
