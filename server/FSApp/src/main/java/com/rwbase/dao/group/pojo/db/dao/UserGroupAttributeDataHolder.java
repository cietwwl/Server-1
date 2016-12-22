package com.rwbase.dao.group.pojo.db.dao;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.dao.group.pojo.db.UserGroupAttributeData;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

/**
 * 角色的Group数据Holder
 * 
 * @author HC
 * @date 2016年2月19日 上午10:10:42
 * @Description
 */
public class UserGroupAttributeDataHolder {

	private static UserGroupAttributeDataHolder holder = new UserGroupAttributeDataHolder();

	public static UserGroupAttributeDataHolder getHolder() {
		return holder;
	}

	private eSynType synType = eSynType.UserGroupAttributeData;// 同步的类型

	protected UserGroupAttributeDataHolder() {
	}

	/**
	 * 获取个人的帮派数据
	 * 
	 * @param userId
	 * @return
	 */
	public UserGroupAttributeData getUserGroupData(String userId) {
		return UserGroupAttributeDataDAO.getDAO().getUserGroupAttributeData(userId);
	}

	/**
	 * 同步个人的帮派数据
	 * 
	 * @param player
	 */
	public void synData(Player player) {
		String userId = player.getUserId();
		UserGroupAttributeData userGroupAttributeData = getUserGroupData(userId);
		if (userGroupAttributeData != null) {
			ClientDataSynMgr.synData(player, userGroupAttributeData, synType, eSynOpType.UPDATE_SINGLE);
		} else {
			GameLog.error("UserGroupAttributeDataHolder", "#synData()", "find UserGroupAttributeData fail:" + userId);
		}
	}

	/**
	 * 更新数据到数据库
	 * 
	 * @param userId
	 */
	public void flush(String userId) {
		UserGroupAttributeDataDAO.getDAO().update(userId);
	}
}