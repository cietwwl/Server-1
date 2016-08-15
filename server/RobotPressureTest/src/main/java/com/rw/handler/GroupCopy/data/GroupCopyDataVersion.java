package com.rw.handler.GroupCopy.data;

import java.util.HashMap;
import java.util.Map;

import com.rw.dataSyn.JsonUtil;

public class GroupCopyDataVersion {
	
	public static String getDataVersion(){
		Map<String, Integer> versionMap = new HashMap<String, Integer>();

		//默认为初始版本
		versionMap.put("groupCopyLevelData", 0);
		versionMap.put("groupCopyMapData", 0);
        versionMap.put("groupCopyRewardData", 0);
        versionMap.put("groupCopyDropApplyData", 0);
        versionMap.put("serverCopyDamageRankData", 0);

        
		String json = JsonUtil.writeValue(versionMap);

		return json;
	}

}
