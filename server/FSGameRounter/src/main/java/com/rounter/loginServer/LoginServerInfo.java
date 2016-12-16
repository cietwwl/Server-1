package com.rounter.loginServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rounter.client.node.ServerInfo;



public class LoginServerInfo {
	Logger logger = LoggerFactory.getLogger(LoginServerInfo.class);
	
	private static LoginServerInfo instance = new LoginServerInfo();

	private long lastModifyTime = 0;
	private String fileName = "loginServerInfo.txt";
	
	private HashMap<String, ServerInfo> loginServerInfoMap = new HashMap<String, ServerInfo>();
	
	public static LoginServerInfo getInstance(){
		return instance;
	}


	/**
	 * 检查登录服信息是否有被改变，如果改了，重装载
	 * @return
	 */
	public HashMap<String, ServerInfo> checkAndRefreshMap(){
		String path  = ClassLoader.getSystemResource(fileName).getPath();
		File file = new File(path);
		if(isModified(file)){
			lastModifyTime = file.lastModified();
			fromFile(file);
		}
		return loginServerInfoMap;
	}


	private boolean isModified(File file) {
		return file.lastModified() > lastModifyTime;
	}
	
	
	@SuppressWarnings("resource")
	private void fromFile(File file){
		loginServerInfoMap.clear();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			logger.error("加载登录服信息，找不到目标配置，配置路径：{}",file.getPath());
			e.printStackTrace();
			return;
		}
		try {
			
			String line = reader.readLine();
			while (line != null && StringUtils.isNotBlank(line)) {
				try {
					
					ServerInfo info = new ServerInfo();
					String[] split = line.split("=");
					String[] ipInfo = split[1].split(":");
					info.setId(split[0].trim());
					info.setIp(ipInfo[0].trim());
					info.setPort(Integer.parseInt(ipInfo[1].trim()));
					loginServerInfoMap.put(info.getId(), info);
					
				} catch (Exception e) {
					logger.error("登录服信息不正确，无法解释：{}",line);
					e.printStackTrace();
				}
				line = reader.readLine();
			}
			
		} catch (Exception e) {
			logger.error("加载登录服信息时异常");
			e.printStackTrace();
		}
		
		
	}
	
	public static void main(String[] args) {
		LoginServerInfo in = new LoginServerInfo();
		in.checkAndRefreshMap();
	}
	
}
