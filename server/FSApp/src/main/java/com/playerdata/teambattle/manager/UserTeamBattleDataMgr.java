package com.playerdata.teambattle.manager;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.teambattle.data.TBTeamItem;
import com.playerdata.teambattle.data.TBTeamItemHolder;
import com.playerdata.teambattle.data.TeamMember;
import com.playerdata.teambattle.data.UserTeamBattleData;
import com.playerdata.teambattle.data.UserTeamBattleDataHolder;
import com.playerdata.teambattle.enums.TBMemberState;

public class UserTeamBattleDataMgr {
	private static UserTeamBattleDataMgr instance = new UserTeamBattleDataMgr();
	
	public static UserTeamBattleDataMgr getInstance() {
		return instance;
	}
	
	public void synData(Player player){
		UserTeamBattleDataHolder.getInstance().synData(player);
	}
	
	/**
	 * 踢出玩家时使用的同步
	 * @param userID
	 */
	public void synData(String userID){
		Player player = PlayerMgr.getInstance().find(userID);
		if(player == null) return;
		UserTeamBattleDataHolder.getInstance().synData(player);
	}
	
	/**
	 * 离开队伍（主动离开，切换队伍，被踢）
	 * @param userID
	 */
	public void leaveTeam(String userID){
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(userID);
		if(StringUtils.isBlank(utbData.getTeamID())) return;
		TBTeamItem teamItem = TBTeamItemMgr.getInstance().get(utbData.getTeamID());
		if(teamItem != null) {
			TeamMember self = teamItem.findMember(userID);
			if(self != null && self.getState().equals(TBMemberState.Ready)){
				teamItem.removeMember(self);
				if(!TBTeamItemMgr.getInstance().removeTeam(teamItem)){
					TBTeamItemHolder.getInstance().synData(teamItem);
				}
			}
		}
		utbData.clearCurrentTeam();
	}
	
	public void dailyReset(Player player){
		UserTeamBattleDataHolder.getInstance().dailyReset(player);
	}
}
