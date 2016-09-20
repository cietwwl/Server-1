package com.playerdata.teambattle.manager;

import com.playerdata.Player;
import com.playerdata.teambattle.data.UserTeamBattleDataHolder;

public class UserTeamBattleDataMgr {
	private static UserTeamBattleDataMgr instance = new UserTeamBattleDataMgr();
	
	public static UserTeamBattleDataMgr getInstance() {
		return instance;
	}
	
	public void synData(Player player){
		UserTeamBattleDataHolder.getInstance().synData(player);
		TBTeamItemMgr.getInstance().synData(player);
	}
	
	public void leaveTeam(Player player){
		
	}
}
