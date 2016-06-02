package com.playerdata.groupsecret;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import com.bm.rank.groupsecretmatch.GroupSecretMatchRankAttribute;
import com.rwbase.dao.groupsecret.GroupSecretHelper;
import com.rwbase.dao.groupsecret.GroupSecretMatchHelper;
import com.rwbase.dao.groupsecret.GroupSecretMatchHelper.IUpdateSecretStateCallBack;
import com.rwbase.dao.groupsecret.pojo.UserCreateGroupSecretDataHolder;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretResourceTemplate;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretResourceCfgDAO;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.UserCreateGroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.data.DefendUserInfoData;

/*
 * @author HC
 * @date 2016年5月26日 下午10:02:23
 * @Description 
 */
public class UserCreateGroupSecretDataMgr {
	private static UserCreateGroupSecretDataMgr mgr = new UserCreateGroupSecretDataMgr();

	public static UserCreateGroupSecretDataMgr getMgr() {
		return mgr;
	}

	UserCreateGroupSecretDataMgr() {
	}

	/**
	 * 获取秘境数据
	 * 
	 * @param id
	 * @return
	 */
	public UserCreateGroupSecretData get(String userId) {
		return UserCreateGroupSecretDataHolder.getHolder().get(userId);
	}

	/**
	 * 刷新秘境的数据
	 * 
	 * @param userId
	 */
	public void updateData(String userId) {
		UserCreateGroupSecretDataHolder.getHolder().updateData(userId);
	}

	/**
	 * 增加秘境
	 * 
	 * @param userId
	 * @param secretData
	 */
	public synchronized void addGroupSecretData(String userId, GroupSecretData secretData) {
		UserCreateGroupSecretData userCreateGroupSecretData = get(userId);
		if (userCreateGroupSecretData == null) {
			return;
		}

		userCreateGroupSecretData.addGroupSecretData(secretData);
		updateData(userId);
	}

	/**
	 * 移除某个索引位置上的阵容信息
	 * 
	 * @param userId
	 * @param index
	 * @param id
	 */
	public void removeDefendInfoData(String userId, int index, int id) {
		UserCreateGroupSecretData userCreateGroupSecretData = get(userId);
		if (userCreateGroupSecretData == null) {
			return;
		}

		GroupSecretData groupSecretData = userCreateGroupSecretData.getGroupSecretData(id);
		if (groupSecretData == null) {
			return;
		}

		groupSecretData.removeDefendUserInfoData(index);
		if (groupSecretData.getDefendMap().isEmpty()) {
			userCreateGroupSecretData.deleteGroupSecretDataById(id);
		}
		updateData(userId);
	}

	/**
	 * 更换驻守的阵容信息
	 * 
	 * @param userId
	 * @param index
	 * @param id
	 * @param fighting
	 * @param changeTime
	 * @param proRes
	 * @param proGS
	 * @param proGE
	 * @param defendHeroList
	 */
	public List<String> changeDefendTeamInfo(String userId, int index, int id, int fighting, long changeTime, int proRes, int proGS, int proGE, List<String> defendHeroList) {
		UserCreateGroupSecretData userCreateGroupSecretData = get(userId);
		if (userCreateGroupSecretData == null) {
			return Collections.emptyList();
		}

		GroupSecretData groupSecretData = userCreateGroupSecretData.getGroupSecretData(id);
		if (groupSecretData == null) {
			return Collections.emptyList();
		}

		List<String> changeList = new ArrayList<String>();
		DefendUserInfoData defendUserInfoData = groupSecretData.getDefendUserInfoData(index);
		if (defendUserInfoData != null) {
			changeList = defendUserInfoData.changeDefendHeroList(defendHeroList);
			defendUserInfoData.setChangeTeamTime(changeTime);
			defendUserInfoData.setFighting(fighting);
			defendUserInfoData.setProRes(proRes);
			defendUserInfoData.setProGS(proGS);
			defendUserInfoData.setProGE(proGE);
		}
		updateData(userId);

		return changeList;
	}

	/**
	 * 更新成员秘境被掠夺的资源数量
	 * 
	 * @param player
	 * @param id
	 * @param robRes
	 * @param robGS
	 * @param robGE
	 */
	public void updateGroupSecretRobInfo(String userId, int id, int[] robRes, int[] robGS, int[] robGE) {
		UserCreateGroupSecretData userCreateGroupSecretData = get(userId);
		if (userCreateGroupSecretData == null) {
			return;
		}

		GroupSecretData groupSecretData = userCreateGroupSecretData.getGroupSecretData(id);
		if (groupSecretData == null) {
			return;
		}

		final int robTimes = groupSecretData.getRobTimes() + 1;
		groupSecretData.setRobTimes(robTimes);

		final long now = System.currentTimeMillis();

		IUpdateSecretStateCallBack call = new IUpdateSecretStateCallBack() {

			@Override
			public boolean call(GroupSecretMatchRankAttribute attr) {
				int cfgId = attr.getCfgId();

				GroupSecretResourceTemplate cfg = GroupSecretResourceCfgDAO.getCfgDAO().getGroupSecretResourceTmp(cfgId);
				if (cfg != null) {
					if (robTimes >= cfg.getRobCount()) {
						attr.setRobMaxProtectState(now);
					} else {
						attr.setRobProtectState(now);
					}
				} else {
					attr.setRobProtectState(now);
				}

				return false;
			}
		};

		String generateCacheSecretId = GroupSecretHelper.generateCacheSecretId(userId, id);
		GroupSecretMatchHelper.updateGroupSecretState(generateCacheSecretId, call);// 更新秘境的状态

		Enumeration<DefendUserInfoData> values = groupSecretData.getEnumerationValues();
		while (values.hasMoreElements()) {
			DefendUserInfoData userInfo = values.nextElement();
			if (userInfo == null) {
				continue;
			}

			int index = userInfo.getIndex();
			// 设置掠夺的资源数量
			userInfo.setRobRes(userInfo.getRobRes() + robRes[index - 1]);
			userInfo.setRobGS(userInfo.getRobGS() + robGS[index - 1]);
			userInfo.setRobGE(userInfo.getRobGE() + robGE[index - 1]);
		}

		updateData(userId);
	}
	// /**
	// * 同步秘境的数据
	// *
	// * @param player
	// */
	// private void updateSingleData(Player player, GroupSecretData data) {
	// GroupSecretDataSynData info = GroupSecretHelper.parseGroupSecretData2Msg(data, player.getUserId());
	// // 同步数据
	// SecretBaseInfoSynData base = info.getBase();
	// if (base != null) {
	// SecretBaseInfoSynDataHolder.getHolder().updateSingleData(player, base);
	// }
	//
	// SecretTeamInfoSynData team = info.getTeam();
	// if (team == null) {
	// SecretTeamInfoSynDataHolder.getHolder().updateSingleData(player, team);
	// }
	// }
}