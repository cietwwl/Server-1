package com.rw.service.group.helper;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.playerdata.PlayerMgr;
import com.playerdata.readonly.PlayerIF;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;

/*
 * @author HC
 * @date 2016年3月9日 下午3:07:35
 * @Description 帮派的Helper类
 */
public class GroupHelper {
	/**
	 * 获取帮派名字
	 * 
	 * @param userId
	 * @return
	 */
	public static String getGroupName(String userId) {
		PlayerIF player = PlayerMgr.getInstance().getReadOnlyPlayer(userId);
		if (player == null) {
			return "";
		}

		UserGroupAttributeDataIF userGroupAttributeData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = userGroupAttributeData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return "";
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			return "";
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			return "";
		}

		return groupData.getGroupName();
	}

	/**
	 * 获取个人的工会Id
	 * 
	 * @param userId
	 * @return
	 */
	public static String getUserGroupId(String userId) {
		PlayerIF player = PlayerMgr.getInstance().getReadOnlyPlayer(userId);
		if (player == null) {
			return "";
		}

		return player.getUserGroupAttributeDataMgr().getUserGroupAttributeData().getGroupId();
	}

	/**
	 * 判断个人是否有帮派
	 * 
	 * @param userId
	 * @return
	 */
	public static boolean hasGroup(String userId) {
		PlayerIF player = PlayerMgr.getInstance().getReadOnlyPlayer(userId);
		if (player == null) {
			return false;
		}

		UserGroupAttributeDataIF userGroupAttributeData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = userGroupAttributeData.getGroupId();
		return !StringUtils.isEmpty(groupId);
	}
}