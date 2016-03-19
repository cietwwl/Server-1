package com.fy.version;

import java.io.File;

public class VersionFileInfo {
	private File versionFile;
	private long fileModifyTime;
	
	public VersionFileInfo(){
		
	}

	public File getVersionFile() {
		return versionFile;
	}

	public void setVersionFile(File versionFile) {
		this.versionFile = versionFile;
	}

	public long getFileModifyTime() {
		return fileModifyTime;
	}

	public void setFileModifyTime(long fileModifyTime) {
		this.fileModifyTime = fileModifyTime;
	}
}
