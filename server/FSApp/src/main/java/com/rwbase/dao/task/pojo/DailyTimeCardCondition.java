package com.rwbase.dao.task.pojo;

import com.playerdata.Player;
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
	public boolean isMatchCondition(Player player) {
		
		return istTmeCardOnGoging(player);
	}

	private boolean istTmeCardOnGoging(Player player) {
		
		return ActivityTimeCardTypeMgr.getInstance().isTimeCardOnGoing(player, timeCardTypeCfgId, timeCardTypeSubItemCfgId);
	}
	
	

}
