package com.rwbase.dao.task.pojo;

import com.log.GameLog;
import com.playerdata.activity.timeCardType.ActivityTimeCardTypeMgr;
import com.rwbase.dao.task.DailyStartCondition;

public class DailyTimeCardCondition implements DailyStartCondition {


	private String timeCardTypeCfgId;
	
	private String timeCardTypeSubItemCfgId;
	
	
	public DailyTimeCardCondition(String startConditionText) {
		
		String[] split = startConditionText.split("_");
		
		timeCardTypeCfgId = split[0];
		timeCardTypeSubItemCfgId = split[1];
	}

	@Override
	public boolean isMatchCondition(String userId, int playerLevel, int playerVip) {
		boolean isMatchcondition = false;
		isMatchcondition = istTmeCardOnGoging(userId, playerLevel, playerVip);
//		GameLog.info("月卡判断", userId, "是否成功 = " + isMatchcondition, null);
		return isMatchcondition;
	}

	private boolean istTmeCardOnGoging(String userId, int playerLevel, int playerVip) {
		
		return ActivityTimeCardTypeMgr.getInstance().isTimeCardOnGoing(userId, timeCardTypeCfgId, timeCardTypeSubItemCfgId);
	}
	
	

}
