package com.rw.service.redpoint.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.playerdata.Player;
import com.playerdata.groupsecret.GroupSecretDefendRecordDataMgr;
import com.playerdata.groupsecret.UserCreateGroupSecretDataMgr;
import com.playerdata.groupsecret.UserGroupSecretBaseDataMgr;
import com.rw.service.group.helper.GroupHelper;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretResourceCfg;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretResourceCfgDAO;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.UserCreateGroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.UserGroupSecretBaseData;
import com.rwbase.dao.groupsecret.pojo.db.data.DefendRecord;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwproto.PrivilegeProtos.GroupPrivilegeNames;

public class GroupSecretCollector implements RedPointCollector {

	private List<String> _canRobRedPointList = Arrays.asList("1");

	private void checkDefendRedPoint(Player player, Map<RedPointType, List<String>> map) {
		GroupSecretDefendRecordDataMgr mgr = GroupSecretDefendRecordDataMgr.getMgr();
		List<DefendRecord> defendRecordList = mgr.getSortDefendRecordList(player.getUserId());
		List<String> list = null;
		if (defendRecordList.size() > 0) {
			list = new ArrayList<String>(defendRecordList.size());
			for (int i = 0; i < defendRecordList.size(); i++) {
				DefendRecord record = defendRecordList.get(i);
				if (record.isHasKey()) {
					list.add(String.valueOf(record.getSecretId()));
				}
			}
		}
		if (list != null && list.size() > 0) {
			map.put(RedPointType.GROUP_SECRET_RECORD_KEY, list);
		}
	}

	private void checkAdventureList(Player player, UserGroupSecretBaseData userGroupSecretBaseData, Map<RedPointType, List<String>> map) {
		// 红点的内容是可探索的内容
		int intPrivilege = player.getPrivilegeMgr().getIntPrivilege(GroupPrivilegeNames.mysteryChallengeCount);
		Map<Integer, String> defendSecretIdMap = userGroupSecretBaseData.getDefendSecretIdMap();
		if (intPrivilege > defendSecretIdMap.size()) {
			List<String> list = new ArrayList<String>(intPrivilege - defendSecretIdMap.size());
			for (int i = 0; i < intPrivilege; i++) {
				int pos = i + 1;
				if (!defendSecretIdMap.containsKey(pos)) {
					list.add(String.valueOf(pos));
				}
			}
			map.put(RedPointType.GROUP_SECRET_ADVENTURE, list);
		}
	}

	private void checkRewards(Player player, UserGroupSecretBaseData userGroupSecretBaseData, Map<RedPointType, List<String>> map) {
		UserCreateGroupSecretDataMgr mgr = UserCreateGroupSecretDataMgr.getMgr();
		UserCreateGroupSecretData userCreateGroupSecretData = mgr.get(player.getUserId());
		Map<Integer, String> defendSecretIdMap = userGroupSecretBaseData.getDefendSecretIdMap();
		List<String> list = null;
		if (defendSecretIdMap.size() > 0) {
			long currentTimeMillis = System.currentTimeMillis();
			list = new ArrayList<String>(defendSecretIdMap.size());
			GroupSecretData secretData;
			String secretId;
			GroupSecretResourceCfg groupSecretResTmp;
			for (Iterator<Integer> keyItr = defendSecretIdMap.keySet().iterator(); keyItr.hasNext();) {
				secretId = defendSecretIdMap.get(keyItr.next());
				secretData = userCreateGroupSecretData.getGroupSecretData(Integer.parseInt(secretId.split("_")[1]));
				groupSecretResTmp = GroupSecretResourceCfgDAO.getCfgDAO().getGroupSecretResourceTmp(secretData.getSecretId());
				int needTime = groupSecretResTmp.getNeedTime();
				long endTime = secretData.getCreateTime() + TimeUnit.MILLISECONDS.convert(needTime, TimeUnit.MINUTES);
				if (endTime < currentTimeMillis) {
					list.add(secretId);
				}
			}
		}
		if (list != null && list.size() > 0) {
			map.put(RedPointType.GROUP_SECRET_RES_REWARD, list);
		}
	}

	private void checkRob(Player player, String groupId, UserGroupSecretBaseData userGroupSecretBaseData, Map<RedPointType, List<String>> map) {
		if (userGroupSecretBaseData.getKeyCount() >= 2) {
			// String matchSecretId = userGroupSecretBaseData.getMatchSecretId();
			// List<String> list = null;
			// if (matchSecretId != null && matchSecretId.length() > 0) {
			// list = Arrays.asList(matchSecretId);
			// } else {
			// if (!userGroupSecretBaseData.isHasSearched()) {
			// list = Arrays.asList("1");
			// }
			// }
			// if (list != null) {
			// map.put(RedPointType.GROUP_SECRET_ROB, list);
			// }
			map.put(RedPointType.GROUP_SECRET_ROB, _canRobRedPointList);
		}
	}

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		String groupId = GroupHelper.getInstance().getGroupId(player);
		if (groupId != null && groupId.length() > 0) {
			checkDefendRedPoint(player, map);
			UserGroupSecretBaseDataMgr baseDataMgr = UserGroupSecretBaseDataMgr.getMgr();
			UserGroupSecretBaseData userGroupSecretBaseData = baseDataMgr.get(player.getUserId());
			checkAdventureList(player, userGroupSecretBaseData, map);
			checkRewards(player, userGroupSecretBaseData, map);
			checkRob(player, groupId, userGroupSecretBaseData, map);
		}
	}

	@Override
	public eOpenLevelType getOpenType() {
		return eOpenLevelType.SECRET_AREA;
	}

}
