package com.playerdata.groupFightOnline.data.version;

import java.util.List;

import com.bm.group.GroupBM;
import com.playerdata.Player;
import com.playerdata.dataSyn.SynDataGroupListVersion;
import com.playerdata.groupFightOnline.manager.GFDefendArmyMgr;
import com.playerdata.groupFightOnline.manager.GFightGroupBidMgr;
import com.playerdata.groupFightOnline.manager.GFightOnlineResourceMgr;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.dao.group.pojo.Group;

public class GFightDataVersionMgr {

	public static void synByVersion(Player player, String versionJson) {
		// 基础数据版本号
		String groupId = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData().getGroupId();
		Group group = GroupBM.get(groupId);
		if (group == null) {
			return;
		}

		GFightDataVersion groupDataVersion = fromJson(versionJson);
		if (groupDataVersion == null) {
			return;
		}
		
		synBiddingItem(player, groupDataVersion.getBiddingItem());
		synOnlineGroupData(player, groupDataVersion.getOnlineGroupData());
		synOnlineResourceData(player, groupDataVersion.getOnlineResourceData());
		synDefendArmyItem(player, groupDataVersion.getDefendArmyItem());
	
	}
	
	public static void synAll(Player player, List<String> groupIdList){
		int version = -1;
		synBiddingItem(player, version);
		synOnlineGroupData(player, version);
		synOnlineResourceData(player, version);
		for (String groupId : groupIdList) {
			GFDefendArmyMgr.getInstance().synGroupData(player, groupId, -1);
		}
	}
	
	
	private static void synBiddingItem(Player player, int version){
		GFightGroupBidMgr.getInstance().synData(player, version);
		
	}
	private static void synOnlineGroupData(Player player, int version){
		
		
	}
	private static void synOnlineResourceData(Player player, int version){
		
		GFightOnlineResourceMgr.getInstance().synData(player, version);
	}
	
	private static void synDefendArmyItem(Player player, List<SynDataGroupListVersion> versionList){
		for (SynDataGroupListVersion versionTmp : versionList) {			
			GFDefendArmyMgr.getInstance().synGroupData(player, versionTmp.getGroupId(), versionTmp.getVersion());
		}
	}
	
	
	
	public static GFightDataVersion fromJson(String versionJson) {
		GFightDataVersion groupDataVersion = JsonUtil.readValue(versionJson, GFightDataVersion.class);
		if(groupDataVersion == null) groupDataVersion = new GFightDataVersion();
		return groupDataVersion;
	}

}
