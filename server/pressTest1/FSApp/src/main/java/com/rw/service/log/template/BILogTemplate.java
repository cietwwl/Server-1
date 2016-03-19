package com.rw.service.log.template;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.rw.service.log.infoPojo.RoleGameInfo;
import com.rw.service.log.infoPojo.ZoneLoginInfo;
import com.rw.service.log.infoPojo.ZoneRegInfo;

public abstract class BILogTemplate {

	
	public String build(ZoneRegInfo zoneRegInfo, ZoneLoginInfo zoneLoginInfo, RoleGameInfo roleGameInfo, Map<String,String> moreInfo) {
		String log = getTextTemplate();
		try {
			Map<String, String> infoMap = null;
			if(zoneRegInfo!=null){
				infoMap = zoneRegInfo.getInfoMap();
			}else{
				infoMap = new HashMap<String, String>();
				
			}
			
			if(zoneLoginInfo!=null){
				infoMap.putAll(zoneLoginInfo.getInfoMap());
			}
			if(roleGameInfo!=null){
				infoMap.putAll(roleGameInfo.getInfoMap());
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
			GameLog.error(LogModule.BILOG.getName(), "BILogTemplate", "BILogTemplate[build]", e);
		}
		
		return log;
	}
	
	public abstract String getTextTemplate();

	public abstract Set<String> getInfoNameSet();

	
}
