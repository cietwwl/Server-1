package com.playerdata.activity.dailyCharge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeCfg;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeCfgDAO;

/**
 * 活动状态检测
 * 
 * @author aken
 */
public class ActivityDetector {

	private Map<String, ActivityDailyChargeCfg> activityMap = new ConcurrentHashMap<String, ActivityDailyChargeCfg>(16, 0.75f, 2);

	private static ActivityDetector instance = new ActivityDetector();

	public static ActivityDetector getInstance() {
		return instance;
	}

	public void detectActive() {
		Map<String, ActivityDailyChargeCfg> currentMap = new ConcurrentHashMap<String, ActivityDailyChargeCfg>(16, 0.75f, 2);
		List<ActivityDailyChargeCfg> chargeCfgs = ActivityDailyChargeCfgDAO.getInstance().getAllCfg();
		if (null == chargeCfgs || chargeCfgs.isEmpty()) {
			activityMap = currentMap;
			return;
		}
		for (ActivityDailyChargeCfg cfg : chargeCfgs) {
			if (isActive(cfg)) {
				currentMap.put(String.valueOf(cfg.getId()), cfg);
			}
		}
		activityMap = currentMap;
	}

	public List<ActivityDailyChargeCfg> getAllDailyActivity() {
		return new ArrayList<ActivityDailyChargeCfg>(activityMap.values());
	}

	public boolean hasDailyCharge() {
		return !activityMap.isEmpty();
	}

	public boolean containsActivity(String cfgId) {
		return activityMap.containsKey(cfgId);
	}

	/**
	 * 根据开始和结束时间判断是否进行中
	 * 
	 * @param cfg
	 * @return
	 */
	public boolean isActive(ActivityDailyChargeCfg cfg) {
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
	public int getCurrentDay(ActivityDailyChargeCfg cfg) {
		return (int) ((System.currentTimeMillis() - cfg.getStartTime()) / (24 * 60 * 60 * 1000)) + 1;
	}
}
