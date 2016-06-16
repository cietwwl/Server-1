package com.bm.groupChamp.data;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;


public class UserGroupChampDataHolder {

	private static UserGroupChampDataHolder instance = new UserGroupChampDataHolder();
	private static eSynType synType = eSynType.UserGroupChampData;

	public static UserGroupChampDataHolder getInstance() {
		return instance;
	}


	public UserGroupChampData get(String userId) {
		return UserGroupChampDataDAO.getInstance().get(userId);
	}

	/**
	 * 同步数据
	 * 
	 * @param player 角色
	 */
	public void synData(Player player) {
	

		UserGroupChampData groupData = get(player.getUserId());
		if (groupData != null) {
			ClientDataSynMgr.synData(player, groupData, synType, eSynOpType.UPDATE_SINGLE);
		}
		
	}
	
	public void update(UserGroupChampData data) {	
		UserGroupChampDataDAO.getInstance().update(data);
	}

	
}