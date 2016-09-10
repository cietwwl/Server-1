package com.fy.lua;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fy.SpringContextUtil;
import com.fy.utils.FileUtils;
import com.fy.utils.MD5Util;

public class LuaDao {
	
	private HashMap<String, LuaInfo> ChannelLuaMap = new HashMap<String, LuaInfo>();
	private HashMap<String, LuaFileInfo> LuaFileMap = new HashMap<String, LuaFileInfo>();
	
	private String luaDirPath = "";
	
	public static LuaDao getInstance(){
		return SpringContextUtil.getBean("luaDao");
	}
	
	public void init(){
		load();
	}
	
	public void load(){
		File luaDir = new File(luaDirPath);
		
		List<File> fileList = new ArrayList<File>();
		FileUtils.sumFiles(luaDir, fileList, ".txt");
		
		if(isModified(fileList)){
			List<LuaInfo> allLua = getAllLua(fileList);
			HashMap<String, LuaInfo> channelLuaMap = new HashMap<String, LuaInfo>();
			for (LuaInfo luaInfo : allLua) {
				String channel = luaInfo.getChannel();
				channelLuaMap.put(channel, luaInfo);
			}
			
			ChannelLuaMap = channelLuaMap;
		}
		for (File file : fileList) {
			if (!LuaFileMap.containsKey(file.getAbsolutePath())) {
				try {
					LuaFileInfo luaFileInfo = new LuaFileInfo();
					String md5Value = MD5Util.getFileMD5String(file);
					luaFileInfo.setMd5Value(md5Value);
					luaFileInfo.setLuaFile(file);
					LuaFileMap.put(file.getAbsolutePath(), luaFileInfo);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	private boolean isModified(List<File> fileList) {
		boolean reault = false;
		try {
			if (fileList.size() < LuaFileMap.size()) {
				return true;
			}
			for (File file : fileList) {
				LuaFileInfo luaFileInfo = LuaFileMap
						.get(file.getAbsolutePath());
				if (luaFileInfo != null) {
					String fileMD5Value = MD5Util.getFileMD5String(file);
					if (!luaFileInfo.getMd5Value().equals(fileMD5Value)) {
						reault = true;
						luaFileInfo.setMd5Value(fileMD5Value);
					}
				} else {
					reault = true;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return reault;
	}
	
	private List<LuaInfo> getAllLua(List<File> fileList){
		List<LuaInfo> allLuaList = new ArrayList<LuaInfo>();
		for (File file : fileList) {
			try {
				LuaInfo fromFile = fromFile(file);
				if(fromFile!=null){
					allLuaList.add(fromFile);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return allLuaList;
	}
	
	private LuaInfo fromFile(File file) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		LuaInfo version = null;
		try {
			version = LuaInfo.fromFile(file);
		}catch (Exception e){
			e.printStackTrace();
			throw(new RuntimeException("Lua配置有错，请检查."));
		}finally{
			reader.close();
		}
		return version;
		
	}
	
	public LuaInfo getLuaInfo(String channel){
		return ChannelLuaMap.get(channel);
	}

	public void setLuaDirPath(String luaDirPath) {
		this.luaDirPath = luaDirPath;
	}
}
