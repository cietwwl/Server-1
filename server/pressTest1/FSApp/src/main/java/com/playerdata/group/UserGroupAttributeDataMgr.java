package com.playerdata.group;

import com.bm.group.GroupBM;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.common.PlayerEventListener;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.db.UserGroupAttributeData;
import com.rwbase.dao.group.pojo.db.dao.UserGroupAttributeDataDAO;
import com.rwbase.dao.group.pojo.db.dao.UserGroupAttributeDataHolder;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;

/*
 * @author HC
 * @date 2016年2月19日 上午10:19:46
 * @Description 个人的帮派数据
 */
public class UserGroupAttributeDataMgr implements PlayerEventListener{
	private UserGroupAttributeDataHolder holder;// 个人帮派数据的管理
	private String userId;// 成员Id

	public UserGroupAttributeDataMgr(String userId) {
		this.userId = userId;
		holder = new UserGroupAttributeDataHolder(userId);
	}
	
	@Override
	public void notifyPlayerCreated(Player player) {
		UserGroupAttributeData data = new UserGroupAttributeData();
		data.setUserId(userId);
		data.setGroupId("");
		UserGroupAttributeDataDAO.getDAO().update(data);
	}

	@Override
	public void notifyPlayerLogin(Player player) {
	}

	@Override
	public void init(Player player) {
	}

	/**
	 * 获取帮派的个人数据
	 * 
	 * @return
	 */
	public UserGroupAttributeDataIF getUserGroupAttributeData() {
		return holder.getUserGroupData();
	}

	/**
	 * 获取个人的帮贡
	 * 
	 * @return
	 */
	public long getUserGroupContribution() {
		UserGroupAttributeData userGroupData = holder.getUserGroupData();
		if (userGroupData == null) {
			return 0;
		}

		Group group = GroupBM.get(userGroupData.getGroupId());
		if (group == null) {
			return 0;
		}

		GroupMemberDataIF memberData = group.getGroupMemberMgr().getMemberData(userId, false);
		if (memberData == null) {
			return 0;
		}

		return memberData.getContribution();
	}

	/**
	 * 获取个人的帮贡
	 * 
	 * @return
	 */
	public void useUserGroupContribution(int offContribution) {
		UserGroupAttributeData userGroupData = holder.getUserGroupData();
		if (userGroupData == null) {
			return;
		}

		Group group = GroupBM.get(userGroupData.getGroupId());
		if (group == null) {
			return;
		}

		group.getGroupMemberMgr().updateMemberContribution(userId, offContribution);
	}

	/**
	 * 当角色有了帮派的时候，设置数据
	 * 
	 * @param player
	 * @param groupId 有帮派的数据
	 */
	public void updateDataWhenHasGroup(Player player, String groupId) {
		UserGroupAttributeData baseData = holder.getUserGroupData();
		baseData.setQuitGroupTime(0);
		baseData.clearApplyGroupIdList();// 清除申请队列
		baseData.setGroupId(groupId);
		// 同步数据
		updateAndSynUserGroupAttributeData(player);
	}

	/**
	 * 当角色被某个帮派拒绝了之后
	 * 
	 * @param player
	 * @param removeGroupId
	 */
	public void updateDataWhenRefuseByGroup(String removeGroupId) {
		UserGroupAttributeData baseData = holder.getUserGroupData();
		baseData.removeApplyGroupId(removeGroupId);
		holder.flush();
	}

	/**
	 * 更新发送邮件的时间
	 * 
	 * @param player 角色
	 * @param sendTime 发送邮件的时间
	 */
	public void updateSendEmailTime(Player player, long sendTime) {
		UserGroupAttributeData baseData = holder.getUserGroupData();
		baseData.setSendEmailTime(sendTime);
		updateAndSynUserGroupAttributeData(player);
	}

	/**
	 * 当角色被踢出帮派或者退出时更新数据
	 * 
	 * @param player
	 * @param quitTime
	 */
	public void updateDataWhenQuitGroup(Player player, long quitTime) {
		UserGroupAttributeData baseData = holder.getUserGroupData();
		baseData.setGroupId("");
		baseData.setQuitGroupTime(quitTime);
		updateAndSynUserGroupAttributeData(player);
	}

	/**
	 * 更新个人申请帮派的条目数
	 * 
	 * @param resetTime
	 */
	public void updateAndCheckApplyTimes(long resetTime) {
		UserGroupAttributeData baseData = holder.getUserGroupData();
		baseData.setLastResetApplyTime(resetTime);
		baseData.setGroupApplySize(0);
		holder.flush();
	}

	/**
	 * 更新个人申请帮派的数据
	 * 
	 * @param applyGroupId
	 */
	public void updateApplyGroupData(String applyGroupId) {
		UserGroupAttributeData baseData = holder.getUserGroupData();
		baseData.addApplyGroupId(applyGroupId);
		baseData.setGroupApplySize(baseData.getGroupApplySize() + 1);
		holder.flush();
	}

	/**
	 * 更新角色学习技能的数据
	 * 
	 * @param player 角色
	 * @param skillId 技能Id
	 * @param skillLevel 技能等级
	 * @param time <b>【注】如果不设置这个数据，就填入-1</b>
	 * @param state <b>【注】如果不设置这个数据，就填入-1</b>
	 * @return
	 */
	public boolean updateUserGroupDataWhenStudySkill(Player player, int skillId, int skillLevel, long time, int state) {
		UserGroupAttributeData userGroupData = holder.getUserGroupData();
		if (userGroupData == null) {
			return false;
		}

		int hasSkillLevel = userGroupData.getStudySkillLevel(skillId);
		if (hasSkillLevel == skillLevel) {
			GameLog.error("学习帮派技能", player.getUserId(), String.format("学习技能Id[%s],技能等级[%s],已经学习的技能等级[%s]", skillId, skillLevel, hasSkillLevel));
			return false;
		}

		if (skillLevel - hasSkillLevel != 1) {
			GameLog.error("学习帮派技能", player.getUserId(),
					String.format("学习技能Id[%s],技能等级[%s],已经学习的技能等级[%s]。请求学习等级过高", skillId, skillLevel, hasSkillLevel));
			return false;
		}

		userGroupData.addOrUpdateStudySkill(skillId, skillLevel, time, state);
		synUserGroupData(player);
		holder.incrementSkillVersion();
		synUserSkillData(player, -1);
		return true;
	}

	/**
	 * 更新并且同步个人帮派数据
	 * 
	 * @param player 角色
	 * @param userGroupAttributeData 个人帮派数据
	 */
	private void updateAndSynUserGroupAttributeData(Player player) {
		holder.flush();
		holder.synData(player);
	}

	/**
	 * 推送个人的帮派数据
	 * 
	 * @param player
	 */
	public void synUserGroupData(Player player) {
		holder.synData(player);
	}

	/**
	 * 推送个人帮派学习技能的数据
	 * 
	 * @param player
	 * @param version
	 */
	public void synUserSkillData(Player player, int version) {
		holder.synSkillData(player, version);
	}

}