package com.playerdata.assistant;

import com.playerdata.Player;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;

public interface IAssistantCheck {

	public AssistantEventID doCheck(Player player);
	
}
