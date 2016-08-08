package com.playerdata.activity.rateType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.ActivityRedPointEnum;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeItemHolder;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfg;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfgDAO;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeStartAndEndHourHelper;
import com.playerdata.activity.rateType.data.ActivityRateTypeItem;
import com.playerdata.activity.rateType.data.ActivityRateTypeItemHolder;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.copy.cfg.CopyCfg;

public class ActivityRateTypeMgr implements ActivityRedPointUpdate{

	private static ActivityRateTypeMgr instance = new ActivityRateTypeMgr();

	public static ActivityRateTypeMgr getInstance() {
		return instance;
	}

	public void synData(Player player) {
		ActivityRateTypeItemHolder.getInstance().synAllData(player);
	}

	

	



	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkVersion(player);
		checkClose(player);

	}
	
	

	/**
	 * 
	 * @param player  同个类型活动同时开启两个会导致add不了新的活动，风险低，需检测
	 */
	private void checkNewOpen(Player player) {
		ActivityRateTypeItemHolder dataHolder = ActivityRateTypeItemHolder
				.getInstance();
		List<ActivityRateTypeCfg> allCfgList = ActivityRateTypeCfgDAO
				.getInstance().getAllCfg();
		for (ActivityRateTypeCfg activityRateTypeCfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (!isOpen(activityRateTypeCfg)) {
				// 活动未开启
				continue;
			}
			ActivityRateTypeEnum typeEnum = ActivityRateTypeEnum
					.getById(activityRateTypeCfg.getEnumId());
			if (typeEnum == null) {
				// 枚举没有配置
				continue;
			}
			ActivityRateTypeItem targetItem = dataHolder.getItem(
					player.getUserId(), typeEnum);// 已在之前生成数据的活动
			if(targetItem != null){
				continue;
			}
			targetItem = ActivityRateTypeCfgDAO.getInstance().newItem(
					player, activityRateTypeCfg);// 生成新开启活动的数据
			if (targetItem != null) {
				dataHolder.addItem(player, targetItem);
			}
		}
	}
	
	private void checkVersion(Player player) {
		ActivityRateTypeItemHolder dataHolder = ActivityRateTypeItemHolder.getInstance();
		List<ActivityRateTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		for(ActivityRateTypeItem item : itemList){
			ActivityRateTypeCfg cfg = ActivityRateTypeCfgDAO.getInstance().getCfgByEnumId(item);
			if(cfg == null){				
				continue;
			}
			if (!StringUtils.equals(item.getVersion(),cfg.getVersion())) {
				item.reset(cfg);
			}
			item.setClosed(false);
			dataHolder.updateItem(player, item);			
		}			
	}
	
	
	public boolean isOpen(ActivityRateTypeCfg ActivityRateTypeCfg) {
		boolean isopen = false;
		long startTime = ActivityRateTypeCfg.getStartTime();
		long endTime = ActivityRateTypeCfg.getEndTime();
		long currentTime = System.currentTimeMillis();
		isopen = currentTime < endTime && currentTime > startTime ? true
				: false;

		if (isopen) {
			int hour = DateUtils.getCurrentHour();
			for (ActivityRateTypeStartAndEndHourHelper timebyhour : ActivityRateTypeCfg
					.getStartAndEnd()) {
				isopen = hour >= timebyhour.getStarthour()
						&& hour < timebyhour.getEndhour() ? true : false;
				if (isopen) {
					break;
				}
			}
		}
		return isopen;
	}
	
	private void checkClose(Player player) {
		ActivityRateTypeItemHolder dataHolder = ActivityRateTypeItemHolder
				.getInstance();
		List<ActivityRateTypeItem> itemList = dataHolder.getItemList(player
				.getUserId());
		for (ActivityRateTypeItem activityRateTypeItem : itemList) {// 每种活动
			if (isClose(activityRateTypeItem)&&!activityRateTypeItem.isClosed()) {
				activityRateTypeItem.setClosed(true);
				activityRateTypeItem.setTouchRedPoint(true);
				dataHolder.updateItem(player, activityRateTypeItem);
			}
		}
	}

	private boolean isClose(ActivityRateTypeItem ActivityRateTypeItem) {
		boolean isclose = false;
		ActivityRateTypeCfg cfgById = ActivityRateTypeCfgDAO.getInstance()
				.getCfgById(ActivityRateTypeItem.getCfgId());
		if(cfgById == null){
			GameLog.error(LogModule.ComActivityRate, null, "通用活动找不到配置文件", null);
			return true;
		}
		
		long endTime = cfgById.getEndTime();
		long currentTime = System.currentTimeMillis();
		long startTime = cfgById.getStartTime();
		isclose = currentTime > endTime ? true : false;
		if (!isclose) {
			isclose = currentTime < startTime ? true : false;
		}

		if (!isclose) {// 活动期间的小时区间是否开启
			int hour = DateUtils.getCurrentHour();
			for (ActivityRateTypeStartAndEndHourHelper timebyhour : cfgById
					.getStartAndEnd()) {
				isclose = hour >= timebyhour.getStarthour()
						&& hour < timebyhour.getEndhour() ? false : true;
				if (!isclose) {
					break;
				}
			}
		}
		return isclose;
	}	
	
	/**
	 * 
	 * @param copyCfg  副本
	 * @param player
	 * @param eSpecialItemIDUserInfo  传入的战斗结果数据对象
	 * 此方法用于站前将结算双倍金币经验等信息发给客户端显示
	 */
	public void setEspecialItemidlis(CopyCfg copyCfg,Player player,eSpecialItemIDUserInfo eSpecialItemIDUserInfo){
		Map<Integer, Integer> map = ActivityRateTypeMgr.getInstance().getEspecialItemtypeAndEspecialWithTime(player, copyCfg.getLevelType());		
	
		int multiplePlayerExp = 1 + ActivityRateTypeMgr.getInstance().getMultiple(map, eSpecialItemId.PlayerExp.getValue());
		int multipleCoin = 1 + ActivityRateTypeMgr.getInstance().getMultiple(map, eSpecialItemId.Coin.getValue());		
		getesESpecialItemIDUserInfo(eSpecialItemIDUserInfo,copyCfg.getPlayerExp()*multiplePlayerExp,0);		
		getesESpecialItemIDUserInfo(eSpecialItemIDUserInfo,0,copyCfg.getCoin()*multipleCoin);
	}
	
	/**
	 * 传入副本类型，根据类型，是否开启获得当前对应副本的“产出类型→产出倍数”的映射返回；
	 */
	public Map<Integer, Integer> getEspecialItemtypeAndEspecialWithTime(Player player,int copyType){
		Map<Integer, Integer> especialItemtypeAndEspecialWithTime = new HashMap<Integer, Integer>();
		List<ActivityRateTypeCfg> cfgList = ActivityRateTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityRateTypeCfg cfg : cfgList){
			Map<Integer, List<Integer>> map = cfg.getCopyTypeMap();
			if(map.get(copyType)== null){
				continue;
			}
			//当前玩家通关的副本类型在这个活动里有对应的双倍奖励
			ActivityRateTypeEnum eNum = ActivityRateTypeEnum.getById(cfg.getEnumId());
			if(eNum == null){
				GameLog.error(LogModule.ComActivityRate, player.getUserId(), "配置活动有某副本双倍数据，代码无枚举", null);
				continue;
			}
			if(!ActivityRateTypeMgr.getInstance().isActivityOnGoing(player, cfg)){
				continue;
			}
			List<Integer> especials = map.get(copyType);
			for(Integer especial : especials){
				if(especialItemtypeAndEspecialWithTime.containsKey(especial)){
					int old = especialItemtypeAndEspecialWithTime.get(especial);
					especialItemtypeAndEspecialWithTime.put(especial, old + cfg.getMultiple()-1);
				}else{
					especialItemtypeAndEspecialWithTime.put(especial, cfg.getMultiple()-1);//一个活动对一个特定副本的某种产出的若干倍翻倍
				}
			}			
		}		
		return especialItemtypeAndEspecialWithTime;
	}
	
	/**判断是否活动开启以及玩家是否满足活动需求等级*/
	public boolean isActivityOnGoing(Player player,
			ActivityRateTypeCfg cfg) {
				ActivityRateTypeItemHolder dataHolder = ActivityRateTypeItemHolder
				.getInstance();		
		if(cfg == null){			
			return false;
		}				
		{
			ActivityRateTypeItem targetItem = dataHolder.getItem(player.getUserId(), ActivityRateTypeEnum.getById(cfg.getEnumId()));// 已在之前生成数据的活动
			if(targetItem == null){
				checkNewOpen(player);				
				return false;
			}
			return !ActivityRateTypeMgr.getInstance().isClose(targetItem)&&player.getLevel() >= cfg.getLevelLimit();			
		}
	}
	
	public int getMultiple(Map<Integer, Integer> map ,int especial){
		int multiple = 0;
		if(map.containsKey(especial)){
			multiple = map.get(especial);
		}
		return multiple;
	}
	
	/** 通用活动三可能扩展的双倍需要发送给客户端显示的在此处理;只能存在一种枚举,需要双加的额外添组合 */
	public eSpecialItemIDUserInfo getesESpecialItemIDUserInfo(eSpecialItemIDUserInfo eSpecialItemIDUserInfo, int expvalue,int coinvalue) {
		if(expvalue != 0){
			eSpecialItemIDUserInfo.setPlayerExp(expvalue);
		}
		if(coinvalue != 0){
			eSpecialItemIDUserInfo.setCoin(coinvalue);
		}
		return eSpecialItemIDUserInfo;
	}
	
	@Override
	public void updateRedPoint(Player player, ActivityRedPointEnum eNum) {
		ActivityRateTypeItemHolder activityCountTypeItemHolder = new ActivityRateTypeItemHolder();
		ActivityRateTypeEnum rateEnum = ActivityRateTypeEnum.getById(eNum.getCfgId());
		if(rateEnum == null){
			GameLog.error(LogModule.ComActivityRate, player.getUserId(), "心跳传入id获得的页签枚举无法找到活动枚举", null);
			return;
		}
		ActivityRateTypeItem dataItem = activityCountTypeItemHolder.getItem(player.getUserId(),rateEnum);
		if(dataItem == null){
			GameLog.error(LogModule.ComActivityRate, player.getUserId(), "心跳传入id获得的页签枚举无法找到活动数据", null);
			return;
		}
		if(!dataItem.isTouchRedPoint()){
			dataItem.setTouchRedPoint(true);
			activityCountTypeItemHolder.updateItem(player, dataItem);
		}		
	}
}
