package com.playerdata.groupFightOnline.manager;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.simple.ArmyHeroSimple;
import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.data.GFDefendArmyItem;
import com.playerdata.groupFightOnline.data.GFDefendArmyItemHolder;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupHolder;
import com.playerdata.groupFightOnline.dataForClient.GFDefendArmySimpleLeader;
import com.rwproto.GrouFightOnlineProto.GFResultType;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineRspMsg;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineRspMsg.Builder;

/**
 * 在线帮战，准备阶段管理类
 * @author aken
 *
 */
public class GFightPrepareMgr {
	
	private static class InstanceHolder{
		private static GFightPrepareMgr instance = new GFightPrepareMgr();
	}
	
	public static GFightPrepareMgr getInstance(){
		return InstanceHolder.instance;
	}
	
	private GFightPrepareMgr() { }
	
	public void getDefenderTeams(Player player, GroupFightOnlineRspMsg.Builder gfRsp, String groupID, int version) {
		List<GFDefendArmyItem> defenders = GFDefendArmyItemHolder.getInstance().getGroupItemList(player, groupID, version);
		for(GFDefendArmyItem defender : defenders) {
			ArmyHeroSimple heroSimple = defender.getSimpleArmy().getHeroList().get(0);
			if(heroSimple == null) continue;
			GFDefendArmySimpleLeader leader = new GFDefendArmySimpleLeader();
			leader.setArmyID(defender.getArmyID());
			leader.setGroupID(defender.getGroupID());
			leader.setState(defender.getState());
			leader.setLevel(heroSimple.getLevel());
			leader.setModeId(heroSimple.getModeId());
			leader.setQualityId(heroSimple.getQualityId());
			leader.setStarLevel(heroSimple.getStarLevel());
			gfRsp.addDefendArmySimpleLeader(ClientDataSynMgr.toClientData(leader));
		}
		gfRsp.setRstType(GFResultType.SUCCESS);
	}

	public void synGroupData(Player player, GroupFightOnlineRspMsg.Builder gfRsp, int resourceID, int dataVersion) {
		GFightOnlineGroupHolder.getInstance().synAllData(player, resourceID, dataVersion);
		gfRsp.setRstType(GFResultType.SUCCESS);
	}

	public void viewDefenderTeam(Player player, GroupFightOnlineRspMsg.Builder gfRsp, String groupID, String viewTeamID) {
		GFDefendArmyItem defendTeam = GFDefendArmyItemHolder.getInstance().getItem(groupID, viewTeamID);
		if(defendTeam == null) {
			gfRsp.setRstType(GFResultType.DATA_ERROR);
			return;
		}
		gfRsp.setEnimyDefenderDetails(ClientDataSynMgr.toClientData(defendTeam));
		gfRsp.setRstType(GFResultType.SUCCESS);
	}

	public void modifySelfDefender(Player player, Builder gfRsp, List<String> heroIDList, String teamID) {
		ArmyInfoSimple simpleArmy = ArmyInfoHelper.getSimpleInfo(player.getUserId(), heroIDList);
		if(simpleArmy == null) {
			gfRsp.setRstType(GFResultType.DATA_ERROR);
			return;
		}
		GFDefendArmyItemHolder.getInstance().
		GFDefendArmyItemHolder.getInstance().resetItems(player, items);
	}
}
