package com.playerdata.activityCommon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.playerdata.activityCommon.activityType.ActivityTypeItemIF;
import com.rw.fsutil.cacheDao.CfgCsvDao;

/**
 * 活动状态检测
 * 
 * @author aken
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ActivityDetector {
	
	private Map<Integer, HashMap<String, ? extends ActivityCfgIF>> activityMap = new HashMap<Integer, HashMap<String, ? extends ActivityCfgIF>>();
	
	private static ActivityDetector instance = new ActivityDetector();

	public static ActivityDetector getInstance() {
		return instance;
	}

	public void detectActive() {
		Map<Integer, HashMap<String, ? extends ActivityCfgIF>> currentTotalMap = new HashMap<Integer, HashMap<String, ? extends ActivityCfgIF>>();
		List<ActivityType> types = ActivityTypeFactory.getAllTypes();
		for(ActivityType activityType : types){
			HashMap<String, ActivityCfgIF> currentSubMap = new HashMap<String, ActivityCfgIF>();
			List<? extends ActivityCfgIF> actCfgs = activityType.getActivityDao().getAllCfg();
			if (null != actCfgs && !actCfgs.isEmpty()) {
				for (ActivityCfgIF cfg : actCfgs) {
					if (isActive(cfg)) {
						currentSubMap.put(String.valueOf(cfg.getCfgId()), cfg);
					}
				}
			}
			if(isMapChanged(currentSubMap, activityMap.get(activityType.getTypeId()), activityType)){
				activityType.addVerStamp();
			}
			currentTotalMap.put(activityType.getTypeId(), currentSubMap);
		}
		activityMap = currentTotalMap;
	}

	public <C extends ActivityCfgIF, T extends ActivityTypeItemIF> List<C> getAllActivityOfType(ActivityType<? extends CfgCsvDao<C>, T> type) {
		HashMap<String, C> subMap = (HashMap<String, C>) activityMap.get(type.getTypeId());
		if(null == subMap || subMap.isEmpty()) return Collections.emptyList();
		return new ArrayList<C>(subMap.values());
	}

	public boolean hasActivityOfType(ActivityType type) {
		HashMap<String, ? extends ActivityCfgIF> subMap = activityMap.get(type.getTypeId());
		return null != subMap && !subMap.isEmpty();
	}

	public boolean containsActivityByCfgId(ActivityType type, String cfgId) {
		HashMap<String, ? extends ActivityCfgIF> subMap = activityMap.get(type.getTypeId());
		if(null == subMap || subMap.isEmpty()) return false;
		return subMap.containsKey(cfgId);
	}
	
	public boolean containsActivityByActId(ActivityType type, String actId) {
		int id = Integer.valueOf(actId);
		HashMap<String, ? extends ActivityCfgIF> subMap = activityMap.get(type.getTypeId());
		if(null == subMap || subMap.isEmpty()) return false;
		for(ActivityCfgIF cfg : subMap.values()){
			if(id == cfg.getId()) return true;
		}
		return false;
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
	 * 检查两个map是否一样
	 * 该方法中有活动开始和结束的事件处理
	 * @param currentMap
	 * @param oldMap
	 * @param activityType
	 * @return 一样返回false，不一样返回true
	 */
	private boolean isMapChanged(HashMap<String, ? extends ActivityCfgIF> currentMap, HashMap<String, ? extends ActivityCfgIF> oldMap, ActivityType activityType){
		if((null == oldMap || oldMap.isEmpty()) && !currentMap.isEmpty()){
			for(ActivityCfgIF cfg : currentMap.values()){
				activityType.getActivityMgr().activityStartHandler(cfg);
			}
			return true;
		}
		
		if((null == currentMap || currentMap.isEmpty()) && !oldMap.isEmpty()){
			for(ActivityCfgIF cfg : currentMap.values()){
				activityType.getActivityMgr().activityEndHandler(cfg);
			}
			return true;
		}
		
		boolean changed = false;
		for(Entry<String, ? extends ActivityCfgIF> entry : currentMap.entrySet()){
			ActivityCfgIF oldCfg = oldMap.get(entry.getKey());
			if(null == oldCfg){
				//有新增的改变
				activityType.getActivityMgr().activityStartHandler(entry.getValue());
				changed = true;
			}
			if(oldCfg.getStartTime() != entry.getValue().getStartTime() ||
					oldCfg.getEndTime() != entry.getValue().getEndTime()){
				//无新增，只是时间的改变
				changed = true;
			}
		}
		for(Entry<String, ? extends ActivityCfgIF> entry : oldMap.entrySet()){
			if(!currentMap.containsKey(entry.getKey())){
				//有过期的活动
				activityType.getActivityMgr().activityEndHandler(entry.getValue());
				changed = true;
			}
		}
		
		return changed;
	}
}
