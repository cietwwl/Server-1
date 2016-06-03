package com.rwbase.dao.groupsecret;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.groupsecret.UserCreateGroupSecretDataMgr;
import com.playerdata.groupsecret.UserGroupSecretBaseDataMgr;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.UserCreateGroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.UserGroupSecretBaseData;
import com.rwbase.dao.groupsecret.syndata.SecretTeamInfoSynData;
import com.rwbase.dao.groupsecret.syndata.base.GroupSecretDataSynData;

/*
 * @author HC
 * @date 2016年6月3日 下午4:45:34
 * @Description 
 */
public class GroupSecretVersionMgr {
	public static void synByVersion(Player player, String versionJson) {
		GroupSecretVersion groupSecretVersion = fromJson(versionJson);
		if (groupSecretVersion == null) {
			return;
		}

		Map<String, Integer> map = groupSecretVersion.getMap();

		String userId = player.getUserId();
		// 个人的秘境数据
		UserGroupSecretBaseData userGroupSecretData = UserGroupSecretBaseDataMgr.getMgr().get(userId);

		// // 同步秘境基础数据
		// List<SecretBaseInfoSynData> baseInfoList = new ArrayList<SecretBaseInfoSynData>();
		// 同步秘境的防守信息
		List<SecretTeamInfoSynData> teamInfoList = new ArrayList<SecretTeamInfoSynData>();

		// 检查密境列表
		List<String> defendSecretIdList = userGroupSecretData.getDefendSecretIdList();
		for (int i = 0, size = defendSecretIdList.size(); i < size; i++) {
			String id = defendSecretIdList.get(i);
			String[] idArr = GroupSecretHelper.parseString2UserIdAndSecretId(id);
			UserCreateGroupSecretData userCreateGroupSecretData = UserCreateGroupSecretDataMgr.getMgr().get(idArr[0]);
			if (userCreateGroupSecretData == null) {
				continue;
			}

			GroupSecretData data = userCreateGroupSecretData.getGroupSecretData(Integer.parseInt(idArr[1]));
			if (data == null) {
				continue;
			}

			if (map != null && map.containsKey(id) && (map.get(id) == data.getVersion())) {
				continue;
			}

			GroupSecretDataSynData synData = GroupSecretHelper.parseGroupSecretData2Msg(data, userId);
			if (synData == null) {
				continue;
			}

			// SecretBaseInfoSynData base = synData.getBase();
			SecretTeamInfoSynData team = synData.getTeam();
			// if (base != null) {
			// baseInfoList.add(base);
			// }

			if (team != null) {
				teamInfoList.add(team);
			}
		}

		// 检查匹配到的人
		GroupSecretDataSynData matchSecretInfo = GroupSecretHelper.fillMatchSecretInfo(player, groupSecretVersion.getEnemyVersion());
		if (matchSecretInfo != null) {
			// SecretBaseInfoSynData base = matchSecretInfo.getBase();
			SecretTeamInfoSynData team = matchSecretInfo.getTeam();
			// if (base != null) {
			// baseInfoList.add(base);
			// }

			if (team != null) {
				teamInfoList.add(team);
			}
		}

		// player.getBaseHolder().synAllData(player, baseInfoList);
		player.getTeamHolder().synAllData(player, teamInfoList);
	}

	public static GroupSecretVersion fromJson(String versionJson) {
		GroupSecretVersion groupSecretVersion = JsonUtil.readValue(versionJson, GroupSecretVersion.class);
		return groupSecretVersion;
	}
}