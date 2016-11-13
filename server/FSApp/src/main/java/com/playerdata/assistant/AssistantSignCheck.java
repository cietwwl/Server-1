package com.playerdata.assistant;

import com.playerdata.Player;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

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
		
		if(CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.SIGN, player)){
			
			if(!player.getSignMgr().isSignToday()){
				return AssistantEventID.Sign;
			}
			if(player.getSignMgr().checkAchieveSignReward()){
				return AssistantEventID.Sign;
			}
		}
		
		return null;
		
		
		
	}

}
