package com.playerdata.groupFightOnline.data;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;


public class GroupChampDataHolder {

	private static GroupChampDataHolder instance = new GroupChampDataHolder();
	private static eSynType synType = eSynType.GroupChampData;

	public static GroupChampDataHolder getInstance() {
		return instance;
	}


	public GroupChampData get(String groupChampId) {
		return GroupChampDataDAO.getInstance().get(groupChampId);
	}

	/**
	 * 同步数据
	 * 
	 * @param player 角色
	 */
	public void synData(Player player, String groupChampId,  int version) {
	

		GroupChampData groupData = get(groupChampId);
		if (groupData == null || groupData.getVersion()>=version) {
			return;
		}
		
		ClientDataSynMgr.synData(player, groupData, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public void update(GroupChampData data) {
		data.incrVersion();
		GroupChampDataDAO.getInstance().update(data);
	}

	
}