package com.playerdata.groupFightOnline.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.bm.rank.groupFightOnline.GFGroupBiddingRankMgr;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.dataForRank.GFGroupBiddingItem;
import com.rw.service.group.helper.GroupHelper;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GFightOnlineGroupHolder {
	private static AtomicInteger gfGroupVersion = new AtomicInteger(0);
	private static GFightOnlineGroupHolder instance = new GFightOnlineGroupHolder();
	private static GFightOnlineGroupDAO gfGroupDao = GFightOnlineGroupDAO.getInstance();
	
	public static GFightOnlineGroupHolder getInstance() {
		return instance;
	}

	private GFightOnlineGroupHolder() { }

	final private eSynType synType = eSynType.GFightOnlineGroupData;
	
	public GFightOnlineGroupData get(String groupId) {
		if(groupId.isEmpty()) return null;
		GFightOnlineGroupData groupData = getItem(groupId, -1);
		if(groupData == null) {
			initGroupData(groupId);
			groupData = getItem(groupId, -1);
		}
		return groupData;
	}
	
	public void clearCurrentLoopData(String groupId){
		GFightOnlineGroupData data = get(groupId);
		data.setVersion(gfGroupVersion.incrementAndGet());
		data.clearCurrentLoopData();
	}
	
	public GFightOnlineGroupData getItem(String groupId, int version) {
		GFightOnlineGroupData data = gfGroupDao.get(groupId);
		if(data != null && data.getVersion() > version) return data;
		return null;
	}
	
	public GFightOnlineGroupData getByUser(Player player) {
		String groupID = GroupHelper.getUserGroupId(player.getUserId());
		if(groupID.isEmpty()) return null;
		return get(groupID);
	}
	
	public void update(Player player, GFightOnlineGroupData data, boolean isUpdateBidRank) {
		int newVersion = gfGroupVersion.incrementAndGet();
		data.setVersion(newVersion);
		gfGroupDao.update(data);
		if(isUpdateBidRank) GFGroupBiddingRankMgr.addOrUpdateGFGroupBidRank(player, data);
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
		for(GFGroupBiddingItem item : bidList) {
			GFightOnlineGroupData groupData = getItem(item.getGroupID(), version);
			if(groupData != null) groupList.add(groupData);
		}
		if(groupList.size() > 0)
			ClientDataSynMgr.synDataList(player, groupList, synType, eSynOpType.UPDATE_PART_LIST, gfGroupVersion.get());
	}
	
	/**
	 * 增加防守队伍总数
	 * @param groupId
	 * @param count
	 */
	public synchronized void addDefenderCount(String groupId, int count) {
		int newVersion = gfGroupVersion.incrementAndGet();
		GFightOnlineGroupData groupData = get(groupId);
		groupData.setVersion(newVersion);
		groupData.addDefenderCount(count);
		gfGroupDao.update(groupData);
	}
	
	/**
	 * 减少存活队伍总数
	 * @param groupId
	 * @param count
	 */
	public synchronized void deductAliveCount(String groupId) {
		int newVersion = gfGroupVersion.incrementAndGet();
		GFightOnlineGroupData groupData = get(groupId);
		groupData.setVersion(newVersion);
		groupData.deductAliveCount();
		gfGroupDao.update(groupData);
	}
	
	private void initGroupData(String groupId) {
		GFightOnlineGroupData data = new GFightOnlineGroupData();
		data.setGroupID(groupId);
		gfGroupDao.update(data);
	}
}
