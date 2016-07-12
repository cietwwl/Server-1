package com.playerdata.activity;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeMgr;
import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.dailyCountType.ActivityDailyTypeMgr;
import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeMgr;
import com.playerdata.activity.exChangeType.ActivityExchangeTypeMgr;
import com.playerdata.activity.rankType.ActivityRankTypeMgr;
import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.activity.timeCountType.ActivityTimeCountTypeMgr;

public class ActivityRedPointManager {

	
	private static ActivityRedPointManager instance = new ActivityRedPointManager();
	
	public static ActivityRedPointManager getInstance(){
		return instance;
	}
	
	/**
	 * 
	 * @param strs 客户端将玩家操作strs发送过来后,找到对应的活动activity
	 * 
	 */
	public boolean init(Player player, String str) {
		if (StringUtils.isBlank(str)) {
			return false;
		}
		ActivityRedPointEnum target = ActivityRedPointEnum.getEnumByCfgId(str);
		if (target == null) {
			return false;
		}
		redPoint(player, target);
		return true;
	}
	
	
	/**
	 * 
	 * @param target 通过活动枚举获取对应的holder进行数据处理；
	 */
	private boolean redPoint(Player player,ActivityRedPointEnum target) {
		boolean issucce = false;
		if(target.getType()==ActivityTypeEnum.ActivityCountType){
			ActivityCountTypeMgr.getInstance().updateRedPoint(player,target);
		}else if(target.getType()==ActivityTypeEnum.ActivityDailyType){
			ActivityDailyTypeMgr.getInstance().updateRedPoint(player, target);
		}else if(target.getType()==ActivityTypeEnum.ActivityRateType){
			ActivityRateTypeMgr.getInstance().updateRedPoint(player, target);
		}else if(target.getType()==ActivityTypeEnum.ActivityTimeCountType){
			//在线礼包目前不需要记录玩家点击历史
		}else if(target.getType()==ActivityTypeEnum.ActivityVitalyType){
			ActivityVitalityTypeMgr.getInstance().updateRedPoint(player, target);
		}else if(target.getType()==ActivityTypeEnum.ActivityExchangeType){
			ActivityExchangeTypeMgr.getInstance().updateRedPoint(player, target);
		}else if(target.getType()==ActivityTypeEnum.ActivityRankType){
			ActivityRankTypeMgr.getInstance().updateRedPoint(player, target);
		}else if(target.getType()==ActivityTypeEnum.ActivityDailyDiscountType){
			ActivityDailyDiscountTypeMgr.getInstance().updateRedPoint(player, target);
		}else{
			GameLog.error("通用活动红点报错", player.getUserId(), "匹配到的枚举没有进行数据处理", null);
			return issucce;
		}
		issucce = true;
		return issucce;
	}	
	
	
}
