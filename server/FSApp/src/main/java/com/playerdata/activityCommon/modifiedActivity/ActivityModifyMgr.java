package com.playerdata.activityCommon.modifiedActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.activityCommon.ActivityTimeHelper;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.playerdata.activityCommon.activityType.ActivitySubCfgIF;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.gameworld.GameWorldFactory;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ActivityModifyMgr {
	
	private static ActivityModifyMgr instance = new ActivityModifyMgr();
	private final long THREE_MONTH_MS = 3L * 30 * 24 * 60 * 60 * 1000;
	
	public static ActivityModifyMgr getInstance(){
		return instance;
	}
	
	/**
	 * 检查活动配置有没有GM修改
	 * @param activityKey
	 * @param cfgId
	 */
	public void checkModifiedActivity(){
		for(ActivityKey activityKey : ActivityKey.values()){
			ActivityModifyGlobleData globleData = getModifiedActivity(activityKey);
			if(null != globleData){
				for(Entry<Integer, ActivityModifyItem> entry : globleData.getItems().entrySet()){
					updateModifiedActivity(activityKey, entry.getValue());
				}
			}
		}
	}
	
	/**
	 * 给玩家同步修改过的活动配置
	 * @param player
	 */
	public void synModifiedActivity(Player player){
		List<ActivityModifyGlobleData> modifiedList = new ArrayList<ActivityModifyGlobleData>();
		for(ActivityKey actKey : ActivityKey.values()){
			ActivityModifyGlobleData modiData = getModifiedActivity(actKey);
			if(null != modiData){
				modifiedList.add(modiData);
			}
		}
		if(!modifiedList.isEmpty()){
			//ClientDataSynMgr.synDataList(player, modifiedList, eSynType.ActivityModifiedCfg, eSynOpType.UPDATE_LIST);
		}
	}
	
	/**
	 * 获取修改过的活动配置
	 * @param activityKey
	 * @return
	 */
	public ActivityModifyGlobleData getModifiedActivity(ActivityKey activityKey){
		String attribute = GameWorldFactory.getGameWorld().getAttribute(activityKey.getGameWorldKey());
		if (attribute != null && (attribute = attribute.trim()).length() > 0) {
			return JsonUtil.readValue(attribute, ActivityModifyGlobleData.class);
		}
		return null;
	}
	
	/**
	 * 获取大于指定版本号的
	 * @param activityKey
	 * @param id
	 * @param version
	 * @return
	 */
	public ActivityModifyItem getModifiedActivity(ActivityKey activityKey, int id, int version){
		ActivityModifyGlobleData globleData = getModifiedActivity(activityKey);
		if(null != globleData){
			ActivityModifyItem item = globleData.getItems().get(id);
			if(null != item){
				if(item.getVersion() > version){
					return item;
				}
			}
		}
		return null;
	}
	
	/**
	 * gm命令设置活动的开始和结束时间
	 * @param cfgId
	 * @param startTime
	 * @param endTime
	 */
	public void gmSetCfgTime(int cfgId, String startTime, String endTime, int version){
		ActivityKey activityKey = ActivityKey.getByCfgId(cfgId);
		if(null == activityKey){
			return;
		}
		long current = System.currentTimeMillis();
		long start = ActivityTimeHelper.cftStartTimeToLong(startTime);
		if(Math.abs(current - start) > THREE_MONTH_MS){
			return;
		}
		long end = ActivityTimeHelper.cftEndTimeToLong(start, endTime);
		if(Math.abs(current - end) > THREE_MONTH_MS){
			return;
		}
		ActivityModifyItem modifyItem = getModifiedActivity(activityKey, cfgId, 0);
		if(null == modifyItem){
			 modifyItem = new ActivityModifyItem();
			 modifyItem.setId(cfgId);
			 modifyItem.setVersion(version);
		}
		modifyItem.setStartTime(startTime);
		modifyItem.setEndTime(endTime);
		updateModifiedActivity(activityKey, modifyItem);
	}
	
	/**
	 * gm命令设置活动的奖励内容
	 * @param cfgId
	 * @param subCfgId
	 * @param reward
	 */
	public void gmSetSubCfgReward(int cfgId, int subCfgId, String reward, int version){
		ActivityKey activityKey = ActivityKey.getByCfgId(cfgId);
		if(null == activityKey){
			return;
		}
		ActivityModifyItem modifyItem = getModifiedActivity(activityKey, cfgId, 0);
		if(null == modifyItem){
			 modifyItem = new ActivityModifyItem();
			 modifyItem.setId(cfgId);
			 modifyItem.setVersion(version);
		}
		if(null == modifyItem.getRewardStrMap()){
			modifyItem.setRewardStrMap(new HashMap<Integer, String>());
		}
		modifyItem.getRewardStrMap().put(subCfgId, reward);
		updateModifiedActivity(activityKey, modifyItem);
	}
	
	/**
	 * 修改活动的配置
	 * @param activityKey
	 * @param item
	 */
	private void updateModifiedActivity(ActivityKey activityKey, ActivityModifyItem item){
		if(modifyActivityCfg(activityKey.getActivityType(), item)){
			// 更新数据库中关于配置表的修改
			ActivityModifyGlobleData globleData = getModifiedActivity(activityKey);
			if(null == globleData){
				globleData = new ActivityModifyGlobleData();
			}
			ActivityModifyItem oldItem = globleData.getItems().get(item.getId());
			if(null == oldItem || oldItem.getVersion() < item.getVersion()){
				globleData.getItems().put(item.getId(), item);
				GameWorldFactory.getGameWorld().updateAttribute(activityKey.getGameWorldKey(), JsonUtil.writeValue(globleData));
			}
		}else{
			ActivityModifyGlobleData globleData = getModifiedActivity(activityKey);
			if(null != globleData){
				globleData.getItems().remove(item.getId());
				GameWorldFactory.getGameWorld().updateAttribute(activityKey.getGameWorldKey(), JsonUtil.writeValue(globleData));
			}
		}
	}
	
	/**
	 * 修改活动主表的配置
	 * 主要是修改开始和结束时间
	 * @param actType
	 * @param item
	 * @return
	 */
	private boolean modifyActivityCfg(ActivityType actType, ActivityModifyItem item){
		if(null == actType) return false;
		CfgCsvDao<? extends ActivityCfgIF> actDao = actType.getActivityDao();
		ActivityCfgIF cfg = actDao.getCfgById(String.valueOf(item.getId()));
		if(null == cfg){
			return false;
		}
		if(cfg.getVersion() < item.getVersion()){
			if(StringUtils.isNotBlank(item.getStartTime())){
				cfg.setStartTime(item.getStartTime());
			}
			if(StringUtils.isNotBlank(item.getEndTime())){
				cfg.setEndTime(item.getEndTime());
			}
			setRewardContent(actType, item);
		}
		return true;
	}
	
	/**
	 * 修改配置表的奖励内容
	 * @param actType
	 * @param item
	 */
	private void setRewardContent(ActivityType actType, ActivityModifyItem item){
		HashMap<Integer, String> rewardMap = item.getRewardStrMap();
		if(null == rewardMap || rewardMap.isEmpty()){
			return;
		}
		CfgCsvDao<? extends ActivitySubCfgIF> actSubDao = actType.getSubActivityDao();
		if(null == actSubDao){
			return;
		}
		for(Entry<Integer, String> entry : rewardMap.entrySet()){
			ActivitySubCfgIF subCfg = actSubDao.getCfgById(String.valueOf(entry.getKey()));
			if(null != subCfg){
				subCfg.setCfgReward(entry.getValue());
			}
		}
	}
}
