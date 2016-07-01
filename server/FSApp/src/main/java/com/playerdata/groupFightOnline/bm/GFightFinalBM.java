package com.playerdata.groupFightOnline.bm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bm.group.GroupBM;
import com.bm.rank.groupFightOnline.GFGroupBiddingRankMgr;
import com.bm.rank.groupFightOnline.GFOnlineHurtRankMgr;
import com.bm.rank.groupFightOnline.GFOnlineKillRankMgr;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupData;
import com.playerdata.groupFightOnline.data.UserGFightOnlineHolder;
import com.playerdata.groupFightOnline.dataForRank.GFEndGroupInfo;
import com.playerdata.groupFightOnline.dataForRank.GFGroupBiddingItem;
import com.playerdata.groupFightOnline.dataForRank.GFOnlineKillItem;
import com.playerdata.groupFightOnline.manager.GFDefendArmyMgr;
import com.playerdata.groupFightOnline.manager.GFightOnlineGroupMgr;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;

/**
 * 在线帮战，最终结算阶段管理类
 * @author aken
 *
 */
public class GFightFinalBM {
	
	private static GFightFinalBM instance = new GFightFinalBM();
	
	public static GFightFinalBM getInstance(){
		return instance;
	}
	
	/**
	 * 处理帮战结果的逻辑
	 * @param resourceID
	 */
	public void calculateFightResult(int resourceID){
		List<String> groupRankList = getRankGroupID(resourceID);
		if(groupRankList == null) return;
		handleVictoryGroup(groupRankList.get(0));
		for(int i = 1; i < groupRankList.size(); i++)
			handleFailGroup(groupRankList.get(i));
		//清除本次循环中的数据，以便于开始下个循环
		clearCurrentLoopData(resourceID);
	}
	
	/**
	 * 获取帮战最终排名
	 * @param resourceID
	 * @return
	 */
	private List<String> getRankGroupID(int resourceID){
		List<GFGroupBiddingItem> bidList = GFGroupBiddingRankMgr.getGFGroupBidRankList(resourceID);
		if(bidList.size() == 0) return null;
		List<GFEndGroupInfo> resultGroups = new ArrayList<GFEndGroupInfo>();
		List<GFOnlineKillItem> killRank = GFOnlineKillRankMgr.getGFKillRankList(resourceID);
		for(int i = 0; i < 4 && i < bidList.size(); i++){
			GFightOnlineGroupData groupData = GFightOnlineGroupMgr.getInstance().get(bidList.get(i).getGroupID());
			int killCount = 0;
			for(GFOnlineKillItem killItem : killRank){
				if(killItem.getGroupID().equals(bidList.get(i).getGroupID())){
					killCount += killItem.getTotalKill();
				}
			}
			resultGroups.add(new GFEndGroupInfo(bidList.get(i).getGroupID(), groupData.getAliveCount(), killCount, groupData.getLastkillTime()));
		}
		Collections.sort(resultGroups);
		List<String> result = new ArrayList<String>();
		for(GFEndGroupInfo info : resultGroups)
			result.add(info.getGroupID());
		return result;
	}
	
	/**
	 * 处理获胜帮派的事务
	 * @param groupID
	 */
	private void handleVictoryGroup(String groupID){
		
	}
	
	/**
	 * 处理战斗失败的帮派事务
	 * @param groupID
	 */
	private void handleFailGroup(String groupID){
		
	}
	
	/**
	 * 清空此循环的数据
	 * @param resourceID
	 */
	private void clearCurrentLoopData(int resourceID){
		// 清除所有的玩家压标信息
		GFightGroupBidBM.getInstance().removeItemsOnResource(resourceID);
		List<GFGroupBiddingItem> bidList = GFGroupBiddingRankMgr.getGFGroupBidRankList(resourceID);
		for(GFGroupBiddingItem item : bidList){
			//清除帮派的所有防守队伍
			GFDefendArmyMgr.getInstance().clearAllRecords(item.getGroupID());
			//清除帮战的帮派信息
			GFightOnlineGroupMgr.getInstance().clearCurrentLoopData(item.getGroupID());
			//清除所有参与帮战的成员的个人信息
			List<? extends GroupMemberDataIF> groupMembers = GroupBM.get(item.getGroupID()).getGroupMemberMgr().getMemberSortList(null);
			for(GroupMemberDataIF member : groupMembers)
				UserGFightOnlineHolder.getInstance().resetData(member.getUserId());
		}
		//清除几个排行榜
		GFGroupBiddingRankMgr.clearRank(resourceID);
		GFOnlineKillRankMgr.clearRank(resourceID);
		GFOnlineHurtRankMgr.clearRank(resourceID);
	}
}
