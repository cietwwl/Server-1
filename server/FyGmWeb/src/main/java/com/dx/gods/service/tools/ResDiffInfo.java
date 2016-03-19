package com.dx.gods.service.tools;

/**
 * 资源差异信息
 * @author lida
 *
 */
public class ResDiffInfo {
	public final static int RES_STATUS_UPDATE = 0;
	public final static int RES_STATUS_DELETE = 1;
	public final static int RES_STATUS_NEW = 2;
	
	private String fileName;
	private String filePath;
	private int status;     //0 update 1 delete 2 new
	private String patchName;
	private String md5Value;
	
	public ResDiffInfo(String fileName, String filePath, String patchName, int status, String md5Value){
		this.fileName = fileName;
		this.filePath = filePath;
		this.patchName = patchName;
		this.status = status;
		this.md5Value = md5Value;
	}

	public int getStatus() {
		return status;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getPatchName() {
		return patchName;
	}

	public String getMd5Value() {
		return md5Value;
	}
}
