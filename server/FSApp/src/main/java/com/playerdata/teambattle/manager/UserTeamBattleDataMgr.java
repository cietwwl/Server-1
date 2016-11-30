package com.playerdata.teambattle.manager;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.teambattle.cfg.TeamCfg;
import com.playerdata.teambattle.cfg.TeamCfgDAO;
import com.playerdata.teambattle.data.TBTeamItem;
import com.playerdata.teambattle.data.TBTeamItemHolder;
import com.playerdata.teambattle.data.TeamHardInfo;
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
			if(null != self){
				if(self.getState().equals(TBMemberState.Ready) || self.getState().equals(TBMemberState.Fight)){
					teamItem.removeMember(self);
					if(!TBTeamItemMgr.getInstance().removeTeam(teamItem)){
						TBTeamItemHolder.getInstance().synData(teamItem);
					}
				}
			}
		}
		utbData.clearCurrentTeam();
	}
	
	public void dailyReset(Player player){
		UserTeamBattleDataHolder.getInstance().dailyReset(player);
	}
	
	/**
	 * 判断是否还有挑战次数
	 * @param player
	 * @param hardID 副本id
	 * @param isChangeTimes 如果次数足够，是否更改挑战次数
	 * @return
	 */
	public boolean haveFightTimes(Player player, String hardID, boolean isChangeTimes){
		TeamCfg teamCfg = TeamCfgDAO.getInstance().getCfgById(hardID);
		if(null == teamCfg) return false;
		
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		if(null == utbData) return false;
		TeamHardInfo thInfo = utbData.getFinishedHardMap().get(hardID);
		if(null == thInfo) return true;
		return true;
	}
}
