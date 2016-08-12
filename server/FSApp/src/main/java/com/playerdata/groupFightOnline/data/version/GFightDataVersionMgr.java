package com.playerdata.groupFightOnline.data.version;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bm.group.GroupBM;
import com.bm.rank.groupFightOnline.GFGroupBiddingRankMgr;
import com.playerdata.Player;
import com.playerdata.dataSyn.SynDataGroupListVersion;
import com.playerdata.groupFightOnline.bm.GFightGroupBidBM;
import com.playerdata.groupFightOnline.data.UserGFightOnlineHolder;
import com.playerdata.groupFightOnline.dataForRank.GFGroupBiddingItem;
import com.playerdata.groupFightOnline.manager.GFDefendArmyMgr;
import com.playerdata.groupFightOnline.manager.GFightOnlineGroupMgr;
import com.playerdata.groupFightOnline.manager.GFightOnlineResourceMgr;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.group.pojo.Group;

public class GFightDataVersionMgr {

	public static void synByVersion(Player player, int resourceID, String versionJson) {
		// 基础数据版本号
		String groupId = GroupHelper.getUserGroupId(player.getUserId());
		if(groupId == null || groupId.isEmpty()) return;
		Group group = GroupBM.get(groupId);
		if (group == null) {
			return;
		}

		GFightDataVersion groupDataVersion = fromJson(versionJson);
		if (groupDataVersion == null) {
			return;
		}
		if(resourceID > 0){
			synOnlineGroupData(player, resourceID, groupDataVersion.getOnlineGroupData());
			synDefendArmyItem(player, resourceID, groupDataVersion.getDefendArmyItem());
		}
		synOnlineResourceData(player, groupDataVersion.getOnlineResourceData());
		synBiddingItem(player, groupDataVersion.getBiddingItem());
		synUserGFData(player);
	}
	
	private static void synUserGFData(Player player){
		UserGFightOnlineHolder.getInstance().synData(player);
	}
	
	private static void synBiddingItem(Player player, int version){
		GFightGroupBidBM.getInstance().synData(player, version);
	}
	
	private static void synOnlineGroupData(Player player, int resourceID, int version){		
		GFightOnlineGroupMgr.getInstance().synAllData(player, resourceID, version); 
	}
	
	private static void synOnlineResourceData(Player player, int version){		
		GFightOnlineResourceMgr.getInstance().synData(player);
	}
	
	private static void synDefendArmyItem(Player player, int resourceID, List<SynDataGroupListVersion> versionList){
		List<GFGroupBiddingItem> bidRankList = GFGroupBiddingRankMgr.getGFGroupBidRankList(resourceID);
		for(GFGroupBiddingItem item : bidRankList){
			GFDefendArmyMgr.getInstance().synGroupDefenderData(player, item.getGroupID(), getVersion(item.getGroupID(), versionList));
		}
	}
	
	private static int getVersion(String groupID, List<SynDataGroupListVersion> versionList){
		if(null == versionList) return 0;
		for(SynDataGroupListVersion version : versionList){
			if(StringUtils.equals(groupID, version.getGroupId())) return version.getVersion();
		}
		return 0;
	}
	
	public static GFightDataVersion fromJson(String versionJson) {
		GFightDataVersion groupDataVersion = JsonUtil.readValue(versionJson, GFightDataVersion.class);
		if(groupDataVersion == null) groupDataVersion = new GFightDataVersion();
		return groupDataVersion;
	}
}
