package com.rounter.loginServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.rounter.client.node.ServerInfo;





public class LoginServerInfo {
	Logger logger = LoggerFactory.getLogger(LoginServerInfo.class);
	
	private static LoginServerInfo instance = new LoginServerInfo();

	private long lastModifyTime = 0;
	private String fileName = "loginServerInfo.properties";
	
	public static LoginServerInfo getInstance(){
		return instance;
	}

	
	

	/**
	 * 检查登录服信息是否有被改变，如果改了，重装载
	 * @return
	 */
	public HashMap<String, ServerInfo> checkServerProp(){
		Resource resource = new ClassPathResource(fileName);
		try {
			File file = resource.getFile();
			if(file == null){
				logger.info("无法找到登录服配置文件！");
				return null;
			}
			if(!isModified(file)){
				return null;
			}

			lastModifyTime = file.lastModified();
			HashMap<String, ServerInfo> infoTempMap = new HashMap<String, ServerInfo>();
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			for (Entry<Object, Object> element : props.entrySet()) {
				
				ServerInfo info = new ServerInfo();
				String key = (String) element.getKey();
				String value = (String) element.getValue();
				String[] strs = value.split(":");
				info.setId(key);
				info.setIp(strs[0].trim());
				info.setPort(Integer.parseInt(strs[1].trim()));
				
				infoTempMap.put(info.getId(), info);
			}
			
			return infoTempMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	private boolean isModified(File file) {
		return file.lastModified() > lastModifyTime;
	}
	
	
	@SuppressWarnings("resource")
	private void fromFile(File file, HashMap<String, ServerInfo> infoTempMap){
		
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
					infoTempMap.put(info.getId(), info);
					
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
		in.checkServerProp();
	}
	
}
