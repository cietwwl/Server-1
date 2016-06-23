package com.playerdata.activity.rateType;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfg;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfgDAO;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeStartAndEndHourHelper;
import com.playerdata.activity.rateType.data.ActivityRateTypeItem;
import com.playerdata.activity.rateType.data.ActivityRateTypeItemHolder;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copypve.CopyType;

public class ActivityRateTypeMgr {

	private static ActivityRateTypeMgr instance = new ActivityRateTypeMgr();

	public static ActivityRateTypeMgr getInstance() {
		return instance;
	}

	public void synData(Player player) {
		ActivityRateTypeItemHolder.getInstance().synAllData(player);
	}

	/**判断是否活动开启以及玩家是否满足活动需求等级*/
	public boolean isActivityOnGoing(Player player,
			ActivityRateTypeEnum activityRateTypeEnum) {
				ActivityRateTypeItemHolder dataHolder = ActivityRateTypeItemHolder
				.getInstance();
		if(activityRateTypeEnum == null){
			return false;
		}
		String cfgId = activityRateTypeEnum.getCfgId();
		ActivityRateTypeCfg cfgById = ActivityRateTypeCfgDAO.getInstance().getCfgById(cfgId);		
		if(cfgById == null){
			
			return false;
		}
				
		{
			ActivityRateTypeItem targetItem = dataHolder.getItem(
					player.getUserId(), activityRateTypeEnum);// 已在之前生成数据的活动			
			return targetItem != null && !targetItem.isClosed()&&player.getLevel() >= cfgById.getLevelLimit();			
		}
	}

	public int getmultiple(Player player,
			ActivityRateTypeEnum activityRateTypeEnum) {
		ActivityRateTypeItemHolder dataHolder = ActivityRateTypeItemHolder
				.getInstance();
		if (activityRateTypeEnum == null) {
			GameLog.error("activityratetypemgr", "没有枚举", "获得倍数时无枚举");
			return 1;
		}
		ActivityRateTypeItem targetItem = dataHolder.getItem(
				player.getUserId(), activityRateTypeEnum);// 已在之前生成数据的活动
		if (targetItem == null) {
			GameLog.error("activityratetypemgr", "没有数据 ", "获得倍数时数据库无数据");
			return 1;
		}
		return targetItem.getMultiple();
	}

	public float getRate(ActivityRateTypeEnum activityRateTypeEnum) {
		ActivityRateTypeCfg cfgById = ActivityRateTypeCfgDAO.getInstance()
				.getCfgById(activityRateTypeEnum.getCfgId());
		return cfgById == null ? 1 : cfgById.getRate();
	}

	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkClose(player);

	}

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
					.getById(activityRateTypeCfg.getId());
			if (typeEnum == null) {
				// 枚举没有配置
				continue;
			}
			ActivityRateTypeItem targetItem = dataHolder.getItem(
					player.getUserId(), typeEnum);// 已在之前生成数据的活动
			if (targetItem == null) {
				targetItem = ActivityRateTypeCfgDAO.getInstance().newItem(
						player, typeEnum);// 生成新开启活动的数据
				if (targetItem != null) {
					dataHolder.addItem(player, targetItem);
				}
			} else {
				if (!StringUtils.equals(targetItem.getVersion(),
						activityRateTypeCfg.getVersion())) {
					targetItem.setVersion(activityRateTypeCfg.getVersion());
				}
				targetItem.setClosed(false);
				dataHolder.updateItem(player, targetItem);
			}

		}
	}

	private void checkClose(Player player) {
		ActivityRateTypeItemHolder dataHolder = ActivityRateTypeItemHolder
				.getInstance();
		List<ActivityRateTypeItem> itemList = dataHolder.getItemList(player
				.getUserId());

		for (ActivityRateTypeItem activityRateTypeItem : itemList) {// 每种活动
			if (isClose(activityRateTypeItem)) {
				activityRateTypeItem.setClosed(true);
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

	private boolean isOpen(ActivityRateTypeCfg ActivityRateTypeCfg) {
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

	/** 通用活动三可能扩展的双倍需要发送给客户端显示的在此处理;只能存在一种枚举,需要双加的额外添组合 */
	public eSpecialItemIDUserInfo getesESpecialItemIDUserInfo(
			ActivityRateTypeEnum activityRateEnum,
			eSpecialItemIDUserInfo eSpecialItemIDUserInfo, int expvalue,
			int coinvalue) {
		if (activityRateEnum == null) {
			return null;
		}

		switch (activityRateEnum) {
		case Normal_copy_EXP_DOUBLE:
		case ELITE_copy_EXP_DOUBLE:
			eSpecialItemIDUserInfo.setPlayerExp(expvalue);
			break;
		case TOWER_DOUBLE:
			eSpecialItemIDUserInfo.setCoin(coinvalue);
			break;
		default:
			break;
		}
		return eSpecialItemIDUserInfo;
	}
	
	/**
	 * 
	 * @param copyCfg  副本
	 * @param player
	 * @param eSpecialItemIDUserInfo  传入的战斗结果数据对象
	 * 此方法用于站前将结算双倍金币经验等信息发给客户端显示
	 */
	public void setEspecialItemidlis(CopyCfg copyCfg,Player player,eSpecialItemIDUserInfo eSpecialItemIDUserInfo){
		ActivityRateTypeEnum activityRateTypeEnum = ActivityRateTypeEnum.getByCopyTypeAndRewardsType(copyCfg.getLevelType(), 1);
		boolean isRateOpen = ActivityRateTypeMgr.getInstance().isActivityOnGoing(player, activityRateTypeEnum);
		int multiple = isRateOpen?ActivityRateTypeMgr.getInstance().getmultiple(player, activityRateTypeEnum):1; 
		getesESpecialItemIDUserInfo(activityRateTypeEnum, eSpecialItemIDUserInfo,copyCfg.getPlayerExp()*multiple,0);
		
		ActivityRateTypeEnum activityRateTypeEnumcoin = ActivityRateTypeEnum.getByCopyTypeAndRewardsType(copyCfg.getLevelType(), 2);
		boolean isRateOpencoin = ActivityRateTypeMgr.getInstance().isActivityOnGoing(player, activityRateTypeEnumcoin);
		int multiplecoin = isRateOpencoin?ActivityRateTypeMgr.getInstance().getmultiple(player, activityRateTypeEnumcoin):1; 		
		getesESpecialItemIDUserInfo(activityRateTypeEnumcoin, eSpecialItemIDUserInfo,0,copyCfg.getCoin()*multiplecoin);
	}
	
	/**
	 * 核实与当前副本相关的活动是否存在，活动是否开启，以及返回倍数
	 * @param copyType 战斗类型
	 * @param doubleType 奖励双倍的类型 
	 * @return  倍数
	 * 此方法用于战后结算后台增加金币经验数据，以及战前生成物品道具
	 */
	public int  checkEnumIsExistAndActivityIsOpen(Player player,int copyType,int doubleType){
		int multiple = 1;
		ActivityRateTypeEnum activityRateTypeEnum = ActivityRateTypeEnum.getByCopyTypeAndRewardsType(copyType, doubleType);
		boolean isRateOpen = ActivityRateTypeMgr.getInstance().isActivityOnGoing(player, activityRateTypeEnum);		
		multiple = isRateOpen?ActivityRateTypeMgr.getInstance().getmultiple(player, activityRateTypeEnum):1;		
		return multiple;
	}
	
	
	
}
