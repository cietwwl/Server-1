package com.dx.gods.service.tools;


public class ProjectVersion {
	private String versionId;
	private String versionName;
	private String serverSvnId;
	private String clientSvnId;
	private String excelSvnId;
	private String clientupdateSvnId;
	private String class_svnId;
	private String util_svnId;
	private String client_res_svnId;
	
	public ProjectVersion(String versionId, String versionName, String excelSvnId, 
			String serverSvnId, String clientSvnId, String clientupdateSvnId,
			String class_svnId, String util_svnId, String client_res_svnId){
		this.versionId = versionId;
		this.versionName = versionName;
		this.excelSvnId = excelSvnId;
		this.serverSvnId = serverSvnId;
		this.clientSvnId = clientSvnId;
		this.clientupdateSvnId = clientupdateSvnId;
		this.class_svnId = class_svnId;
		this.util_svnId = util_svnId;
		this.client_res_svnId = client_res_svnId;
	}

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getServerSvnId() {
		return serverSvnId;
	}

	public void setServerSvnId(String serverSvnId) {
		this.serverSvnId = serverSvnId;
	}

	public String getClientSvnId() {
		return clientSvnId;
	}

	public void setClientSvnId(String clientSvnId) {
		this.clientSvnId = clientSvnId;
	}

	public String getExcelSvnId() {
		return excelSvnId;
	}

	public void setExcelSvnId(String excelSvnId) {
		this.excelSvnId = excelSvnId;
	}

	public String getClientupdateSvnId() {
		return clientupdateSvnId;
	}

	public String getClass_svnId() {
		return class_svnId;
	}

	public String getUtil_svnId() {
		return util_svnId;
	}

	public String getClient_res_svnId() {
		return client_res_svnId;
	}
}
