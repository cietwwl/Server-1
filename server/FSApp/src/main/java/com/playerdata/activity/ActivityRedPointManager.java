package com.playerdata.activity;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.activity.countType.ActivityCountTypeMgr;

public class ActivityRedPointManager {

	
	private static ActivityRedPointManager instance = new ActivityRedPointManager();
	
	public ActivityRedPointManager getInstance(){
		return instance;
	}
	
	/**
	 * 
	 * @param strs 客户端通过心跳将玩家操作strs发送过来后,找到对应的活动activity
	 * 
	 */
	public void initHeartBeat(Player player,String strs){
		if(StringUtils.isBlank(strs)){
			return;
		}
		String[] strEnums = strs.split(",");
		for(String str : strEnums){
			ActivityRedPointEnum target = ActivityRedPointEnum.getEnumByCfgId(str);
			if(target == null){
				continue;
			}
			redPoint(player,target);			
		}		
	}
	
	
	/**
	 * 
	 * @param target 通过活动枚举获取对应的holder进行数据处理；
	 */
	private void redPoint(Player player,ActivityRedPointEnum target) {
		if(target.getType()==ActivityTypeEnum.ActivityCountType){
			ActivityCountTypeMgr.getInstance().updateRedPoint(player,target);
		}else if(target.getType()==ActivityTypeEnum.ActivityDailyType){
			
		}else if(target.getType()==ActivityTypeEnum.ActivityRateType){
			
		}else if(target.getType()==ActivityTypeEnum.ActivityTimeCountType){
			
		}else if(target.getType()==ActivityTypeEnum.ActivityVitalyType){
			
		}else if(target.getType()==ActivityTypeEnum.ActivityExchangeType){
			
		}else if(target.getType()==ActivityTypeEnum.ActivityRankType){
			
		}else if(target.getType()==ActivityTypeEnum.ActivityDiscountType){
			
		}else{
			GameLog.error("通用活动红点报错", player.getUserId(), "匹配到的枚举没有进行数据处理", null);
		}
	}	
	
	
}
