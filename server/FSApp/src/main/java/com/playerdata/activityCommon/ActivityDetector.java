package com.playerdata.activityCommon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.playerdata.activityCommon.activityType.ActivityTypeItemIF;
import com.playerdata.activityCommon.timeControl.ActivitySpecialTimeMgr;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;
import com.rwbase.gameworld.PlayerPredecessor;

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
	
	protected ActivityDetector(){
		// 读取数据库中停服前保存的活动数据（单例，只会加载一次）
		String attribute = GameWorldFactory.getGameWorld().getAttribute(GameWorldKey.ALIVE_ACTIVITY);
		if (attribute != null && (attribute = attribute.trim()).length() > 0) {
			ActivityAliveGlobleData _globalData = JsonUtil.readValue(attribute, ActivityAliveGlobleData.class);
			if(null != _globalData && null != _globalData.getActivityMap() && !_globalData.getActivityMap().isEmpty()){
				Map<Integer, HashMap<String, ? extends ActivityCfgIF>> currentTotalMap = new HashMap<Integer, HashMap<String, ? extends ActivityCfgIF>>();
				for(Entry<Integer, ArrayList<String>> entry : _globalData.getActivityMap().entrySet()){
					ActivityType activityType = ActivityTypeFactory.getType(entry.getKey());
					if(null != activityType){
						HashMap<String, ActivityCfgIF> cfgs = new HashMap<String, ActivityCfgIF>();
						CfgCsvDao<? extends ActivityCfgIF> cfgDao = activityType.getActivityDao();
						for(String cfgId : entry.getValue()){
							ActivityCfgIF cfg = cfgDao.getCfgById(cfgId);
							if(null != cfg){
								cfgs.put(cfgId, cfg);
							}
						}
						if(!cfgs.isEmpty()) {
							currentTotalMap.put(activityType.getTypeId(), cfgs);
						}
					}
				}
				activityMap = currentTotalMap;
			}
		}
	}

	public void detectActive() {
		if(!ActivitySpecialTimeMgr.ISINIT.get() && System.currentTimeMillis() - ActivitySpecialTimeMgr.LISTENTIME < 120000){
			//等待登录服推送活动消息期间，不检测活动变化（限时2分钟）
			return ;
		}
		Map<Integer, HashMap<String, ? extends ActivityCfgIF>> currentTotalMap = new HashMap<Integer, HashMap<String, ? extends ActivityCfgIF>>();
		List<ActivityType> types = ActivityTypeFactory.getAllTypes();
		boolean changed = false;
		List<Player> players = PlayerMgr.getInstance().getOnlinePlayers();
		for(final ActivityType activityType : types){
			if(null == activityType.getActivityMgr()){
				continue;
			}
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
				changed = true;
				for(final Player player : players){
					GameWorldFactory.getGameWorld().asyncExecute(player.getUserId(), new PlayerPredecessor() {
						@Override
						public void run(String e) {
							activityType.getActivityMgr().synData(player);
						}
					});
				}
			}
			if(!currentSubMap.isEmpty()){
				currentTotalMap.put(activityType.getTypeId(), currentSubMap);
			}
		}
		activityMap = currentTotalMap;
		if(changed){
			//活动有变化时，保存一下数据库
			ActivityAliveGlobleData _globalData = new ActivityAliveGlobleData();
			HashMap<Integer, ArrayList<String>> newActivity = new HashMap<Integer, ArrayList<String>>();
			if(null != activityMap){
				for(Entry<Integer, HashMap<String, ? extends ActivityCfgIF>> entry : activityMap.entrySet()){
					newActivity.put(entry.getKey(), new ArrayList<String>(entry.getValue().keySet()));
				}
			}
			_globalData.setActivityMap(newActivity);
			GameWorldFactory.getGameWorld().updateAttribute(GameWorldKey.ALIVE_ACTIVITY, JsonUtil.writeValue(_globalData));
		}
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

	public boolean containsActivityByCfgId(ActivityType type, String cfgId, int version) {
		HashMap<String, ? extends ActivityCfgIF> subMap = activityMap.get(type.getTypeId());
		if(null == subMap || subMap.isEmpty()) return false;
		ActivityCfgIF cfg = subMap.get(cfgId);
		if(null == cfg) return false;
		return cfg.getVersion() == version;
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
		if((null == currentMap || currentMap.isEmpty()) && (null == oldMap || oldMap.isEmpty())){
			//都为空时，未改变
			return false;
		}
		//全部是新出现的活动，处理开始事件
		if((null == oldMap || oldMap.isEmpty()) && !currentMap.isEmpty()){
			for(ActivityCfgIF cfg : currentMap.values()){
				activityType.getActivityMgr().activityStartHandler(cfg);
			}
			return true;
		}
		//全部是已结束的活动，处理结束事件
		if((null == currentMap || currentMap.isEmpty()) && !oldMap.isEmpty()){
			for(ActivityCfgIF cfg : oldMap.values()){
				activityType.getActivityMgr().activityEndHandler(cfg);
			}
			return true;
		}
		//检查哪些是新增的，哪些是过期的
		boolean changed = false;
		for(Entry<String, ? extends ActivityCfgIF> entry : currentMap.entrySet()){
			ActivityCfgIF oldCfg = oldMap.get(entry.getKey());
			if(null == oldCfg){
				//有新增的改变
				activityType.getActivityMgr().activityStartHandler(entry.getValue());
				changed = true;
			}else if(oldCfg.getStartTime() != entry.getValue().getStartTime() ||
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
