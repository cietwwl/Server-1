package com.rw.service.log.template;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.rw.service.log.RegLog;
import com.rw.service.log.infoPojo.ClientInfo;

public abstract class BILogTemplate {

	
	public String build(ClientInfo clientInfo, Map<String,String> moreInfo,RegLog reglog) {
		String log = getTextTemplate();
		try {
			Map<String, String> infoMap = null;
			if(clientInfo!=null){
				infoMap = clientInfo.getInfoMap();
			}else{
				infoMap = new HashMap<String, String>();
			}
			if(reglog != null){
				infoMap = reglog.getInfoMap(infoMap);
			}
			
			
			if(moreInfo!=null){
				infoMap.putAll(moreInfo);
			}
			for (String infoName : getInfoNameSet()) {
				String infoValue = infoMap.get(infoName);
				if(StringUtils.isBlank(infoValue)){
					infoValue = "";
				}
				String token = "$"+infoName+"$";
				log = log.replace(token, infoValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return log;
	}
	
	public abstract String getTextTemplate();

	public abstract Set<String> getInfoNameSet();

	
}
