package com.playerdata.assistant;

import com.playerdata.Player;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;

public class AssistantGambleCheck implements IAssistantCheck{

	@Override
	public AssistantEventID doCheck(Player player) {
		
		if(player.getGambleMgr().getHasFree()){
			return AssistantEventID.GetItem;
		}
		return null;
		
	}

}
