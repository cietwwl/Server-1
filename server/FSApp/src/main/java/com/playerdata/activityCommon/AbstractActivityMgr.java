package com.playerdata.activityCommon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeItemIF;
import com.playerdata.activityCommon.activityType.ActivityTypeSubItemIF;
import com.rw.fsutil.cacheDao.CfgCsvDao;


@SuppressWarnings({ "rawtypes", "unchecked"})
public abstract class AbstractActivityMgr<T extends ActivityTypeItemIF> implements ActivityRedPointUpdate {

	public void synData(Player player) {
		getHolder().synAllData(player);
	}

	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时清空 */
	protected void checkActivityOpen(Player player) {
		getHolder().refreshActivity(player.getUserId());
	}
	
	/**
	 * 设置活动是否被查看的红点
	 */
	public void updateRedPoint(Player player, String eNum) {
		if(!isThisActivityIndex(Integer.parseInt(eNum))){
			return;
		}
		ActivityType activityType = getHolder().getActivityType();
		ActivityCfgIF cfg = (ActivityCfgIF) activityType.getActivityDao().getCfgById(eNum);
		if (cfg == null) {
			return;
		}
		T dataItem = getHolder().getItem(player.getUserId(), String.valueOf(cfg.getId()));
		if (dataItem == null) {
			GameLog.error(LogModule.ComActivity, player.getUserId(), "心跳传入id[" + eNum + "]获得的页签枚举无法找到活动数据", null);
			return;
		}
		if (!dataItem.isHasViewed()) {
			dataItem.setHasViewed(true);
		}
		getHolder().updateItem(player, dataItem);
	}

	/**
	 * 判断红点
	 * 
	 * @param player
	 * @return
	 */
	protected List<String> haveRedPoint(Player player) {
		List<String> redPointList = new ArrayList<String>();
		List<T> items = getHolder().getItemList(player.getUserId());
		if (null == items || items.isEmpty())
			return redPointList;
		for (T item : items) {
			redPointList.addAll(checkRedPoint(player, item));
		}
		return redPointList;
	}
	
	/**
	 * 刷新每日活动
	 * 
	 * @param player
	 */
	protected void dailyRefreshNewDaySubActivity(Player player) {
		List<T> items = getHolder().getItemList(player.getUserId());
		if (null == items || items.isEmpty()) {
			return;
		}
		ActivityType activityType = getHolder().getActivityType();
		CfgCsvDao<? extends ActivityCfgIF> actDao = activityType.getActivityDao();
		for (T item : items) {
			ActivityCfgIF cfg = actDao.getCfgById(item.getCfgId());
			if(cfg.isDailyRefresh()){
				expireActivityHandler(player, item);
				dailyRefresh(player, item);
			}else{
				dailyCheck(player, item);
			}
		}
		getHolder().synAllData(player);
	}
	
	/**
	 * 需要每日刷新的活动
	 * @param player
	 * @param item
	 */
	private void dailyRefresh(Player player, T item){
		List<ActivityTypeSubItemIF> subItemList = new ArrayList<ActivityTypeSubItemIF>();
		List<String> todaySubs = getHolder().getTodaySubActivity(item.getCfgId());
		ActivityType activityType = getHolder().getActivityType();
		for (String subId : todaySubs) {
			ActivityTypeSubItemIF subItem = activityType.getNewActivityTypeSubItem();
			subItem.setCfgId(subId);
			subItemList.add(subItem);
		}
		item.setSubItemList(subItemList);
		getHolder().updateItem(player, item);
	}
	
	/**
	 * 不需要每日刷新的活动
	 * 每天要检查有更改的子项
	 * @param player
	 * @param item
	 */
	private void dailyCheck(Player player, T item){
		//不需要每日刷新的活动，检查新的子项，删除不存在的子项
		List<ActivityTypeSubItemIF> subItemList = item.getSubItemList();
		HashSet<String> subIDList = new HashSet<String>();
		Iterator<ActivityTypeSubItemIF> subItor = subItemList.iterator();
		List<String> todaySubs = getHolder().getTodaySubActivity(item.getCfgId());
		ActivityType activityType = getHolder().getActivityType();
		boolean changed = false;
		while(subItor.hasNext()){
			//移除旧的
			ActivityTypeSubItemIF subItem = subItor.next();
			if(!todaySubs.contains(subItem.getCfgId())){
				subItor.remove();
				changed = true;
			}else{
				subIDList.add(subItem.getCfgId());
			}
		}
		for (String subId : todaySubs) {
			//添加新的
			if(!subIDList.contains(subId)){
				ActivityTypeSubItemIF subItem = activityType.getNewActivityTypeSubItem();
				subItem.setCfgId(subId);
				subItemList.add(subItem);
				changed = true;
			}
		}
		if(changed){
			item.setSubItemList(subItemList);
			getHolder().updateItem(player, item);
		}
	}
	
	protected boolean isLevelEnough(Player player, ActivityCfgIF cfg) {
		if (null == cfg)
			return false;
		return player.getLevel() >= cfg.getLevelLimit();
	}
	
	/**
	 * 重载该函数做红点检测
	 * @param item
	 * @return
	 */
	protected List<String> checkRedPoint(Player player, T item){
		return new ArrayList<String>();
	}
	
	/**
	 * 重载该函数做活动过期处理
	 * @param player
	 * @param item
	 */
	protected void expireActivityHandler(Player player, T item){
		//如果有过期处理，就需要重载这个方法
	}
	
	protected abstract UserActivityChecker<T> getHolder();
	
	/**
	 * 判断一个id是否是该类活动的合法id
	 * (每类活动都有一个id取值范围)
	 * @param index
	 * @return
	 */
	protected abstract boolean isThisActivityIndex(int index);
}
