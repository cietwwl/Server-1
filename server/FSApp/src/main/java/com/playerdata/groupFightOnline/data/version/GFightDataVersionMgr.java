package com.playerdata.groupFightOnline.data.version;

import com.bm.group.GroupBM;
import com.playerdata.Player;
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
	}
	
	public static GFightDataVersion fromJson(String versionJson) {
		GFightDataVersion groupDataVersion = JsonUtil.readValue(versionJson, GFightDataVersion.class);
		if(groupDataVersion == null) groupDataVersion = new GFightDataVersion();
		return groupDataVersion;
	}

}
