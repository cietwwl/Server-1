package com.playerdata.groupFightOnline.bm;

import java.util.List;

import com.bm.group.GroupBM;
import com.bm.rank.groupFightOnline.GFGroupBiddingRankMgr;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfgDAO;
import com.playerdata.groupFightOnline.data.GFBiddingItemHolder;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupData;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceData;
import com.playerdata.groupFightOnline.dataForClient.GFResourceInfo;
import com.playerdata.groupFightOnline.dataForRank.GFGroupBiddingItem;
import com.playerdata.groupFightOnline.manager.GFightOnlineGroupMgr;
import com.playerdata.groupFightOnline.manager.GFightOnlineResourceMgr;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwproto.GrouFightOnlineProto.GFResultType;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineRspMsg;

/**
 * 在线帮战，用户竞标阶段管理类
 * @author aken
 *
 */
public class GFightGroupBidBM {

	private static GFightGroupBidBM instance = new GFightGroupBidBM();
	
	public static GFightGroupBidBM getInstance(){
		return instance;
	}
	
	public void synData(Player player, int version){
		GFBiddingItemHolder.getInstance().synAllData(player);
	}
	
	/**
	 * 获取所有资源点的占有信息和状态信息
	 * @param player
	 * @param gfRsp
	 */
	public void getResourceInfo(Player player, GroupFightOnlineRspMsg.Builder gfRsp){
		List<GFightOnlineResourceCfg> resCfgs = GFightOnlineResourceCfgDAO.getInstance().getAllCfg();
		for(GFightOnlineResourceCfg cfg : resCfgs){
			GFightOnlineResourceData resData = GFightOnlineResourceMgr.getInstance().get(cfg.getResID());
			if(resData == null) continue;
			GFResourceInfo resInfo = toClientResourceData(player.getUserId(), resData);
			if(resInfo == null) {
				gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
				return;
			}
			gfRsp.addGfResourceInfo(ClientDataSynMgr.toClientData(resInfo));
		}
		gfRsp.setSystemTime(System.currentTimeMillis());
		gfRsp.setRstType(GFResultType.SUCCESS);
	}
	
	/**
	 * 获取帮派竞标排行榜
	 * @param player
	 * @param gfRsp
	 * @param resourceID
	 */
	public void getGroupBidRank(Player player, GroupFightOnlineRspMsg.Builder gfRsp, int resourceID){
		List<GFGroupBiddingItem> groupBidRank = GFGroupBiddingRankMgr.getGFGroupBidRankList(resourceID);
		if(groupBidRank == null) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("取不到帮派竞标排行榜");
		}
		for(GFGroupBiddingItem item : groupBidRank) 
			gfRsp.addRankData(ClientDataSynMgr.toClientData(item));
		gfRsp.setRstType(GFResultType.SUCCESS);
	}
	
	/**
	 * 帮派竞标
	 * @param player
	 * @param gfRsp
	 * @param resourceID
	 * @param bidCount
	 */
	public void groupBidding(Player player, GroupFightOnlineRspMsg.Builder gfRsp, int resourceID, int bidCount){
		GFResultType canBid = GFightConditionJudge.getInstance().canBidForGroup(player, resourceID, bidCount);
		if(canBid != GFResultType.SUCCESS) {
			gfRsp.setRstType(canBid);
			return;
		}
		if(!GFightConditionJudge.getInstance().isBidPeriod(resourceID)) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("不在竞标期间");
			return;
		}
		GFightOnlineGroupData gfGroupData = GFightOnlineGroupMgr.getInstance().getByUser(player);
		if(!GFightConditionJudge.getInstance().isLegalBidCount(resourceID, gfGroupData.getBiddingCount(), bidCount)) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("竞标数量没有达到最小要求");
			return;
		}
		if(gfGroupData.getResourceID() > 0 && gfGroupData.getResourceID() != resourceID) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION); 
			gfRsp.setTipMsg("不能同时竞标两个资源点");
			return;
		}
		gfGroupData.setResourceID(resourceID);
		gfGroupData.setBiddingCount(bidCount);
		gfGroupData.setLastBidTime(System.currentTimeMillis());
		GFightOnlineGroupMgr.getInstance().update(player, gfGroupData, true);
		// 排行榜有改变
		List<GFGroupBiddingItem> groupBidRank = GFGroupBiddingRankMgr.getGFGroupBidRankList(resourceID);
		if(groupBidRank == null) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("没取到帮派竞标排行榜");
			return;
		}
		for(GFGroupBiddingItem item : groupBidRank) 
			gfRsp.addRankData(ClientDataSynMgr.toClientData(item));
		gfRsp.setRstType(GFResultType.SUCCESS);
	}
	
	/**
	 * 将服务端存储的资源点信息，转成前端可用的
	 * 实际只是将帮派id转成了帮派基本信息
	 * @param userID
	 * @param resData
	 * @return
	 */
	private GFResourceInfo toClientResourceData(String userID, GFightOnlineResourceData resData){
		GFResourceInfo resInfo = new GFResourceInfo();
		resInfo.setResourceID(resData.getResourceID());
		resInfo.setState(resData.getState());
		if(resData.getOwnerGroupID() == null || resData.getOwnerGroupID().isEmpty()) return resInfo;
		Group gp = GroupBM.get(resData.getOwnerGroupID());
		if(gp == null) return resInfo;
		GroupBaseDataIF groupData = gp.getGroupBaseDataMgr().getGroupData();
		if(groupData == null) {
			GameLog.error(LogModule.GroupFightOnline.getName(), userID, String.format("getResourceInfo, 占领资源点[%s]的帮派[%s]信息不存在", resData.getResourceID(), resData.getOwnerGroupID()));
		}
		String leaderName = GroupBM.get(resData.getOwnerGroupID()).getGroupMemberMgr().getGroupLeader().getName();
		
		GFGroupBiddingItem groupSimple = new GFGroupBiddingItem();
		groupSimple.setGroupID(groupData.getGroupId());
		groupSimple.setIconID(groupData.getIconId());
		groupSimple.setGroupName(groupData.getGroupName());
		groupSimple.setLeaderName(leaderName);
		resInfo.setGroupInfo(groupSimple);
		
		return resInfo;
	}
	
	public void removeItemsOnResource(int resourceID){		
		GFBiddingItemHolder.getInstance().removeItemsOnResource(resourceID);
	}
}
