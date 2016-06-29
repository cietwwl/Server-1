package com.playerdata.groupFightOnline.data;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GFightOnlineGroupHolder {
	
	private static GFightOnlineGroupHolder instance = new GFightOnlineGroupHolder();
	
	final private eSynType synType = eSynType.GFightOnlineGroupData;
	
	
	public static GFightOnlineGroupHolder getInstance() {
		return instance;
	}

	
	public GFightOnlineGroupData get(String groupId) {
		return GFightOnlineGroupDAO.getInstance().get(groupId);
	}
	
	public void add(GFightOnlineGroupData data) {			
		GFightOnlineGroupDAO.getInstance().update(data);
	}
	
	public void update(GFightOnlineGroupData data) {	
		GFightOnlineGroupDAO.getInstance().update(data);
	}

	/**
	 * 只用来同步所有的帮派信息
	 * 帮派的其它防守队伍，需要用请求
	 * @param player
	 */
	public void synAllData(Player player, List<String> groupIdList){
		
		List<GFightOnlineGroupData> groupList = new ArrayList<GFightOnlineGroupData>();		
		
		for(String groupId : groupIdList) {
			GFightOnlineGroupData groupData = get(groupId);
			if(groupData != null) {
				groupList.add(groupData);
			}
		}
		if(groupList.size() > 0){
			ClientDataSynMgr.synDataList(player, groupList, synType, eSynOpType.UPDATE_LIST);
		}
	}
}
