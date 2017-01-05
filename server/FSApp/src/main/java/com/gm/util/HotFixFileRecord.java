package com.gm.util;

import java.util.List;

public class HotFixFileRecord {

	private String version; // 版本
	private List<String> multipleTimeFiles;  // multipleTimes的文件
	private List<String> ontimeFiles; // oneTimes的文件
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public List<String> getMultipleTimeFiles() {
		return multipleTimeFiles;
	}
	
	public void setMultipleTimeFiles(List<String> multipleTimeFiles) {
		this.multipleTimeFiles = multipleTimeFiles;
	}
	
	public List<String> getOntimeFiles() {
		return ontimeFiles;
	}
	
	public void setOntimeFiles(List<String> ontimeFiles) {
		this.ontimeFiles = ontimeFiles;
	}
}
