package com.playerdata.groupFightOnline.bm;

import java.util.List;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.data.GFDefendArmyItem;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupData;
import com.playerdata.groupFightOnline.data.version.GFightDataVersion;
import com.playerdata.groupFightOnline.dataException.GFArmyDataException;
import com.playerdata.groupFightOnline.dataForClient.DefendArmyHerosInfo;
import com.playerdata.groupFightOnline.manager.GFDefendArmyMgr;
import com.playerdata.groupFightOnline.manager.GFightOnlineGroupMgr;
import com.rw.service.group.helper.GroupHelper;
import com.rwproto.GrouFightOnlineProto.GFResultType;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineRspMsg;

/**
 * 在线帮战，准备阶段管理类
 * @author aken
 *
 */
public class GFightPrepareBM {
	
	private static class InstanceHolder{
		private static GFightPrepareBM instance = new GFightPrepareBM();
	}
	
	public static GFightPrepareBM getInstance(){
		return InstanceHolder.instance;
	}
	
//	/**
//	 * 查看某个帮派所有防守队伍的简要信息
//	 * @param player
//	 * @param gfRsp
//	 * @param groupID
//	 * @param version
//	 */
//	public void getDefenderTeams(Player player, GroupFightOnlineRspMsg.Builder gfRsp, String groupID, int version) {
//		List<GFDefendArmyItem> defenders = GFDefendArmyMgr.getInstance().getGroupItemList(player, groupID, version);
//		for(GFDefendArmyItem defender : defenders) {
//			GFDefendArmySimpleLeader leader = defender.getSimpleLeader();
//			if(leader == null) continue;
//			gfRsp.addDefendArmySimpleLeader(ClientDataSynMgr.toClientData(leader));
//		}
//		gfRsp.setRstType(GFResultType.SUCCESS);
//	}

	/**
	 * 请求同步有更新的帮派信息
	 * @param player
	 * @param gfRsp
	 * @param resourceID
	 * @param dataVersion
	 */
	@Deprecated
	public void synGroupData(Player player, GroupFightOnlineRspMsg.Builder gfRsp, int resourceID, GFightDataVersion dataVersion) {
		// GFightOnlineGroupMgr.getInstance().synAllData(player, resourceID, dataVersion.getOnlineGroupData());
		gfRsp.setRstType(GFResultType.SUCCESS);
	}

	/**
	 * 查看某个防守的队伍的详情（前端的公会防守页面）
	 * 或是查看自己已经锁定的，准备挑战的队伍（前端的进战斗前的页面）
	 * @param player
	 * @param gfRsp
	 * @param groupID
	 * @param viewArmyID
	 */
	public void viewDefenderTeam(Player player, GroupFightOnlineRspMsg.Builder gfRsp, String groupID, String viewArmyID) {
		GFDefendArmyItem defendTeam = GFDefendArmyMgr.getInstance().getItem(groupID, viewArmyID);
		if(defendTeam == null) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("防守队伍数据不存在");
			return;
		}
		gfRsp.setEnimyDefenderDetails(ClientDataSynMgr.toClientData(defendTeam));
		gfRsp.setRstType(GFResultType.SUCCESS);
	}

	/**
	 * 修改自己的防守队伍
	 * 修改之后的队伍结果会同步
	 * 修改之后的个人所属公会的队伍数量信息，也同步
	 * @param player
	 * @param gfRsp
	 * @param items
	 * @param dataVersion
	 */
	public void modifySelfDefender(Player player, GroupFightOnlineRspMsg.Builder gfRsp, List<DefendArmyHerosInfo> items, GFightDataVersion dataVersion) {
		String groupID = GroupHelper.getUserGroupId(player.getUserId());
		if(groupID.isEmpty()) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("没有帮派，不能进行此项操作");
			return;
		}
		GFightOnlineGroupData gfGroupData = GFightOnlineGroupMgr.getInstance().get(groupID);
		if(gfGroupData == null || gfGroupData.getResourceID() == 0) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("帮派数据异常");
			return;
		}
		int resourceID = gfGroupData.getResourceID();
		if(!GFightConditionJudge.getInstance().isPreparePeriod(resourceID)) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("不在备战阶段，不能进行此项操作");
			return;
		}
		try {
			GFDefendArmyMgr.getInstance().resetItems(player, items);
			//同步公会数据
			GFightOnlineGroupMgr.getInstance().synAllData(player, resourceID, dataVersion.getOnlineGroupData());
			gfRsp.setRstType(GFResultType.SUCCESS);
		} catch (GFArmyDataException e) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg(e.getMessage());
			GameLog.error(LogModule.GroupFightOnline.getName(), player.getUserId(), String.format("modifySelfDefender，修改个人防守队伍信息时，数据异常"), e);
		}
	}
}
