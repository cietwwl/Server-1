package com.dx.gods.service.tools;

public class FileSVNInfo {
	private String name;   			//文件名
	private String path;   			//文件路径
	private long svnVersion;  	//文件版本号
	private boolean blnGenerate = true; 	//是否生成json
	private String statusDes;
	private long lastUpdateTime;  //上次更新时间
	private boolean belongClient = true;   //属于客户端
	private boolean belongServer;   //属于服务器
	
	public FileSVNInfo(String name, String path, long svnVersion) {
		super();
		this.name = name;
		this.path = path;
		this.svnVersion = svnVersion;
	}
	
	public FileSVNInfo(String name, String path, long svnVersion, boolean blnGenerate, long lastUpdateTime) {
		super();
		this.name = name;
		this.path = path;
		this.svnVersion = svnVersion;
		this.blnGenerate = blnGenerate;
		this.lastUpdateTime = lastUpdateTime;
	}
	
	
	public String getName() {
		return name;
	}
	public String getPath() {
		return path;
	}
	public long getSvnVersion() {
		return svnVersion;
	}
	public boolean isBlnGenerate() {
		return blnGenerate;
	}
	public void setBlnGenerate(boolean blnGenerate) {
		this.blnGenerate = blnGenerate;
		if(blnGenerate){
			setStatusDes("已生成");
		}else{
			setStatusDes("未生成");
		}
	}

	public void setSvnVersion(long svnVersion) {
		this.svnVersion = svnVersion;
	}

	public String getStatusDes() {
		if(blnGenerate){
			statusDes = "已生成";
		}else{
			statusDes = "未生成";
		}
		return statusDes;
	}

	public void setStatusDes(String statusDes) {
		this.statusDes = statusDes;
	}

	public boolean isBelongClient() {
		return belongClient;
	}

	public void setBelongClient(boolean belongClient) {
		this.belongClient = belongClient;
	}

	public boolean isBelongServer() {
		return belongServer;
	}

	public void setBelongServer(boolean belongServer) {
		this.belongServer = belongServer;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
}
