package com.playerdata.groupFightOnline.manager;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.army.simple.ArmyHeroSimple;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.data.GFDefendArmyItem;
import com.playerdata.groupFightOnline.data.GFDefendArmyItemHolder;
import com.playerdata.groupFightOnline.dataForClient.GFDefendArmySimpleLeader;
import com.rwproto.GrouFightOnlineProto.GFResultType;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineRspMsg;

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
		int currentVersion = GFDefendArmyItemHolder.getInstance().getCurrentVersion();
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
		gfRsp.setServerVersion(currentVersion);
		gfRsp.setRstType(GFResultType.SUCCESS);
	}
}
