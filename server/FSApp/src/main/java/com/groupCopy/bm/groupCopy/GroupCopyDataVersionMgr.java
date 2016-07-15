package com.groupCopy.bm.groupCopy;

import org.apache.commons.lang3.StringUtils;

import com.bm.group.GroupBM;
import com.groupCopy.bm.GroupHelper;
import com.groupCopy.playerdata.group.UserGroupCopyMapRecordMgr;
import com.groupCopy.rwbase.dao.groupCopy.db.ServerGroupCopyDamageRecordMgr;
import com.playerdata.Player;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.dao.group.pojo.Group;

public class GroupCopyDataVersionMgr {
	
	public final static int TYPE_LEVEL = 1;
	public final static int TYPE_MAP = 2;
	public final static int TYPE_REWARD = 3;
	public final static int TYPE_USER = 4;
//	public final static int TYPE_DROP_APPLY = 5;
	public final static int TYPE_SERVER_HURT_RANK = 6;
	public static void synAllDataByVersion(Player player, String versionJson) {
		
		if(StringUtils.isNotBlank(versionJson)){
			String groupId = GroupHelper.getGroupId(player);
			Group group = GroupBM.get(groupId);
			if(group!=null){				
				
				GroupCopyDataVersion groupDataVersion = fromJson(versionJson);
				if (groupDataVersion == null) {
					return;
				}
				
				group.synGroupLevelData(player, groupDataVersion.getGroupCopyLevelData());
				group.synGroupMapData(player, groupDataVersion.getGroupCopyMapData());
				group.synGroupRewardData(player, groupDataVersion.getGroupCopyRewardData());
				player.getUserGroupCopyRecordMgr().syncData(player);
			}
			
		}
		

	}

	public static GroupCopyDataVersion fromJson(String versionJson) {
		GroupCopyDataVersion groupDataVersion = JsonUtil.readValue(versionJson, GroupCopyDataVersion.class);
		return groupDataVersion;
	}

	
	
	public static void syncSingleDataByVersion(Player player, String versionJson, int type){
		if(StringUtils.isNotBlank(versionJson)){
			String groupId = GroupHelper.getGroupId(player);
			Group group = GroupBM.get(groupId);
			if(group!=null){				
				
				GroupCopyDataVersion verson = fromJson(versionJson);
				if (verson == null) {
					return;
				}
				switch (type) {
				case TYPE_LEVEL:
					group.synGroupLevelData(player, verson.getGroupCopyLevelData());
					break;
//				case TYPE_DROP_APPLY:
//					group.synGroupCopyDropApplyData(player, verson.getGroupCopyDropApplyData());
//					break;
				case TYPE_MAP:
					group.synGroupMapData(player, verson.getGroupCopyMapData());
					break;
				case TYPE_REWARD:
					group.synGroupRewardData(player, verson.getGroupCopyRewardData());
					break;
				case TYPE_USER:
					player.getUserGroupCopyRecordMgr().syncData(player);
					break;
				case TYPE_SERVER_HURT_RANK:
					ServerGroupCopyDamageRecordMgr.getInstance().synAllData(player, verson.getServerCopyDamageRankData());
					break;
				default:
					break;
				}
				
				
				
				
			}
			
		}
	}
}
