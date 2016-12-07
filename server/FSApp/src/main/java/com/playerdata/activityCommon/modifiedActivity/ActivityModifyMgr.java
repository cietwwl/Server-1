package com.playerdata.activityCommon.modifiedActivity;

import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.gameworld.GameWorldFactory;

public class ActivityModifyMgr {
	
	private static ActivityModifyMgr instance = new ActivityModifyMgr();
	
	public static ActivityModifyMgr getInstance(){
		return instance;
	}
	
	public ActivityModifyGlobleData getModifiedActivity(ActivityKey activityKey){
		String attribute = GameWorldFactory.getGameWorld().getAttribute(activityKey.getGameWorldKey());
		if (attribute != null && (attribute = attribute.trim()).length() > 0) {
			return JsonUtil.readValue(attribute, ActivityModifyGlobleData.class);
		}
		return null;
	}
	
	public ActivityModifyItem getModifiedActivity(ActivityKey activityKey, int id, int version){
		String attribute = GameWorldFactory.getGameWorld().getAttribute(activityKey.getGameWorldKey());
		if (attribute != null && (attribute = attribute.trim()).length() > 0) {
			ActivityModifyGlobleData globleData = JsonUtil.readValue(attribute, ActivityModifyGlobleData.class);
			if(null != globleData){
				ActivityModifyItem item = globleData.getItems().get(id);
				if(null != item){
					
				}
			}
		}
		return null;
	}
	
	public void updateModifiedActivity(ActivityKey activityKey, ActivityModifyItem item){
		
	}
}
