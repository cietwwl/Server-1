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

	// private UserGroupAttributeData userGroupAttributeData;// 个人的帮派数据
	private static final eSynType synType = eSynType.UserGroupAttributeData;// 同步的类型
	private final String userId;

	// private static final eSynType skillSynType = eSynType.GroupStudySkill;//
	// 学习技能的同步类型

	// private AtomicInteger skillVersion = new AtomicInteger(1);// 个人学习帮派技能的版本号

	public UserGroupAttributeDataHolder(String userId) {
		this.userId = userId;
	}

	public UserGroupAttributeData getUserGroupData() {
		return UserGroupAttributeDataDAO.getDAO().getUserGroupAttributeData(userId);
	}

	/**
	 * 同步个人的帮派数据
	 * 
	 * @param player
	 */
	public void synData(Player player) {
		UserGroupAttributeData userGroupAttributeData = UserGroupAttributeDataDAO.getDAO().getUserGroupAttributeData(userId);
		if (userGroupAttributeData != null) {
			ClientDataSynMgr.synData(player, userGroupAttributeData, synType, eSynOpType.UPDATE_SINGLE);
		} else {
			GameLog.error("UserGroupAttributeDataHolder", "#synData()", "find UserGroupAttributeData fail:" + userId);
		}
	}

	// /**
	// * 更新个人技能的数据
	// *
	// * @param player
	// * @param version
	// */
	// public void synSkillData(Player player, int version) {
	// int ver = skillVersion.get();
	// if (ver == version) {
	// return;
	// }
	//
	// ClientDataSynMgr.synDataList(player,
	// userGroupAttributeData.getSkillItemList(), skillSynType,
	// eSynOpType.UPDATE_LIST, ver);
	// }
	//
	// /**
	// * 增加个人帮派技能数据的版本号
	// */
	// public void incrementSkillVersion() {
	// skillVersion.incrementAndGet();
	// }

	/**
	 * 保存到数据库
	 */
	public void flush() {
		UserGroupAttributeDataDAO.getDAO().update(userId);
	}
}