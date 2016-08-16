package com.playerdata.assistant;

import com.playerdata.Player;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;

public class AssistantGambleCheck extends DefaultAssistantChecker {

	@Override
	public AssistantEventID doCheck(Player player) {
		super.doCheck(player);
		if(player.getGambleMgr().getHasFree()){
			return AssistantEventID.GetItem;
		}
		return null;
		
	}

}
