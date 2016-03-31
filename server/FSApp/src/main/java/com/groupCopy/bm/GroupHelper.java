package com.groupCopy.bm;

import com.bm.group.GroupBM;
import com.playerdata.Player;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;

public class GroupHelper {

	private GroupHelper(){};
	
	public static String getGroupId(Player player){
		UserGroupAttributeDataIF userGroupAttributeData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = null;
		if(userGroupAttributeData!=null){
			groupId = userGroupAttributeData.getGroupId();
		}
		return groupId;
	}
	
	public static Group getGroup(Player player){
		String groupId = getGroupId(player);
		return GroupBM.get(groupId);
	}
}
