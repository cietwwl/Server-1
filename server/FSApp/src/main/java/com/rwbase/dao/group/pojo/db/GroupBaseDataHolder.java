package com.rwbase.dao.group.pojo.db;

import java.util.concurrent.atomic.AtomicInteger;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.dao.group.pojo.db.dao.GroupBaseDataDAO;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

/**
 * 帮派的基础数据holder
 * 
 * @author HC
 * @date 2016年2月19日 下午12:19:55
 * @Description
 */
public class GroupBaseDataHolder {
	// private GroupBaseData groupData;// 帮派基础数据
	private static final eSynType groupDataSynType = eSynType.GroupBaseData;// 同步基础数据
	private static final eSynType groupSkillSynType = eSynType.GroupResearchSkill;// 帮派研发技能
	/** 帮派基础数据的版本号 */
	private AtomicInteger groupDataVersion = new AtomicInteger(1);
	private AtomicInteger groupSkillVersion = new AtomicInteger(1);// 帮派研发技能的版本号
	private String groupId;// 帮派Id

	public GroupBaseDataHolder(String groupId) {
		this.groupId = groupId;
		// groupData = GroupBaseDataDAO.getDAO().getGroupData(groupId);
	}

	/**
	 * 获取帮派数据
	 * 
	 * @return
	 */
	public GroupBaseData getGroupData() {
		return GroupBaseDataDAO.getDAO().getGroupData(groupId);
	}

	/**
	 * 同步数据
	 * 
	 * @param player 角色
	 */
	public void synGroupData(Player player, int version) {
		if (groupDataVersion.get() == version) {
			return;
		}

		GroupBaseData groupData = getGroupData();
		if (groupData == null) {
			return;
		}

		ClientDataSynMgr.synData(player, groupData, groupDataSynType, eSynOpType.UPDATE_SINGLE, groupDataVersion.get());
	}

	/**
	 * 同步帮派技能的数据
	 * 
	 * @param player
	 */
	public void synGroupSkillData(Player player, int version) {
		int skillVersion = groupSkillVersion.get();
		if (skillVersion == version) {
			return;
		}

		GroupBaseData groupData = getGroupData();
		if (groupData == null) {
			return;
		}

		ClientDataSynMgr.synDataList(player, groupData.getResearchSkillList(), groupSkillSynType, eSynOpType.UPDATE_LIST, skillVersion);
	}

	/**
	 * 更新数据到数据库
	 */
	public void updateGroupData2DB() {
		GroupBaseDataDAO.getDAO().update(groupId);
	}

	/**
	 * 更新帮派数据的版本号
	 */
	public void incrementGroupDataVersion() {
		groupDataVersion.incrementAndGet();
	}

	/**
	 * 更新帮派技能的版本号
	 */
	public void incrementGroupSkillVersion() {
		groupSkillVersion.incrementAndGet();
	}

	public void setGroupSupplier(int s) {
		getGroupData().setSupplies(s);
		incrementGroupDataVersion();
	}
}