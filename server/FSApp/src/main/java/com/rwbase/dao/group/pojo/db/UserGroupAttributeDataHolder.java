package com.rwbase.dao.group.pojo.db;

import java.util.Collections;
import java.util.List;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.dao.group.pojo.db.dao.UserGroupAttributeDataDAO;
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

	/**
	 * 获取赠送过魂石卡的人列表
	 * 
	 * @param userId
	 * @return
	 */
	public List<String> getPrayList(String userId) {
		UserGroupAttributeData userGroupData = getUserGroupData(userId);
		if (userGroupData == null) {
			return Collections.emptyList();
		}

		long lastPrayTime = userGroupData.getLastResetPrayTime();
		// 是否可以重置，可以重置就先返回赠送过的人列表为空
		if (DateUtils.isResetTime(5, 0, 0, lastPrayTime)) {
			userGroupData.clearPrayList();
			userGroupData.setLastResetPrayTime(DateUtils.getSecondLevelMillis());
			holder.flush(userId);
		}

		return userGroupData.getPrayList();
	}
}