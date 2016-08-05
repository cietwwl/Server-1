package com.playerdata.assistant;

import com.playerdata.Player;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;

public class AssistantSignCheck extends DefaultAssistantChecker {

	
//    GetItem,
//    HeroAdvance,
//    UpdateSkill,
//    Invaild,
//    Sign,
//    DailyQuest,
//    HeroLevelUp,
//    GotoCopy,
//    TakeUpEquip,
	
	@Override
	public AssistantEventID doCheck(Player player) {
		super.doCheck(player);
		if(!player.getSignMgr().isSignToday()){
			return AssistantEventID.Sign;
		}
		if(player.getSignMgr().checkAchieveSignReward()){
			return AssistantEventID.Sign;
		}
		return null;
		
		
		
	}

}
