package com.playerdata.groupFightOnline.bm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bm.group.GroupBM;
import com.bm.rank.groupFightOnline.GFGroupBiddingRankMgr;
import com.bm.rank.groupFightOnline.GFOnlineHurtRankMgr;
import com.bm.rank.groupFightOnline.GFOnlineKillRankMgr;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfgDAO;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupData;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceData;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceHolder;
import com.playerdata.groupFightOnline.data.UserGFightOnlineHolder;
import com.playerdata.groupFightOnline.dataException.GFRewardItemException;
import com.playerdata.groupFightOnline.dataForRank.GFEndGroupInfo;
import com.playerdata.groupFightOnline.dataForRank.GFGroupBiddingItem;
import com.playerdata.groupFightOnline.dataForRank.GFOnlineKillItem;
import com.playerdata.groupFightOnline.manager.GFDefendArmyMgr;
import com.playerdata.groupFightOnline.manager.GFFinalRewardMgr;
import com.playerdata.groupFightOnline.manager.GFightOnlineGroupMgr;
import com.playerdata.groupFightOnline.manager.GFightOnlineResourceMgr;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwproto.GrouFightOnlineProto.GFResultType;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineRspMsg;

/**
 * 在线帮战，最终结算阶段管理类
 * 
 * @author aken
 *
 */
public class GFightFinalBM {

	private static GFightFinalBM instance = new GFightFinalBM();

	public static GFightFinalBM getInstance() {
		return instance;
	}

	/**
	 * 获取帮战最后的各类奖励
	 * 
	 * @param player
	 * @param gfRsp
	 * @param resourceID
	 * @param rewardID 奖励的id
	 */
	public void getFinalReward(Player player, GroupFightOnlineRspMsg.Builder gfRsp, int resourceID, String rewardID) {
		try {
			GFFinalRewardMgr.getInstance().getFinalReward(player, resourceID, rewardID);
			gfRsp.setRstType(GFResultType.SUCCESS);
		} catch (GFRewardItemException e) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
			gfRsp.setTipMsg(e.getMessage());
		}
	}

	/**
	 * 处理帮战结果的逻辑
	 * 
	 * @param resourceID
	 */
	public void handleGFightResult(int resourceID) {
		// 设置资源点占有者的可竞标状态
		setResourceOwnerBidAble(resourceID);
		GFightOnlineResourceMgr.getInstance().clearVictoryGroup(resourceID);
		// 杀敌排行奖励
		handleKillRankReward(resourceID);
		// 伤害排行奖励
		handleHurtRankReward(resourceID);

		// 确定帮战胜利方
		List<String> groupRankList = getRankGroupID(resourceID);
		if (groupRankList == null || groupRankList.size() == 0)
			return;
		handleVictoryGroup(resourceID, groupRankList.get(0));
		for (int i = 1; i < groupRankList.size(); i++)
			handleFailGroup(groupRankList.get(i));
		// 清除本次循环中的数据，以便于开始下个循环
		clearCurrentLoopData(resourceID);
	}

	/**
	 * 获取帮战最终排名
	 * 
	 * @param resourceID
	 * @return
	 */
	private List<String> getRankGroupID(int resourceID) {
		List<GFGroupBiddingItem> bidList = GFGroupBiddingRankMgr.getGFGroupBidRankList(resourceID);
		if (bidList.size() == 0)
			return null;
		List<GFEndGroupInfo> resultGroups = new ArrayList<GFEndGroupInfo>();
		List<GFOnlineKillItem> killRank = GFOnlineKillRankMgr.getGFKillRankList(resourceID);
		for (int i = 0; i < GFightConst.IN_FIGHT_MAX_GROUP && i < bidList.size(); i++) {
			GFightOnlineGroupData groupData = GFightOnlineGroupMgr.getInstance().get(bidList.get(i).getGroupID());
			int killCount = 0;
			for (GFOnlineKillItem killItem : killRank) {
				if (killItem.getGroupID().equals(bidList.get(i).getGroupID())) {
					killCount += killItem.getTotalKill();
				}
			}
			resultGroups.add(new GFEndGroupInfo(bidList.get(i).getGroupID(), groupData.getAliveCount(), killCount, groupData.getLastkillTime()));
		}
		Collections.sort(resultGroups);
		List<String> result = new ArrayList<String>();
		for (GFEndGroupInfo info : resultGroups)
			result.add(info.getGroupID());
		return result;
	}

	/**
	 * 击杀排名奖励
	 * 
	 * @param resourceID
	 */
	private void handleKillRankReward(int resourceID) {
		GFOnlineKillRankMgr.dispatchKillReward(resourceID);
	}

	/**
	 * 伤害排行奖励
	 * 
	 * @param resourceID
	 */
	private void handleHurtRankReward(int resourceID) {
		GFOnlineHurtRankMgr.dispatchHurtReward(resourceID);
	}

	/**
	 * 处理获胜帮派的事务
	 * 
	 * @param groupID
	 */
	private void handleVictoryGroup(int resourceID, String groupID) {
		// 占领资源点
		GFightOnlineResourceMgr.getInstance().setVictoryGroup(resourceID, groupID);
		// 发放帮战胜利成员奖励
		GFightOnlineGroupMgr.getInstance().dispatchVictoryReward(groupID);
		// 发放帮派被压标奖励
		GFightOnlineGroupMgr.getInstance().dispathchBidOnReward(groupID);
	}

	/**
	 * 处理战斗失败的帮派事务
	 * 
	 * @param groupID
	 */
	private void handleFailGroup(String groupID) {
		GFightOnlineGroupMgr.getInstance().dispathchFailReward(groupID);
	}

	/**
	 * 清空此循环的数据
	 * 
	 * @param resourceID
	 */
	private void clearCurrentLoopData(int resourceID) {
		// 清除所有的玩家压标信息
		GFightGroupBidBM.getInstance().removeItemsOnResource(resourceID);
		List<GFGroupBiddingItem> bidList = GFGroupBiddingRankMgr.getGFGroupBidRankList(resourceID);
		for (GFGroupBiddingItem item : bidList) {
			// 清除帮派的所有防守队伍
			GFDefendArmyMgr.getInstance().clearAllRecords(item.getGroupID());
			// 清除帮战的帮派信息
			GFightOnlineGroupMgr.getInstance().clearCurrentLoopData(item.getGroupID());
			// 清除所有参与帮战的成员的个人信息
			Group group = GroupBM.getInstance().get(item.getGroupID());
			if (null != group) {
				List<? extends GroupMemberDataIF> groupMembers = group.getGroupMemberMgr().getMemberSortList(null);
				for (GroupMemberDataIF member : groupMembers)
					UserGFightOnlineHolder.getInstance().resetData(member.getUserId());
			}
		}
		// 清除几个排行榜
		GFGroupBiddingRankMgr.clearRank(resourceID);
		GFOnlineKillRankMgr.clearRank(resourceID);
		GFOnlineHurtRankMgr.clearRank(resourceID);
	}

	/**
	 * 设置下个资源点的可占领情况
	 * 
	 * @param resourceID
	 */
	private void setResourceOwnerBidAble(int currentResourceID) {
		// 设置当前资源点占有者的可竞标状态
		GFightOnlineResourceData currentResData = GFightOnlineResourceHolder.getInstance().get(currentResourceID);
		if (currentResData != null) {
			currentResData.setOwnerBidAble(false);
			GFightOnlineResourceHolder.getInstance().update(currentResData);
		}
		// 设置下一个资源点占有者的可竞标状态
		List<GFightOnlineResourceCfg> resCfg = GFightOnlineResourceCfgDAO.getInstance().getAllCfg();
		int size = resCfg.size();
		int nextResourceID = (currentResourceID + 1) % size == 0 ? size : (currentResourceID + 1) % size;
		GFightOnlineResourceData nextResData = GFightOnlineResourceHolder.getInstance().get(nextResourceID);
		if (nextResData != null) {
			nextResData.setOwnerBidAble(true);
			GFightOnlineResourceHolder.getInstance().update(nextResData);
		}
	}
}
