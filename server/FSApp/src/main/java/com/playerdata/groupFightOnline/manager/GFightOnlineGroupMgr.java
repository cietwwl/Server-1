package com.playerdata.groupFightOnline.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bm.rank.groupFightOnline.GFGroupBiddingRankMgr;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupData;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupHolder;
import com.playerdata.groupFightOnline.dataForRank.GFGroupBiddingItem;
import com.rw.service.group.helper.GroupHelper;

public class GFightOnlineGroupMgr {
	
	private static GFightOnlineGroupMgr instance = new GFightOnlineGroupMgr();
	
	public static GFightOnlineGroupMgr getInstance() {
		return instance;
	}

	
	public GFightOnlineGroupData get(String groupId) {
		GFightOnlineGroupData groupData = GFightOnlineGroupHolder.getInstance().get(groupId);
		
		if(groupData == null) {
			initGroupData(groupId);
			groupData =  GFightOnlineGroupHolder.getInstance().get(groupId);
		}
		return groupData;
	}
	
	private void initGroupData(String groupId) {
		GFightOnlineGroupData data = new GFightOnlineGroupData();
		data.setGroupID(groupId);
		GFightOnlineGroupHolder.getInstance().add(data);
	}
	
	public void clearCurrentLoopData(String groupId){
		GFightOnlineGroupData data = get(groupId);
		data.clearCurrentLoopData();
		GFightOnlineGroupHolder.getInstance().update(data);
		
	}

	
	public GFightOnlineGroupData getByUser(Player player) {
		String groupID = GroupHelper.getUserGroupId(player.getUserId());
		GFightOnlineGroupData target  = null;
		if(StringUtils.isNotBlank(groupID)){
			target = get(groupID);
		}
		return target;
	}
	
	public void update(Player player, GFightOnlineGroupData data, boolean isUpdateBidRank) {
	
		GFightOnlineGroupHolder.getInstance().update(data);
		
		if(isUpdateBidRank) {
			GFGroupBiddingRankMgr.addOrUpdateGFGroupBidRank(player, data);
		}
	}
	
	public void update(Player player, GFightOnlineGroupData data) {
		update(player, data, false);
	}
	
	/**
	 * 只用来同步所有的帮派信息
	 * 帮派的其它防守队伍，需要用请求
	 * @param player
	 */
	public void synAllData(Player player, int resourceID, int version){
		List<GFGroupBiddingItem> bidList = GFGroupBiddingRankMgr.getGFGroupBidRankList(resourceID);
		List<GFightOnlineGroupData> groupList = new ArrayList<GFightOnlineGroupData>();
		List<String> groupIdList = new ArrayList<String>();
		for(GFGroupBiddingItem item : bidList) {
			GFightOnlineGroupData groupData = get(item.getGroupID());
			if(groupData != null) {				
				groupIdList.add(groupData.getGroupID());
			}
		}
		if(groupList.size() > 0){
			GFightOnlineGroupHolder.getInstance().synAllData( player, groupIdList );
		}
	}
	
	/**
	 * 增加防守队伍总数
	 * @param groupId
	 * @param count
	 */
	public synchronized void addDefenderCount(String groupId, int count) {
		GFightOnlineGroupData groupData = get(groupId);
		groupData.addDefenderCount(count);		
		GFightOnlineGroupHolder.getInstance().update(groupData);
	}
	
	/**
	 * 减少存活队伍总数
	 * @param groupId
	 * @param count
	 */
	public synchronized void deductAliveCount(String groupId) {
		GFightOnlineGroupData groupData = get(groupId);
		groupData.deductAliveCount();
		GFightOnlineGroupHolder.getInstance().update(groupData);
	}
	
	
}
