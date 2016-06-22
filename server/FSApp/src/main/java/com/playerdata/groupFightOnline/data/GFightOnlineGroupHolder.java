package com.playerdata.groupFightOnline.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.bm.rank.groupFightOnline.GFGroupBiddingRankMgr;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.dataForRank.GFGroupBiddingItem;
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
	
	public int getCurrentVersion(){
		return gfGroupVersion.get();
	}
	
	public GFightOnlineGroupData get(String groupId) {
		return getItem(groupId, 0);
	}
	
	public GFightOnlineGroupData getItem(String groupId, int version) {
		GFightOnlineGroupData data = gfGroupDao.get(groupId);
		if(data != null && data.getVersion() > version) return data;
		return null;
	}
	
	public GFightOnlineGroupData getByUser(Player player) {
		String groupID = player.getGuildUserMgr().getGuildId();
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
	 * 只用来同步所有的公会信息
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
			ClientDataSynMgr.synDataList(player, groupList, synType, eSynOpType.UPDATE_LIST);
	}
	
	public synchronized void addDefenderCount(String groupId, int count) {
		int newVersion = gfGroupVersion.incrementAndGet();
		GFightOnlineGroupData groupData = get(groupId);
		groupData.setVersion(newVersion);
		groupData.setDefenderCount(count);
		gfGroupDao.update(groupData);
	}
	
	public synchronized void deductAliveCount(String groupId, int count) {
		int newVersion = gfGroupVersion.incrementAndGet();
		GFightOnlineGroupData groupData = get(groupId);
		groupData.setVersion(newVersion);
		groupData.deductAliveCount(count);
		gfGroupDao.update(groupData);
	}
}
