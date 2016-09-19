package com.playerdata.teambattle.manager;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.teambattle.data.TBTeamItemHolder;
import com.playerdata.teambattle.data.UserTeamBattleData;
import com.playerdata.teambattle.data.UserTeamBattleDataHolder;

public class TBTeamItemMgr{
	
	private static TBTeamItemMgr instance = new TBTeamItemMgr();
	
	public static TBTeamItemMgr getInstance(){
		return instance;
	}
	
	public void synData(Player player){
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		if(StringUtils.isNotBlank(utbData.getTeamID())){
			TBTeamItemHolder.getInstance().synData(player, utbData.getTeamID());
		}
	}
	
	public void createNewTeam(Player player, String hardID){
		
	}
	
	public void joinTeam(Player player, String hardID){
		
	}
}
