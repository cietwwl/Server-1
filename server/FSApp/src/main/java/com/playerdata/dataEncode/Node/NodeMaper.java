package com.playerdata.dataEncode.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

public class NodeMaper {

	
	public static NodeMaper fromText(String fieldInfo){
		return new NodeMaper(fieldInfo);
	}

	
	private Map<String,AtomicInteger> map = new ConcurrentHashMap<String,AtomicInteger>();
	
	private NodeMaper(String fieldInfo){
		
		if(StringUtils.isNotBlank(fieldInfo)){
			String[] split = fieldInfo.split(";");
			for (String classFieldTmp : split) {
				if(StringUtils.isNotBlank(classFieldTmp)){
					map.put(classFieldTmp, new AtomicInteger());
				}
			}			
		}
	}


	
	public boolean isEncodedField(String className, String fieldName){		
		String key = makeKey(className, fieldName);
		return map.containsKey(key);	
		
	}

	private String makeKey(String className, String fieldName) {
		String key = className + "-" + fieldName;
		return key;
	}
	
	public void incrEncodeCount(String className, String fieldName){
		String key = makeKey(className, fieldName);
		AtomicInteger count = map.get(key);
		if(count!=null){
			count.incrementAndGet();
		}
	}
	
	public void printZero(){
		StringBuilder sb = new StringBuilder();
		List<String> keyList = new ArrayList<String>(map.keySet());
		Collections.sort(keyList);
		for (String keyTmp : keyList) {
			AtomicInteger count = map.get(keyTmp);
			if(count.get() == 0){
				sb.append(keyTmp).append(";");
			}
		}
		//System.out.println("miss encode:"+sb.toString());
	}
}
