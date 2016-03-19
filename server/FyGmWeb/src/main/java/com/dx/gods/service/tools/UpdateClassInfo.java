package com.dx.gods.service.tools;

import java.util.ArrayList;
import java.util.List;

public class UpdateClassInfo {
	private List<String> classPath = new ArrayList<String>();
	private List<String> libPath = new ArrayList<String>();
	
	public UpdateClassInfo(List<String> classPath, List<String> libPath){
		this.classPath = classPath;
		this.libPath = libPath;
	}

	public List<String> getClassPath() {
		return classPath;
	}

	public List<String> getLibPath() {
		return libPath;
	}
}
