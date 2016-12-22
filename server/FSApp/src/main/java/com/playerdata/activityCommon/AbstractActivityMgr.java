package com.playerdata.activityCommon;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.playerdata.activityCommon.activityType.IndexRankJudgeIF;
import com.rw.fsutil.cacheDao.CfgCsvDao;


@SuppressWarnings({ "rawtypes", "unchecked"})
public abstract class AbstractActivityMgr<T extends ActivityTypeItemIF> implements ActivityRedPointUpdate, IndexRankJudgeIF{

	public void synData(Player player) {
		getHolder().synAllData(player);
	}
	
	public void synDataWithoutEmpty(Player player) {
		getHolder().synAllDataWithoutEmpty(player);
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
		if (null == items || items.isEmpty()){
			return redPointList;
		}
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
	protected void dailyRefresh(Player player, T item){
		item.setSubItemList(getHolder().newSubItemList(item.getCfgId()));
		getHolder().updateItem(player, item);
	}
	
	/**
	 * 不需要每日刷新的活动
	 * 每天要检查有更改的子项
	 * @param player
	 * @param item
	 */
	protected void dailyCheck(Player player, T item){
		List<ActivityTypeSubItemIF> oldSubItemList = item.getSubItemList();
		HashSet<String> oldSubIDList = new HashSet<String>();
		Iterator<ActivityTypeSubItemIF> oldSubItor = oldSubItemList.iterator();
		List<? extends ActivityTypeSubItemIF> todaySubs = getHolder().newSubItemList(item.getCfgId());
		HashMap<String, ActivityTypeSubItemIF> todaySubMap = new HashMap<String, ActivityTypeSubItemIF>();
		for(ActivityTypeSubItemIF todaySub : todaySubs){
			todaySubMap.put(todaySub.getCfgId(), todaySub);
		}
		boolean changed = false;
		while(oldSubItor.hasNext()){
			//移除旧的
			ActivityTypeSubItemIF oldSubItem = oldSubItor.next();
			if(!todaySubMap.containsKey(oldSubItem.getCfgId())){
				oldSubItor.remove();
				changed = true;
			}else{
				oldSubIDList.add(oldSubItem.getCfgId());
			}
		}
		for (String subId : todaySubMap.keySet()) {
			//添加新的
			if(!oldSubIDList.contains(subId)){
				oldSubItemList.add(todaySubMap.get(subId));
				changed = true;
			}
		}
		if(changed){
			item.setSubItemList(oldSubItemList);
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
	 * 重载该函数做活动过期处理（个人的处理）
	 * @param player
	 * @param item
	 */
	protected void expireActivityHandler(Player player, T item){
		//如果有过期处理，就需要重载这个方法
	}
	
	/**
	 * 重载该函数做活动开始的统一处理（全服，非个人）
	 * @param cfg 开始的活动的配置
	 */
	protected void activityStartHandler(ActivityCfgIF cfg){
		//如果有活动开始的统一处理，就需要重载这个方法
	}
	
	/**
	 * 重载该函数做活动结束的统一处理（全服，非个人）
	 * @param cfg 结束的活动的配置
	 */
	protected void activityEndHandler(ActivityCfgIF cfg){
		//如果有活动结束的统一处理，就需要重载这个方法
	}
	
	/**
	 * 用于和数据库交互，以及和玩家之间同步数据的holder
	 * @return
	 */
	protected abstract UserActivityChecker<T> getHolder();
}
