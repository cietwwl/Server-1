package com.playerdata.teambattle.bm;

import com.playerdata.Player;
import com.rwproto.TeamBattleProto.TeamBattleRspMsg.Builder;


/**
 * 组队战
 * @author aken
 *
 */
public class TeamBattleBM {
	
	private static class InstanceHolder{
		private static TeamBattleBM instance = new TeamBattleBM();
		private static TeamBattleJudger tbJudger = TeamBattleJudger.getInstance();
	}
	
	public static TeamBattleBM getInstance(){
		return InstanceHolder.instance;
	}

	public void synTeamBattle(Player player, Builder tbRsp) {
		
	}

	public void saveTeamInfo(Player player, Builder tbRsp) {
		
	}

	public void createTeam(Player player, Builder tbRsp) {
		
	}

	public void joinTeam(Player player, Builder tbRsp) {
		
	}

	public void acceptInvite(Player player, Builder tbRsp) {
		
	}

	public void setTeamFreeJion(Player player, Builder tbRsp) {
		
	}

	public void kickoffMember(Player player, Builder tbRsp) {
		
	}

	public void invitePlayer(Player player, Builder tbRsp) {
		
	}

	public void startFight(Player player, Builder tbRsp) {
		
	}

	public void informFightResult(Player player, Builder tbRsp) {
		
	}
}
