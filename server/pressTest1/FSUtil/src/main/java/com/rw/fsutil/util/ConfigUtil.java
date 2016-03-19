package com.rw.fsutil.util;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class ConfigUtil {
	
	private static final String DEFAULT_SETTINGS="config.properties";
	private static Properties props = null; 
	static{
		Resource resource = new ClassPathResource(DEFAULT_SETTINGS);
		try {
			props = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static String get(String key){
		return props.getProperty(key);
	}
	
}
