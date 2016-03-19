package com.playerdata.assistant;

import com.playerdata.Player;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;

public class AssistantPowerCheck implements IAssistantCheck{

	private final int POWER_CHECK_VALUE = 5;
	
	@Override
	public AssistantEventID doCheck(Player player) {
		
		if(check(player)){
			return AssistantEventID.GotoCopy;
		}
		return null;
	}
	
	private boolean check(Player player){
		int power = player.getUserGameDataMgr().getPower();
		return power > POWER_CHECK_VALUE;
	}

}
