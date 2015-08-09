package com.playerdata.activity;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeMgr;
import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.dailyCountType.ActivityDailyTypeMgr;
import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeMgr;
import com.playerdata.activity.exChangeType.ActivityExchangeTypeMgr;
import com.playerdata.activity.rankType.ActivityRankTypeMgr;
import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.activity.redEnvelopeType.ActivityRedEnvelopeTypeMgr;
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
		redPoint(player, str);
		return true;
	}
	
	
	/**
	 * 
	 * @param target 通过活动枚举获取对应的holder进行数据处理；
	 */
	private boolean redPoint(Player player,String str) {
		boolean issucce = false;
//		if(target.getType()==ActivityTypeEnum.ActivityCountType){
//			ActivityCountTypeMgr.getInstance().updateRedPoint(player,target);
//		}else if(target.getType()==ActivityTypeEnum.ActivityDailyType){
//			ActivityDailyTypeMgr.getInstance().updateRedPoint(player, target);
//		}else if(target.getType()==ActivityTypeEnum.ActivityRateType){
//			ActivityRateTypeMgr.getInstance().updateRedPoint(player, target);
//		}else if(target.getType()==ActivityTypeEnum.ActivityTimeCountType){
//			//在线礼包目前不需要记录玩家点击历史
//		}else if(target.getType()==ActivityTypeEnum.ActivityVitalyType){
//			ActivityVitalityTypeMgr.getInstance().updateRedPoint(player, target);
//		}else if(target.getType()==ActivityTypeEnum.ActivityExchangeType){
//			ActivityExchangeTypeMgr.getInstance().updateRedPoint(player, target);
//		}else if(target.getType()==ActivityTypeEnum.ActivityRankType){
//			ActivityRankTypeMgr.getInstance().updateRedPoint(player, target);
//		}else if(target.getType()==ActivityTypeEnum.ActivityDailyDiscountType){
//			ActivityDailyDiscountTypeMgr.getInstance().updateRedPoint(player, target);
//		}else if(target.getType()==ActivityTypeEnum.ActivityRedEnvelope){
//			ActivityRedEnvelopeTypeMgr.getInstance().updateRedPoint(player, target);
		int tmp = Integer.parseInt(str);
		if (tmp < 10000 && tmp > 0) {
			ActivityCountTypeMgr.getInstance().updateRedPoint(player, str);
		} else if (tmp < 20000 && tmp > 10000) {
			ActivityDailyTypeMgr.getInstance().updateRedPoint(player, str);
		} else if (tmp < 30000 && tmp > 20000) {
			ActivityRateTypeMgr.getInstance().updateRedPoint(player, str);
		} else if (tmp < 40000 && tmp > 30000) {
			// 在线礼包目前不需要记录玩家点击历史
		}else if (tmp < 50000 && tmp > 40000) {
			ActivityRedEnvelopeTypeMgr.getInstance().updateRedPoint(player,	str);
		} else if (tmp < 60000 && tmp > 50000) {
			ActivityVitalityTypeMgr.getInstance().updateRedPoint(player, str);
		} else if (tmp < 70000 && tmp > 60000) {
			ActivityExchangeTypeMgr.getInstance().updateRedPoint(player, str);
		} else if (tmp < 80000 && tmp > 70000) {
			ActivityRankTypeMgr.getInstance().updateRedPoint(player, str);
		} else if (tmp < 90000 && tmp > 80000) {
			ActivityDailyDiscountTypeMgr.getInstance().updateRedPoint(player,str);
		}else{
			GameLog.error(LogModule.RedPoint, player.getUserId(), "传来的id异常"+str, null);
			return issucce;
		}
		issucce = true;
		return issucce;
	}	
	
	
}
