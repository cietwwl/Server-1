package com.playerdata.activityCommon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 活动状态检测
 * 
 * @author aken
 */
public class ActivityDetector {
	
	private Map<ActivityType1, HashMap<String, ActivityCfgIF>> activityMap = new HashMap<ActivityType1, HashMap<String, ActivityCfgIF>>();
	
	private static ActivityDetector instance = new ActivityDetector();

	public static ActivityDetector getInstance() {
		return instance;
	}

	public void detectActive() {
		Map<ActivityType1, HashMap<String, ActivityCfgIF>> currentTotalMap = new HashMap<ActivityType1, HashMap<String, ActivityCfgIF>>();
		for(ActivityType1 activityType : ActivityType1.values()){
			HashMap<String, ActivityCfgIF> currentSubMap = new HashMap<String, ActivityCfgIF>();
			List<? extends ActivityCfgIF> actCfgs = activityType.getDao().getAllCfg();
			if (null != actCfgs && !actCfgs.isEmpty()) {
				for (ActivityCfgIF cfg : actCfgs) {
					if (isActive(cfg)) {
						currentSubMap.put(String.valueOf(cfg.getId()), cfg);
					}
				}
			}
			if(isMapChanged(currentSubMap, activityMap.get(activityType))){
				activityType.addVerStamp();
			}
			currentTotalMap.put(activityType, currentSubMap);
		}
		activityMap = currentTotalMap;
	}

	public List<? extends ActivityCfgIF> getAllActivityOfType(ActivityType1 type) {
		HashMap<String, ActivityCfgIF> subMap = activityMap.get(type);
		if(null == subMap || subMap.isEmpty()) return Collections.emptyList();
		return new ArrayList<ActivityCfgIF>(subMap.values());
	}

	public boolean hasActivityOfType(ActivityType1 type) {
		HashMap<String, ActivityCfgIF> subMap = activityMap.get(type);
		return null != subMap && !subMap.isEmpty();
	}

	public boolean containsActivity(ActivityType1 type, String cfgId) {
		HashMap<String, ActivityCfgIF> subMap = activityMap.get(type);
		if(null == subMap || subMap.isEmpty()) return false;
		return subMap.containsKey(cfgId);
	}

	/**
	 * 根据开始和结束时间判断是否进行中
	 * 
	 * @param cfg
	 * @return
	 */
	public boolean isActive(ActivityCfgIF cfg) {
		if (null != cfg) {
			long startTime = cfg.getStartTime();
			long endTime = cfg.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}

	/**
	 * 获取活动当前第几天
	 * 
	 * @return
	 */
	public int getCurrentDay(ActivityCfgIF cfg) {
		return (int) ((System.currentTimeMillis() - cfg.getStartTime()) / (24 * 60 * 60 * 1000)) + 1;
	}
	
	/**
	 * 检查两个map是否一样
	 * @param currentMap
	 * @param oldMap
	 * @return 一样返回false，不一样返回true
	 */
	private boolean isMapChanged(HashMap<String, ActivityCfgIF> currentMap, HashMap<String, ActivityCfgIF> oldMap){
		if(currentMap.size() != oldMap.size() || null == oldMap){
			return true;
		}
		for(Entry<String, ActivityCfgIF> entry : currentMap.entrySet()){
			ActivityCfgIF oldCfg = oldMap.get(entry.getKey());
			if(null == oldCfg){
				return true;
			}
			if(oldCfg.getStartTime() != entry.getValue().getStartTime() ||
					oldCfg.getEndTime() != entry.getValue().getEndTime()){
				return true;
			}
		}
		return false;
	}
}
