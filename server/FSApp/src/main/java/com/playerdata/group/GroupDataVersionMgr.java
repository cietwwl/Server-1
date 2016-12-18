package com.playerdata.group;

import com.bm.group.GroupBM;
import com.playerdata.Player;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.dao.group.pojo.Group;

public class GroupDataVersionMgr {

	public static void synByVersion(Player player, String versionJson) {
		// 基础数据版本号
		String groupId = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData().getGroupId();
		Group group = GroupBM.getInstance().get(groupId);
		if (group == null) {
			return;
		}

		GroupDataVersion groupDataVersion = fromJson(versionJson);
		if (groupDataVersion == null) {
			return;
		}

		group.synGroupData(player, groupDataVersion.getGroupBaseData());
		group.synGroupMemberData(player, false, groupDataVersion.getGroupMemberData());
		group.synGroupMemberData(player, true, groupDataVersion.getApplyMemberData());
		group.synGroupSkillData(player, groupDataVersion.getResearchSkill());
	}

	public static GroupDataVersion fromJson(String versionJson) {
		GroupDataVersion groupDataVersion = JsonUtil.readValue(versionJson, GroupDataVersion.class);
		return groupDataVersion;
	}

}
