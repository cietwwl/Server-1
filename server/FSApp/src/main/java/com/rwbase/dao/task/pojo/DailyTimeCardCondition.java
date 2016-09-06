package com.rwbase.dao.task.pojo;

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
		
		return istTmeCardOnGoging(userId, playerLevel, playerVip);
	}

	private boolean istTmeCardOnGoging(String userId, int playerLevel, int playerVip) {
		
		return ActivityTimeCardTypeMgr.getInstance().isTimeCardOnGoing(userId, timeCardTypeCfgId, timeCardTypeSubItemCfgId);
	}
	
	

}
