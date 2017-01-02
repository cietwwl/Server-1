package com.playerdata.groupFightOnline.bm;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bm.rank.groupFightOnline.GFGroupBiddingRankMgr;
import com.bm.rank.groupFightOnline.GFOnlineHurtRankMgr;
import com.bm.rank.groupFightOnline.GFOnlineKillRankMgr;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfgDAO;
import com.playerdata.groupFightOnline.data.GFBiddingItemHolder;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupData;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceData;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceHolder;
import com.playerdata.groupFightOnline.dataForRank.GFGroupBiddingItem;
import com.playerdata.groupFightOnline.manager.GFightOnlineGroupMgr;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.group.pojo.cfg.GroupFunctionCfg;
import com.rwbase.dao.group.pojo.cfg.dao.GroupFunctionCfgDAO;
import com.rwproto.GrouFightOnlineProto.GFResultType;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineRspMsg;

/**
 * 在线帮战，用户竞标阶段管理类
 * 
 * @author aken
 *
 */
public class GFightGroupBidBM {

	private static GFightGroupBidBM instance = new GFightGroupBidBM();

	public static GFightGroupBidBM getInstance() {
		return instance;
	}

	public void synData(Player player, int version) {
		GFBiddingItemHolder.getInstance().synAllData(player);
	}

	/**
	 * 获取所有资源点的占有信息和状态信息
	 * 
	 * @param player
	 * @param gfRsp
	 */
	public void getResourceInfo(Player player, GroupFightOnlineRspMsg.Builder gfRsp) {
		gfRsp.setSystemTime(System.currentTimeMillis());
		gfRsp.setRstType(GFResultType.SUCCESS);
	}

	/**
	 * 获取帮派竞标排行榜
	 * 
	 * @param player
	 * @param gfRsp
	 * @param resourceID
	 */
	public void getGroupBidRank(Player player, GroupFightOnlineRspMsg.Builder gfRsp, int resourceID) {
		List<GFGroupBiddingItem> groupBidRank = GFGroupBiddingRankMgr.getGFGroupBidRankList(resourceID, GFightConst.GROUP_BID_RANK_COUNT);
		if (groupBidRank == null) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("取不到帮派竞标排行榜");
		}
		for (GFGroupBiddingItem item : groupBidRank)
			gfRsp.addRankData(ClientDataSynMgr.toClientData(item));
		String groupID = GroupHelper.getInstance().getUserGroupId(player.getUserId());
		gfRsp.setSelfRank(GFGroupBiddingRankMgr.getRankIndex(resourceID, groupID));
		gfRsp.setRstType(GFResultType.SUCCESS);
	}

	/**
	 * 帮派竞标
	 * 
	 * @param player
	 * @param gfRsp
	 * @param resourceID
	 * @param bidCount
	 */
	public void groupBidding(Player player, GroupFightOnlineRspMsg.Builder gfRsp, int resourceID, int bidCount) {
		GFResultType canBid = GFightConditionJudge.getInstance().canBidForGroup(player, resourceID, bidCount);
		if (canBid != GFResultType.SUCCESS) {
			gfRsp.setRstType(canBid);
			return;
		}
		if (!GFightConditionJudge.getInstance().isBidPeriod(resourceID)) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("不在竞标期间");
			return;
		}
		if (!GFightConditionJudge.getInstance().isLevelEnoughForBid(player)) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			GroupFunctionCfg funCfg = GroupFunctionCfgDAO.getDAO().getCfgById(GFightConst.GF_BID_AUTHORITY_ID);
			gfRsp.setTipMsg("竞标要求帮派达到" + funCfg.getNeedGroupLevel() + "级");
			return;
		}
		GFightOnlineGroupData gfGroupData = GFightOnlineGroupMgr.getInstance().getByUser(player);
		if (!GFightConditionJudge.getInstance().isLegalBidCount(resourceID, gfGroupData.getBiddingCount(), bidCount)) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("竞标数量没有达到最小要求(起始或最小增长值)");
			return;
		}
		List<GFightOnlineResourceCfg> resDataCfgs = GFightOnlineResourceCfgDAO.getInstance().getAllCfg();
		for (GFightOnlineResourceCfg cfg : resDataCfgs) {
			GFightOnlineResourceData resData = GFightOnlineResourceHolder.getInstance().get(cfg.getResID());
			if (null != resData && !resData.isOwnerBidAble() && StringUtils.equals(resData.getOwnerGroupID(), gfGroupData.getGroupID())) {
				gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
				gfRsp.setTipMsg("竞标冷却期间，不能竞标");
				return;
			}
		}
		// if(gfGroupData.getResourceID() > 0){// && gfGroupData.getResourceID() != resourceID) {
		// GFightOnlineResourceData resData = GFightOnlineResourceHolder.getInstance().get(gfGroupData.getResourceID());
		// if(!resData.isOwnerBidAble() && StringUtils.equals(resData.getOwnerGroupID(), gfGroupData.getGroupID())){
		// gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
		// gfRsp.setTipMsg("竞标冷却期间，不能竞标");
		// return;
		// }
		// }
		if (!GFightConditionJudge.getInstance().haveAuthorityToBid(player)) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("您的帮派职位没有竞标权限");
			return;
		}
		if (!GFightConditionJudge.getInstance().isEnoughGroupToken(player, gfGroupData.getBiddingCount(), bidCount)) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("帮派令牌不足");
			return;
		}
		gfGroupData.setResourceID(resourceID);
		gfGroupData.setBiddingCount(bidCount);
		gfGroupData.setLastBidTime(System.currentTimeMillis());
		GFightOnlineGroupMgr.getInstance().update(player, gfGroupData, true);
		// 排行榜有改变
		List<GFGroupBiddingItem> groupBidRank = GFGroupBiddingRankMgr.getGFGroupBidRankList(resourceID);
		if (groupBidRank == null) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg("没取到帮派竞标排行榜");
			return;
		}
		for (GFGroupBiddingItem item : groupBidRank)
			gfRsp.addRankData(ClientDataSynMgr.toClientData(item));
		gfRsp.setRstType(GFResultType.SUCCESS);
	}

	public void removeItemsOnResource(int resourceID) {
		GFBiddingItemHolder.getInstance().removeItemsOnResource(resourceID);
	}

	/**
	 * 竞标开始时处理的事件
	 * 
	 * @param resourceID
	 */
	public void bidStart(int resourceID) {
		// 清除几个排行榜
		GFGroupBiddingRankMgr.clearRank(resourceID);
		GFOnlineKillRankMgr.clearRank(resourceID);
		GFOnlineHurtRankMgr.clearRank(resourceID);
	}
}
