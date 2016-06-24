package com.playerdata.groupFightOnline.manager;

import java.util.List;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.army.simple.ArmyHeroSimple;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.data.GFDefendArmyItem;
import com.playerdata.groupFightOnline.data.GFDefendArmyItemHolder;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupData;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupHolder;
import com.playerdata.groupFightOnline.data.version.GFightDataVersion;
import com.playerdata.groupFightOnline.dataException.GFArmyDataException;
import com.playerdata.groupFightOnline.dataForClient.DefendArmyHerosInfo;
import com.playerdata.groupFightOnline.dataForClient.GFDefendArmySimpleLeader;
import com.rw.service.group.helper.GroupHelper;
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

	public void synGroupData(Player player, GroupFightOnlineRspMsg.Builder gfRsp, int resourceID, GFightDataVersion dataVersion) {
		GFightOnlineGroupHolder.getInstance().synAllData(player, resourceID, dataVersion.getOnlineGroupData());
		gfRsp.setRstType(GFResultType.SUCCESS);
	}

	public void viewDefenderTeam(Player player, GroupFightOnlineRspMsg.Builder gfRsp, String groupID, String viewArmyID) {
		GFDefendArmyItem defendTeam = GFDefendArmyItemHolder.getInstance().getItem(groupID, viewArmyID);
		if(defendTeam == null) {
			gfRsp.setRstType(GFResultType.DATA_ERROR);
			return;
		}
		gfRsp.setEnimyDefenderDetails(ClientDataSynMgr.toClientData(defendTeam));
		gfRsp.setRstType(GFResultType.SUCCESS);
	}

	public void modifySelfDefender(Player player, GroupFightOnlineRspMsg.Builder gfRsp, List<DefendArmyHerosInfo> items, GFightDataVersion dataVersion) {
		try {
			GFDefendArmyItemHolder.getInstance().resetItems(player, items);
			//同步公会数据
			String groupID = GroupHelper.getUserGroupId(player.getUserId());
			GFightOnlineGroupData groupData = GFightOnlineGroupHolder.getInstance().get(groupID);
			int resourceID = groupData.getResourceID();
			GFightOnlineGroupHolder.getInstance().synAllData(player, resourceID, dataVersion.getOnlineGroupData());
			gfRsp.setRstType(GFResultType.SUCCESS);
		} catch (GFArmyDataException e) {
			gfRsp.setRstType(GFResultType.DATA_ERROR);
			GameLog.error(LogModule.GroupFightOnline.getName(), player.getUserId(), String.format("modifySelfDefender，修改个人防守队伍信息时，数据异常"), e);
		}
	}
}
