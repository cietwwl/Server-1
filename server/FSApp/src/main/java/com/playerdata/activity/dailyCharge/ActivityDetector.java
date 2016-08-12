package com.playerdata.activity.dailyCharge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeCfg;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeCfgDAO;

/**
 * 活动状态检测
 * @author aken
 */
public class ActivityDetector {
	
	private Map<Integer, Integer> activityMap = new HashMap<Integer, Integer>();
	
	private static ActivityDetector instance = new ActivityDetector();
	
	public static ActivityDetector getInstance(){
		return instance;
	}
	
	public void detectActive(){
		Map<Integer, Integer> currentMap = new HashMap<Integer, Integer>();
		List<ActivityDailyChargeCfg> chargeCfgs = ActivityDailyChargeCfgDAO.getInstance().getAllCfg();
		if(null == chargeCfgs || chargeCfgs.isEmpty()) {
			activityMap = currentMap;
			return;
		}
		for(ActivityDailyChargeCfg cfg : chargeCfgs){
			if(isActive(cfg)){
				currentMap.put(cfg.getId(), cfg.getId());
			}
		}
		activityMap = currentMap;
 	}
	
	public ArrayList<Integer> getCurrentActivity(){
		return new ArrayList<Integer>(activityMap.values());
	}
	
	/**
	 * 根据开始和结束时间判断是否进行中
	 * @param cfg
	 * @return
	 */
	private boolean isActive(ActivityDailyChargeCfg cfg){
		if (null != cfg) {
			long startTime = cfg.getStartTime();
			long endTime = cfg.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}
}
