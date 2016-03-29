package com.bm.group.groupCopy;

import org.apache.commons.lang3.StringUtils;

import com.bm.group.GroupBM;
import com.bm.group.GroupHelper;
import com.playerdata.Player;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.dao.group.pojo.Group;

public class GroupCopyDataVersionMgr {

	public static void synByVersion(Player player, String versionJson) {
		
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
			}
			
			
		}
		

	}

	public static GroupCopyDataVersion fromJson(String versionJson) {
		GroupCopyDataVersion groupDataVersion = JsonUtil.readValue(versionJson, GroupCopyDataVersion.class);
		return groupDataVersion;
	}

}
