package com.dx.gods.service.tools.controller;

import javassist.bytecode.stackmap.BasicBlock.Catch;

import javax.servlet.http.HttpServletRequest;

import org.antlr.stringtemplate.language.Cat;
import org.apache.struts2.ServletActionContext;

import com.dx.gods.common.utils.NetUtils;
import com.dx.gods.common.utils.UtilTools;
import com.dx.gods.controller.admin.common.DXAdminController;
import com.dx.gods.service.tools.FileSVNCache;
import com.dx.gods.service.tools.ProjectVersion;
import com.dx.gods.service.tools.SVNService;
import com.dx.gods.service.tools.SVNWorkCopy;

public class ClearClientCacheController extends DXAdminController{
	
	public String handlerClearClientCache() {
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectId = request.getParameter("pId");
			ProjectVersion projectVersion = SVNService.ProjectMap
					.get(projectId);
			String svnId = projectVersion.getExcelSvnId();
			SVNWorkCopy svnWorkCopy = SVNService.SVNMap.get(svnId);
			String workCopyPath = svnWorkCopy.getWorkCopyPath();
			String UploadPath = workCopyPath + "/" + FileSVNCache.UPLOAD_CLIENT_FOLDER_NAME;
			UtilTools.clearFolder(UploadPath);
			return NetUtils.packMessage(request, "清除缓存成功！");
		} catch (Exception ex) {
			return NetUtils.packMessage(request, ex.getMessage());
		}
	}
}
