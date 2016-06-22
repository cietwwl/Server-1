package com.playerdata.groupFightOnline.manager;

import java.util.List;

import com.bm.group.GroupBM;
import com.bm.rank.groupFightOnline.GFGroupBiddingRankMgr;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceDAO;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupData;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupHolder;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceData;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceHolder;
import com.playerdata.groupFightOnline.dataForClient.GFResourceInfo;
import com.playerdata.groupFightOnline.dataForRank.GFGroupBiddingItem;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwproto.GrouFightOnlineProto.GFResultType;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineRspMsg;

/**
 * 在线帮战，用户竞标阶段管理类
 * @author aken
 *
 */
public class GFightGroupBidMgr {
	
	private static class InstanceHolder{
		private static GFightGroupBidMgr instance = new GFightGroupBidMgr();
	}
	
	public static GFightGroupBidMgr getInstance(){
		return InstanceHolder.instance;
	}
	
	private GFightGroupBidMgr() { }
	
	public void getResourceInfo(Player player, GroupFightOnlineRspMsg.Builder gfRsp){
		gfRsp.setSystemTime(System.currentTimeMillis());
		List<GFightOnlineResourceCfg> resCfgs = GFightOnlineResourceDAO.getInstance().getAllCfg();
		for(GFightOnlineResourceCfg cfg : resCfgs){
			GFightOnlineResourceData resData = GFightOnlineResourceHolder.getInstance().get(String.valueOf(cfg.getResID()));
			if(resData == null) continue;
			GFResourceInfo resInfo = toClientResourceData(player.getUserId(), resData);
			if(resInfo == null) {
				gfRsp.setRstType(GFResultType.DATA_ERROR);
				return;
			}
			gfRsp.addGfResourceInfo(ClientDataSynMgr.toClientData(resInfo));
		}
		gfRsp.setRstType(GFResultType.SUCCESS);
	}
	
	public void getGroupBidRank(Player player, GroupFightOnlineRspMsg.Builder gfRsp, int resourceID){
		List<GFGroupBiddingItem> groupBidRank = GFGroupBiddingRankMgr.getGFGroupBidRankList(resourceID);
		if(groupBidRank == null) gfRsp.setRstType(GFResultType.DATA_ERROR);
		for(GFGroupBiddingItem item : groupBidRank) 
			gfRsp.addRankData(ClientDataSynMgr.toClientData(item));
		gfRsp.setRstType(GFResultType.SUCCESS);
	}
	
	public void groupBidding(Player player, GroupFightOnlineRspMsg.Builder gfRsp, int resourceID, int bidCount){
		GFResultType canBid = GFightConditionJudge.getInstance().canBidForGroup(player, resourceID, bidCount);
		if(canBid != GFResultType.SUCCESS) {
			gfRsp.setRstType(canBid);
			return;
		}
		if(!GFightConditionJudge.getInstance().isBidPeriod(resourceID)) {
			gfRsp.setRstType(GFResultType.NOT_IN_OPEN_TIME);
			return;
		}
		GFightOnlineGroupData gfGroupData = GFightOnlineGroupHolder.getInstance().getByUser(player);
		gfGroupData.setBiddingCount(biddingCount);
	}
	
	private GFResourceInfo toClientResourceData(String userID, GFightOnlineResourceData resData){
		GroupBaseDataIF groupData = GroupBM.get(resData.getOwnerGroupID()).getGroupBaseDataMgr().getGroupData();
		if(groupData == null) {
			GameLog.error(LogModule.GroupFightOnline.getName(), userID, String.format("getResourceInfo, 占领资源点[%s]的帮派[%s]信息不存在", resData.getResourceID(), resData.getOwnerGroupID()));
			return null;
		}
		String leaderName = GroupBM.get(resData.getOwnerGroupID()).getGroupMemberMgr().getGroupLeader().getName();
		GFGroupBiddingItem groupSimple = new GFGroupBiddingItem();
		groupSimple.setGroupID(groupData.getGroupId());
		groupSimple.setIconID(groupData.getIconId());
		groupSimple.setGroupName(groupData.getGroupName());
		groupSimple.setLeaderName(leaderName);
		GFResourceInfo resInfo = new GFResourceInfo();
		resInfo.setResourceID(resData.getResourceID());
		resInfo.setGroupInfo(groupSimple);
		return resInfo;
	}
}
