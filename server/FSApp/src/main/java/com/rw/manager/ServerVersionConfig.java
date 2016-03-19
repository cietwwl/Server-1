package com.rw.manager;

import com.rw.fsutil.util.SpringContextUtil;
import com.rw.netty.ServerConfig;

public class ServerVersionConfig {
	private String version;
	
	public static ServerVersionConfig getInstance(){
		return SpringContextUtil.getBean("serverVersionConfig");
	} 
	
	public void init(){

	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
