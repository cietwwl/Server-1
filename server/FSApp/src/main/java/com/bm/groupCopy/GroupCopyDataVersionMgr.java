package com.bm.groupCopy;

import org.apache.commons.lang3.StringUtils;

import com.bm.group.GroupBM;
import com.playerdata.Player;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.dao.group.pojo.Group;

public class GroupCopyDataVersionMgr {
	

	public static void synAllDataByVersion(final Player player, String versionJson) {
		
		if(StringUtils.isNotBlank(versionJson)){
			String groupId = com.rw.service.group.helper.GroupHelper.getGroupId(player);    

			Group group = GroupBM.get(groupId);
			if(group!=null){				
				
				GroupCopyDataVersion groupDataVersion = fromJson(versionJson);
				if (groupDataVersion == null) {
					return;
				}
				
				group.synGroupLevelData(player, groupDataVersion.getGroupCopyLevelData());
				group.synGroupMapData(player, groupDataVersion.getGroupCopyMapData());
				player.getUserGroupCopyRecordMgr().syncData(player);
				
				
			}
			
		}
		

	}

	public static GroupCopyDataVersion fromJson(String versionJson) {
		GroupCopyDataVersion groupDataVersion = JsonUtil.readValue(versionJson, GroupCopyDataVersion.class);
		return groupDataVersion;
	}

	
	
	
}
