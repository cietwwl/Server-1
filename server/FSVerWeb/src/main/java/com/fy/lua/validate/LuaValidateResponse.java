package com.fy.lua.validate;

public class LuaValidateResponse {
	private boolean result;
	private String downloadPath;
	private String backupDownloadPath;
	private String md5Value;
	private long downloadSize;
	public String fileName;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getDownloadPath() {
		return downloadPath;
	}
	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}
	public String getMd5Value() {
		return md5Value;
	}
	public void setMd5Value(String md5Value) {
		this.md5Value = md5Value;
	}
	public long getDownloadSize() {
		return downloadSize;
	}
	public void setDownloadSize(long downloadSize) {
		this.downloadSize = downloadSize;
	}
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getBackupDownloadPath() {
		return backupDownloadPath;
	}
	public void setBackupDownloadPath(String backupDownloadPath) {
		this.backupDownloadPath = backupDownloadPath;
	}
	
	
}	
