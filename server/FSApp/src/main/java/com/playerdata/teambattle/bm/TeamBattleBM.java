package com.playerdata.teambattle.bm;

import com.playerdata.Player;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.teambattle.data.UserTeamBattleData;
import com.playerdata.teambattle.data.UserTeamBattleDataHolder;
import com.playerdata.teambattle.dataForClient.StaticMemberTeamInfo;
import com.playerdata.teambattle.dataForClient.TBArmyHerosInfo;
import com.playerdata.teambattle.manager.UserTeamBattleDataMgr;
import com.rwproto.TeamBattleProto.TBResultType;
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
		UserTeamBattleDataMgr.getInstance().synData(player);
	}

	public void saveTeamInfo(Player player, Builder tbRsp, TBArmyHerosInfo item) {
		ArmyInfoSimple simpleArmy = ArmyInfoHelper.getSimpleInfo(player.getUserId(), item.getMagicID(), item.getHeroIDs());
		if(simpleArmy == null) {
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			return;
		}
		StaticMemberTeamInfo staticMemInfo = new StaticMemberTeamInfo();
		staticMemInfo.setUserID(player.getUserId());
		staticMemInfo.setUserStaticTeam(simpleArmy);
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		utbData.setSelfTeamInfo(staticMemInfo);
		UserTeamBattleDataHolder.getInstance().synData(player);
	}

	public void createTeam(Player player, Builder tbRsp) {
		//判断是否可以创建队伍
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		utbData.clearCurrentTeam();
		
		//utbData.setSelfTeamInfo(staticMemInfo);
		UserTeamBattleDataHolder.getInstance().synData(player);
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
