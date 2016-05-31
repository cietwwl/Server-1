package com.playerdata.groupsecret;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rwbase.dao.groupsecret.pojo.UserCreateGroupSecretDataHolder;
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
}