package com.playerdata.groupFightOnline.data;

import com.bm.rank.groupFightOnline.GFGroupBiddingRankMgr;
import com.playerdata.Player;

public class GFightOnlineGroupHolder {
	private static GFightOnlineGroupHolder instance = new GFightOnlineGroupHolder();
	private static GFightOnlineGroupDAO gfGroupDao = GFightOnlineGroupDAO.getInstance();
	
	public static GFightOnlineGroupHolder getInstance() {
		return instance;
	}

	private GFightOnlineGroupHolder() { }

	// final private eSynType synType = eSynType.GFightOnlineGroupData;
	
	public GFightOnlineGroupData get(String groupId) {
		return gfGroupDao.get(groupId);
	}
	
	public GFightOnlineGroupData getByUser(Player player) {
		String groupID = player.getGuildUserMgr().getGuildId();
		return get(groupID);
	}
	
	public void update(Player player, GFightOnlineGroupData data) {
		gfGroupDao.update(data);
		GFGroupBiddingRankMgr.addOrUpdateGFGroupBidRank(player, data);
	}
}
