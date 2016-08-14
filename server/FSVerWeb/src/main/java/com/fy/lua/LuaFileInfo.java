package com.fy.lua;

import java.io.File;

public class LuaFileInfo {
	private File luaFile;
	private String md5Value;
	public File getLuaFile() {
		return luaFile;
	}
	public void setLuaFile(File luaFile) {
		this.luaFile = luaFile;
	}
	public String getMd5Value() {
		return md5Value;
	}
	public void setMd5Value(String md5Value) {
		this.md5Value = md5Value;
	}	
}
