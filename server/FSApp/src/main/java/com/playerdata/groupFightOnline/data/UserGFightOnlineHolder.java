package com.playerdata.groupFightOnline.data;

import com.bm.rank.groupFightOnline.GFOnlineHurtRankMgr;
import com.bm.rank.groupFightOnline.GFOnlineKillRankMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.service.group.helper.GroupHelper;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class UserGFightOnlineHolder {
	private static UserGFightOnlineHolder instance = new UserGFightOnlineHolder();
	private static UserGFightOnlineDAO gfPersonalDao = UserGFightOnlineDAO.getInstance();
	
	public static UserGFightOnlineHolder getInstance() {
		return instance;
	}

	private UserGFightOnlineHolder() { }
	final private eSynType synType = eSynType.GFightOnlinePersonalData;
	
	public UserGFightOnlineData get(String userID) {
		return gfPersonalDao.get(userID);
	}
	
	public void update(Player player, UserGFightOnlineData data) {
		gfPersonalDao.update(data);
		ClientDataSynMgr.synData(player, data, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public void updateAndInformRank(Player player, UserGFightOnlineData data) {
		GFOnlineKillRankMgr.addOrUpdateUserGFKillRank(player, data);
		GFOnlineHurtRankMgr.addOrUpdateUserGFHurtRank(player, data);
		update(player, data);
	}
	
	/**
	 * 同步数据
	 * @param player
	 */
	public void synData(Player player) {
		UserGFightOnlineData userGFData = get(player.getUserId());
		if (userGFData != null) {
			String groupID = GroupHelper.getUserGroupId(player.getUserId());
			if(!groupID.isEmpty()) {
				GFightOnlineGroupData gfGroup = GFightOnlineGroupHolder.getInstance().get(groupID);
				if(gfGroup != null) {
					userGFData.setResourceID(gfGroup.getResourceID());
				}
			}
			ClientDataSynMgr.synData(player, userGFData, synType, eSynOpType.UPDATE_SINGLE);
		}
	}
	
	/**
	 * 重置帮战个人数据
	 * @param userID
	 */
	public void resetData(String userID) {
		UserGFightOnlineData data = gfPersonalDao.get(userID);
		if(data == null) return;
		data.resetLoopData();
		Player player = PlayerMgr.getInstance().find(userID);
		update(player, data);
	}
}
