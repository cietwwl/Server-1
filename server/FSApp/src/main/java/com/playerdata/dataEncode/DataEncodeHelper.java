package com.playerdata.dataEncode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class DataEncodeHelper {

	
	public static String mapToStr(Map<String,String> map){
		Set<String> keySet = map.keySet();
		List<String> keyList = new ArrayList<String>(keySet);
		Collections.sort(keyList);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");

		int count = 1;
		for (String keytmp : keyList) {
			String value = map.get(keytmp);
			if(StringUtils.isNotBlank(value)){
				sb.append(keytmp).append("-").append(value);	
				if(count!=keyList.size()){
					sb.append("|");
				}
			}
			count++;
		}	
		
		sb.append("}");
		
		return sb.toString();
	}
	
	public static String listToStr(List<String> list){
		
		Collections.sort(list);
		
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		int count = 1;
		for (String valueTmp : list) {		
			if(StringUtils.isNotBlank(valueTmp)){
				sb.append(valueTmp);		
				if(count!=list.size()){
					sb.append("-");
				}
			}
			count++;
		}	
		
		sb.append("]");
		
		return sb.toString();
	}
}
