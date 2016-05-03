package com.rw.service.Privilege.datamodel;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

public class PrivilegeConfigHelper {
	private static PrivilegeConfigHelper instance;

	public static PrivilegeConfigHelper getInstance() {
		if (instance == null) {
			instance = new PrivilegeConfigHelper();
		}
		return instance;
	}

	private HashMap<String,IPrivilegeConfigSourcer<?>> sourcerMap=new HashMap<String,IPrivilegeConfigSourcer<?>>();
	
	public void addOrReplace(String configSourcerName,IPrivilegeConfigSourcer<?> sourcer){
		if (StringUtils.isNotBlank(configSourcerName) && sourcer != null){
			sourcerMap.put(configSourcerName,sourcer);
		}
	}
	
	public Iterable<IPrivilegeConfigSourcer<?>> getSources(){
		return sourcerMap.values();
	}
}
