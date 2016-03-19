package com.rwbase.dao.group.pojo.db.dao;

import java.util.concurrent.atomic.AtomicInteger;

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
	private static final eSynType skillSynType = eSynType.GroupStudySkill;// 学习技能的同步类型

	private final String userId;
	private AtomicInteger skillVersion = new AtomicInteger(1);// 个人学习帮派技能的版本号

	public UserGroupAttributeDataHolder(String userId) {
		// userGroupAttributeData =
		// UserGroupAttributeDataDAO.getDAO().getUserGroupAttributeData(userId);
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
		UserGroupAttributeData userData = UserGroupAttributeDataDAO.getDAO().getUserGroupAttributeData(userId);
		if (userData != null) {
			ClientDataSynMgr.synData(player, userData, synType, eSynOpType.UPDATE_SINGLE);
		} else {
			GameLog.error("UserGroupAttributeDataHolder", "#synData()", "find UserGroupAttributeData fail:" + userId);
		}
	}

	/**
	 * 更新个人技能的数据
	 * 
	 * @param player
	 * @param version
	 */
	public void synSkillData(Player player, int version) {
		int ver = skillVersion.get();
		if (ver == version) {
			return;
		}
		UserGroupAttributeData dataDAO = UserGroupAttributeDataDAO.getDAO().getUserGroupAttributeData(userId);
		if (dataDAO != null) {
			ClientDataSynMgr.synDataList(player, dataDAO.getSkillItemList(), skillSynType, eSynOpType.UPDATE_LIST, ver);
		}else{
			GameLog.error("UserGroupAttributeDataHolder", "#synSkillData()", "find UserGroupAttributeData fail:" + userId);
		}
	}

	/**
	 * 增加个人帮派技能数据的版本号
	 */
	public void incrementSkillVersion() {
		skillVersion.incrementAndGet();
	}

	/**
	 * 保存到数据库
	 */
	public void flush() {
		UserGroupAttributeDataDAO.getDAO().update(this.userId);
	}
}