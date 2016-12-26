package com.playerdata.activityCommon.modifiedActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.activityCommon.ActivityMgrHelper;
import com.playerdata.activityCommon.ActivityTimeHelper;
import com.playerdata.activityCommon.ActivityTimeHelper.TimePair;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.playerdata.activityCommon.activityType.ActivityExtendTimeIF;
import com.playerdata.activityCommon.activityType.ActivityRangeTimeIF;
import com.playerdata.activityCommon.activityType.ActivitySubCfgIF;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

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
					updateModifiedActivity(activityKey, entry.getValue(), true);
				}
			}
		}
	}
	
	/**
	 * 给玩家同步服务端的活动配置
	 * @param player
	 */
	public void synModifiedActivity(Player player, List<ActivityModifyItem> modifiedList){
		if(null == modifiedList){
			modifiedList = new ArrayList<ActivityModifyItem>();
			for(ActivityKey actKey : ActivityKey.values()){
				List<ActivityCfgIF> cfgs = actKey.getActivityType().getActivityDao().getAllCfg();
				if(null != cfgs){
					for(ActivityCfgIF cfg : cfgs){
						ActivityModifyItem item = new ActivityModifyItem();
						item.setId(cfg.getCfgId());
						item.setStartTime(cfg.getStartTimeStr());
						item.setEndTime(cfg.getEndTimeStr());
						item.setVersion(cfg.getVersion());
						item.setActDesc(cfg.getActDesc());
						if(cfg instanceof ActivityExtendTimeIF){
							item.setStartViceTime(((ActivityExtendTimeIF) cfg).getViceStartTime());
							item.setEndViceTime(((ActivityExtendTimeIF) cfg).getViceEndTime());
						}
						if(cfg instanceof ActivityRangeTimeIF){
							item.setRangeTime(((ActivityRangeTimeIF) cfg).getRangeTime());
						}
						modifiedList.add(item);
					}
				}
			}
		}
		if(!modifiedList.isEmpty()){
			ClientDataSynMgr.synDataList(player, modifiedList, eSynType.ActivityModifiedCfg, eSynOpType.UPDATE_PART_LIST);
		}
	}
	
	/**
	 * 给在线玩家同步有更改的活动配置
	 * @param player
	 */
	public void synModifiedActivityToAll(List<Integer> changedList){
		List<Player> players = PlayerMgr.getInstance().getOnlinePlayers();
		if(null == players || players.isEmpty()){
			return;
		}
		List<ActivityModifyItem> modifiedList = new ArrayList<ActivityModifyItem>();
		for(Integer cfgId : changedList){
			ActivityType actType = ActivityTypeFactory.getByCfgId(cfgId);
			ActivityCfgIF cfg = (ActivityCfgIF) actType.getActivityDao().getCfgById(String.valueOf(cfgId));
			if(null != cfg){
				ActivityModifyItem item = new ActivityModifyItem();
				item.setId(cfg.getCfgId());
				item.setStartTime(cfg.getStartTimeStr());
				item.setEndTime(cfg.getEndTimeStr());
				item.setVersion(cfg.getVersion());
				item.setActDesc(cfg.getActDesc());
				if(cfg instanceof ActivityExtendTimeIF){
					item.setStartViceTime(((ActivityExtendTimeIF) cfg).getViceStartTime());
					item.setEndViceTime(((ActivityExtendTimeIF) cfg).getViceEndTime());
				}
				if(cfg instanceof ActivityRangeTimeIF){
					item.setRangeTime(((ActivityRangeTimeIF) cfg).getRangeTime());
				}
				modifiedList.add(item);
			}
		}
		if(!modifiedList.isEmpty()){
			for(Player player : players){
				ActivityMgrHelper.getInstance().synActivityData(player, modifiedList);
			}
		}
	}
	
	/**
	 * 给玩家同步变化的活动配置
	 * @param data
	 */
	private void synSingalModifiedActivity(ActivityModifyItem data){
		List<Player> onlinePlayers = PlayerMgr.getInstance().getOnlinePlayers();
		for(Player player : onlinePlayers){
			if(null != data){				
				ClientDataSynMgr.synData(player, data, eSynType.ActivityModifiedCfg, eSynOpType.UPDATE_SINGLE);
			}
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
			GameLog.info("GM-gmSetCfgTime", "", "id[" + cfgId + "]暂时不支持动态更换时间");
			return;
		}
		long current = System.currentTimeMillis();
		TimePair time = ActivityTimeHelper.transToAbsoluteTime(startTime, endTime);
		long start = time.getStartMil();
		if(Math.abs(current - start) > THREE_MONTH_MS){
			return;
		}
		long end = time.getEndMil();
		if(Math.abs(current - end) > THREE_MONTH_MS){
			return;
		}
		ActivityModifyItem modifyItem = getModifiedActivity(activityKey, cfgId, 0);
		if(null == modifyItem){
			 modifyItem = new ActivityModifyItem();
			 modifyItem.setId(cfgId);
		} 
		//TODO 这里要处理版本的比较
		modifyItem.setStartTime(startTime);
		modifyItem.setEndTime(endTime);
		modifyItem.setVersion(version);
		updateModifiedActivity(activityKey, modifyItem, false);
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
			GameLog.info("GM-gmSetCfgTime", "", "id[" + cfgId + "]暂时不支持动态更换奖励物品");
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
		updateModifiedActivity(activityKey, modifyItem, false);
	}
	
	/**
	 * 修改活动的配置
	 * @param activityKey
	 * @param item
	 */
	private void updateModifiedActivity(ActivityKey activityKey, ActivityModifyItem item, boolean isDropOld){
		if(modifyActivityCfg(activityKey.getActivityType(), item)){
			// 更新数据库中关于配置表的修改
			ActivityModifyGlobleData globleData = getModifiedActivity(activityKey);
			if(null == globleData){
				globleData = new ActivityModifyGlobleData();
			}
			globleData.getItems().put(item.getId(), item);
			synSingalModifiedActivity(item);
			GameWorldFactory.getGameWorld().updateAttribute(activityKey.getGameWorldKey(), JsonUtil.writeValue(globleData));
		}else if(isDropOld){
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
		if(cfg.getVersion() <= item.getVersion()){
			if(StringUtils.isNotBlank(item.getStartTime()) && StringUtils.isNotBlank(item.getEndTime())){
				cfg.setStartAndEndTime(item.getStartTime(), item.getEndTime());
			}
			cfg.setVersion(item.getVersion());
			if(StringUtils.isNotBlank(item.getStartViceTime()) 
					&& StringUtils.isNotBlank(item.getEndViceTime()) 
					&& cfg instanceof ActivityExtendTimeIF){
				((ActivityExtendTimeIF) cfg).setViceStartAndEndTime(item.getStartViceTime(), item.getEndViceTime());
			}
			if(StringUtils.isNotBlank(item.getRangeTime()) 
					&& cfg instanceof ActivityRangeTimeIF){
				((ActivityRangeTimeIF) cfg).setRangeTime(item.getRangeTime());
			}
			setRewardContent(actType, item);
			return true;
		}
		return false;
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
