package com.dx.gods.service.tools;

import java.util.HashMap;
import java.util.List;

import org.jdom.Element;


public class SVNService {
	
	public final static HashMap<String, SVNWorkCopy> SVNMap = new HashMap<String, SVNWorkCopy>();
	
	//<projectId, ProjectVersion>
	public final static HashMap<String, ProjectVersion> ProjectMap = new HashMap<String, ProjectVersion>();
	
	@SuppressWarnings("unchecked")
	public static void init(Element element)throws Exception{
		try{
			if (element.getName().equals("SVNList")) {
				List<Element> elements2 = element.getChildren();
				for (Element e2 : elements2) {
					String svnId = e2.getAttributeValue("svnID");
					String svnName = e2.getAttributeValue("svnName");
					String svnPath = e2.getAttributeValue("svnPath");
					String workCopyPath = e2.getAttributeValue("workCopyPath");
					String loginName = e2.getAttributeValue("loginName");
					String loginPwd = e2.getAttributeValue("loginPwd");
					SVNWorkCopy svn = new SVNWorkCopy(svnId, svnName, svnPath,
							workCopyPath, loginName, loginPwd);
					SVNService.SVNMap.put(svnId, svn);
				}
			}
			
			if(element.getName().equals("Versions")){
				List<Element> elements2 = element.getChildren();
				for(Element e2 : elements2){
					String versionId = e2.getAttributeValue("versionId");
					String versionName = e2.getAttributeValue("versionName");
					String excel_svnId = e2.getAttributeValue("excel_svnId");
					String client_svnId = e2.getAttributeValue("client_svnId");
					String server_svnId = e2.getAttributeValue("server_svnId");
					String client_update_svnId = e2.getAttributeValue("client_update_svnId");
					String class_svnId = e2.getAttributeValue("class_svnId");
					String util_svnId = e2.getAttributeValue("util_svnId");
					String client_res_svnId = e2.getAttributeValue("client_res_svnId");
					ProjectVersion pv = new ProjectVersion(versionId, versionName, excel_svnId, server_svnId, client_svnId, 
							client_update_svnId, class_svnId, util_svnId, client_res_svnId);
					SVNService.ProjectMap.put(versionId, pv);
				}
			}
		}catch(Exception ex){
			throw new Exception("init SVNService异常："+ex.getMessage());
		}
	}
}
