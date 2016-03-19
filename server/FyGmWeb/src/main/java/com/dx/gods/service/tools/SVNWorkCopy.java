package com.dx.gods.service.tools;

/**
 * SVN的配置信息
 * @author lida
 *
 */
public class SVNWorkCopy {
	private String svnId;
	private String svnName;
	private String svnPath;
	private String workCopyPath;
	private String loginName;
	private String loginPwd;
	
	public SVNWorkCopy(String svnId, String svnName, String svnPath, String workCopyPath, String loginName, String loginPwd){
		this.svnId = svnId;
		this.svnName = svnName;
		this.svnPath = svnPath;
		this.loginName = loginName;
		this.loginPwd = loginPwd;
		this.workCopyPath = workCopyPath;
	}

	public String getSvnId() {
		return svnId;
	}

	public String getWorkCopyPath() {
		return workCopyPath;
	}

	public String getSvnName() {
		return svnName;
	}

	public String getSvnPath() {
		return svnPath;
	}

	public String getLoginName() {
		return loginName;
	}

	public String getLoginPwd() {
		return loginPwd;
	}
	
	
}
