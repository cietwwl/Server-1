package com.rwbase.dao.groupsecret;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bm.group.GroupBM;
import com.playerdata.Player;
import com.playerdata.groupsecret.GroupSecretDefendRecordDataMgr;
import com.playerdata.groupsecret.UserCreateGroupSecretDataMgr;
import com.playerdata.groupsecret.UserGroupSecretBaseDataMgr;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.UserCreateGroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.UserGroupSecretBaseData;
import com.rwbase.dao.groupsecret.syndata.SecretBaseInfoSynData;
import com.rwbase.dao.groupsecret.syndata.SecretTeamInfoSynData;
import com.rwbase.dao.groupsecret.syndata.base.GroupSecretDataSynData;
import com.rwproto.DataSynProtos.eSynType;

/*
 * @author HC
 * @date 2016年6月3日 下午4:45:34
 * @Description 
 */
public class GroupSecretVersionMgr {
	/**
	 * 同步成员的信息
	 * 
	 * @param player
	 * @param versionJson
	 */
	public static void synByVersion(Player player, String versionJson) {
		GroupSecretVersion groupSecretVersion = fromJson(versionJson);
		if (groupSecretVersion == null) {
			return;
		}

		Map<String, Integer> map = groupSecretVersion.getMap();

		String userId = player.getUserId();
		int level = player.getLevel();
		// 个人的秘境数据
		UserGroupSecretBaseData userGroupSecretData = UserGroupSecretBaseDataMgr.getMgr().get(userId);

		// 同步秘境基础数据
		List<SecretBaseInfoSynData> baseInfoList = new ArrayList<SecretBaseInfoSynData>();
		// 同步秘境的防守信息
		List<SecretTeamInfoSynData> teamInfoList = new ArrayList<SecretTeamInfoSynData>();

		// 检查密境列表
//		List<String> defendSecretIdList = userGroupSecretData.getDefendSecretIdList();
//		for (int i = 0, size = defendSecretIdList.size(); i < size; i++) {
		Map<Integer, String> defendSecretIdMap = userGroupSecretData.getDefendSecretIdMap();
		for(Iterator<Map.Entry<Integer, String>> itr = defendSecretIdMap.entrySet().iterator();itr.hasNext();) {
//			String id = defendSecretIdList.get(i);
			Map.Entry<Integer, String> entry = itr.next();
			String id = entry.getValue();
			String[] idArr = GroupSecretHelper.parseString2UserIdAndSecretId(id);
			UserCreateGroupSecretData userCreateGroupSecretData = UserCreateGroupSecretDataMgr.getMgr().get(idArr[0]);
			if (userCreateGroupSecretData == null) {
				continue;
			}

			GroupSecretData data = userCreateGroupSecretData.getGroupSecretData(Integer.parseInt(idArr[1]));
			if (data == null) {
				continue;
			}

			boolean contains = map != null && map.containsKey(id);
			if (contains && (map.get(id) == data.getVersion())) {
				continue;
			}

			GroupSecretDataSynData synData = GroupSecretHelper.parseGroupSecretData2Msg(entry.getKey(), data, userId, level);
			if (synData == null) {
				continue;
			}

			SecretBaseInfoSynData base = synData.getBase();
			SecretTeamInfoSynData team = synData.getTeam();
			if (!contains && base != null) {
				baseInfoList.add(base);
			}

			if (team != null) {
				teamInfoList.add(team);
			}
		}

		// 检查匹配到的人
		GroupSecretDataSynData matchSecretInfo = GroupSecretHelper.fillMatchSecretInfo(player, groupSecretVersion.getEnemyVersion());
		if (matchSecretInfo != null) {
			SecretBaseInfoSynData base = matchSecretInfo.getBase();
			SecretTeamInfoSynData team = matchSecretInfo.getTeam();
			if (base != null) {
				baseInfoList.add(base);
			}

			if (team != null) {
				teamInfoList.add(team);
			}
		}

		player.getBaseHolder().synAllData(player, baseInfoList);
		player.getTeamHolder().synAllData(player, teamInfoList);

		// 防守记录
		int defendRecordVersion = groupSecretVersion.getDefendRecordVersion();
		int version = player.getDataSynVersionHolder().getVersion(eSynType.SECRETAREA_DEF_RECORD);
		if (defendRecordVersion != version) {
			GroupSecretDefendRecordDataMgr.getMgr().synData(player);
		}

		// 发送帮派成员的列表

		String groupId = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData().getGroupId();
		Group group = GroupBM.get(groupId);
		if (group != null) {
			group.synGroupMemberData(player, false, groupSecretVersion.getMemberVersion());
		}
	}

	public static GroupSecretVersion fromJson(String versionJson) {
		GroupSecretVersion groupSecretVersion = JsonUtil.readValue(versionJson, GroupSecretVersion.class);
		return groupSecretVersion;
	}
}