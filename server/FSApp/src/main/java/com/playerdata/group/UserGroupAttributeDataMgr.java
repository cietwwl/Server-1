package com.playerdata.group;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.bm.groupCopy.GroupCopyLevelBL;
import com.bm.rank.teaminfo.AngelArrayTeamInfoCall;
import com.bm.rank.teaminfo.AngelArrayTeamInfoHelper;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.common.PlayerEventListener;
import com.rw.support.FriendSupportFactory;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeUtils;
import com.rwbase.common.enu.eStoreType;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.cfg.GroupSkillAttributeCfg;
import com.rwbase.dao.group.pojo.cfg.GroupSkillLevelTemplate;
import com.rwbase.dao.group.pojo.cfg.dao.GroupSkillAttributeCfgDAO;
import com.rwbase.dao.group.pojo.cfg.dao.GroupSkillLevelCfgDAO;
import com.rwbase.dao.group.pojo.db.GroupSkillItem;
import com.rwbase.dao.group.pojo.db.UserGroupAttributeData;
import com.rwbase.dao.group.pojo.db.dao.UserGroupAttributeDataDAO;
import com.rwbase.dao.group.pojo.db.dao.UserGroupAttributeDataHolder;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwproto.GroupCommonProto.GroupPost;

/*
 * @author HC
 * @date 2016年2月19日 上午10:19:46
 * @Description 个人的帮派数据
 */
public class UserGroupAttributeDataMgr implements PlayerEventListener {

	private static UserGroupAttributeDataMgr mgr = new UserGroupAttributeDataMgr();

	public static UserGroupAttributeDataMgr getMgr() {
		return mgr;
	}

	private UserGroupAttributeDataHolder holder;// 个人帮派数据的管理

	protected UserGroupAttributeDataMgr() {
	}

	@Override
	public void notifyPlayerCreated(Player player) {
		if (player.isRobot()) {
			return;
		}
		UserGroupAttributeData data = new UserGroupAttributeData();
		data.setUserId(player.getUserId());
		data.setGroupId("");
		UserGroupAttributeDataDAO.getDAO().update(data);
	}

	@Override
	public void notifyPlayerLogin(Player player) {
		UserGroupAttributeData userGroupData = holder.getUserGroupData();

		if (userGroupData == null) {
			return;
		}
		String groupId = userGroupData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return;
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			return;
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			return;
		}

		// 检查个人成员信息
		GroupMemberDataIF memberData = group.getGroupMemberMgr().getMemberData(player.getUserId(), false);
		if (memberData == null) {
			return;
		}

		userGroupData.setGroupName(groupData.getGroupName());
		// userGroupData.setContribution(memberData.getContribution());
		userGroupData.setDayContribution(memberData.getDayContribution());
		userGroupData.setJoinTime(memberData.getReceiveTime());
	}

	@Override
	public void init(Player player) {
	}

	/**
	 * 获取帮派的个人数据
	 * 
	 * @return
	 */
	public UserGroupAttributeData getUserGroupAttributeData(String userId) {
		return holder.getUserGroupData();
	}

	/**
	 * 重置管理员每天分配奖励次数
	 */
	public void resetAllotGroupRewardCount(String userId) {
		UserGroupAttributeData data = holder.getUserGroupData();
		if (data == null) {
			return;
		}
		Group group = GroupBM.get(data.getGroupId());
		if (group == null) {
			return;
		}
		// 检查职位
		GroupMemberDataIF memberData = group.getGroupMemberMgr().getMemberData(userId, false);
		if (memberData == null) {
			return;
		}

		int post = memberData.getPost();
		if (post != GroupPost.LEADER_VALUE && post != GroupPost.ASSISTANT_LEADER_VALUE) {
			return;
		}
		group.getGroupMemberMgr().resetAllotGroupRewardCount(userId, GroupCopyLevelBL.MAX_ALLOT_COUNT, false);

	}

	/**
	 * 获取个人的帮贡
	 * 
	 * @return
	 */
	public long getUserGroupContribution(String userId) {
		UserGroupAttributeData userGroupData = holder.getUserGroupData();
		if (userGroupData == null) {
			return 0;
		}

		return userGroupData.getContribution();
	}

	/**
	 * 获取个人的帮贡
	 * 
	 * @return
	 */
	public void useUserGroupContribution(String userId, int offContribution) {
		UserGroupAttributeData userGroupData = holder.getUserGroupData();
		if (userGroupData == null) {
			return;
		}

		Group group = GroupBM.get(userGroupData.getGroupId());
		if (group == null) {
			return;
		}

		group.getGroupMemberMgr().updateMemberContribution(userId, offContribution, false);
	}

	/**
	 * 当角色有了帮派的时候，设置数据
	 * 
	 * @param player
	 * @param groupId 有帮派的数据
	 */
	public void updateDataWhenHasGroup(Player player, String groupId, String groupName) {
		UserGroupAttributeData baseData = holder.getUserGroupData();
		baseData.setQuitGroupTime(0);
		baseData.clearApplyGroupIdList();// 清除申请队列
		baseData.setGroupId(groupId);
		baseData.setGroupName(groupName);
		baseData.setJoinTime(System.currentTimeMillis());
		// 同步数据
		updateAndSynUserGroupAttributeData(player);
		notifyGroupSkillAttrData(player);
		// 通知好友更改更新帮派名字
		FriendSupportFactory.getSupport().notifyFriendInfoChanged(player);
		player.getStoreMgr().AddStore();
	}

	/**
	 * 当角色被某个帮派拒绝了之后
	 * 
	 * @param player
	 * @param removeGroupId
	 */
	public void updateDataWhenRefuseByGroup(Player player, String removeGroupId) {
		UserGroupAttributeData baseData = holder.getUserGroupData();
		baseData.removeApplyGroupId(removeGroupId);
		updateAndSynUserGroupAttributeData(player);
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
		baseData.setGroupName("");
		baseData.setQuitGroupTime(quitTime);
		updateAndSynUserGroupAttributeData(player);
		notifyGroupSkillAttrData(player);
		// 通知好友更改更新帮派名字
		FriendSupportFactory.getSupport().notifyFriendInfoChanged(player);
		// 通知阵容更新下名字
		AngelArrayTeamInfoHelper.updateRankingEntry(player, AngelArrayTeamInfoCall.groupCall);
		player.getStoreMgr().removeStore(eStoreType.Union.getOrder());
	}

	/**
	 * 更新个人申请帮派的条目数
	 * 
	 * @param resetTime
	 */
	public void updateAndCheckApplyTimes(String userId, long resetTime) {
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
	public void updateApplyGroupData(Player player, String applyGroupId) {
		UserGroupAttributeData baseData = holder.getUserGroupData();
		baseData.addApplyGroupId(applyGroupId);
		baseData.setGroupApplySize(baseData.getGroupApplySize() + 1);
		updateAndSynUserGroupAttributeData(player);
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
			GameLog.error("学习帮派技能", player.getUserId(), String.format("学习技能Id[%s],技能等级[%s],已经学习的技能等级[%s]。请求学习等级过高", skillId, skillLevel, hasSkillLevel));
			return false;
		}

		userGroupData.addOrUpdateStudySkill(skillId, skillLevel, time, state);
		updateAndSynUserGroupAttributeData(player);
		notifyGroupSkillAttrData(player);
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
	 * 通知所有英雄重算属性
	 * 
	 * @param player
	 */
	private void notifyGroupSkillAttrData(Player player) {
		Enumeration<? extends Hero> herosEnumeration = player.getHeroMgr().getHerosEnumeration(player);
		while (herosEnumeration.hasMoreElements()) {
			Hero hero = herosEnumeration.nextElement();
			if (hero == null) {
				continue;
			}

			hero.getAttrMgr().reCal();
		}
	}

	/**
	 * 更新帮派的名字
	 * 
	 * @param player
	 * @param groupName
	 */
	public void updateGroupName(Player player, String groupName) {
		UserGroupAttributeData userGroupData = holder.getUserGroupData();
		userGroupData.setGroupName(groupName);
		holder.synData(player);
	}

	/**
	 * 更新个人的帮派贡献
	 * 
	 * @param player
	 * @param contribution
	 * @param dayContribution
	 * @param donateTimes 今天捐献的次数
	 */
	public void updateContribution(Player player, int offsetContribution, int dayContribution, int donateTimes) {
		UserGroupAttributeData userGroupData = holder.getUserGroupData();
		userGroupData.setDonateTimes(donateTimes);

		int contribution = userGroupData.getContribution();
		contribution += offsetContribution;
		contribution = contribution < 0 ? 0 : contribution;
		userGroupData.setContribution(contribution);

		userGroupData.setDayContribution(dayContribution);
		holder.flush();
		holder.synData(player);
	}

	/**
	 * 获取帮派增加的属性
	 * 
	 * @return
	 */
	public Map<Integer, AttributeItem> getGroupSkillAttrDataMap(String userId) {
		UserGroupAttributeData userGroupData = holder.getUserGroupData();
		if (userGroupData == null) {
			// GameLog.error("计算英雄帮派属性", userId, "角色没有对应的UserGroupAttributeData数据");
			return null;
		}

		String groupId = userGroupData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {// 没有帮派
			// GameLog.error("计算英雄帮派属性", userId, "角色没有帮派");
			return null;
		}

		if (!userGroupData.hasStudySkill()) {
			// GameLog.error("计算英雄帮派属性", userId, "角色没有学习过任何技能");
			return null;
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			// GameLog.error("计算英雄帮派属性", userId, String.format("[%s]的帮派没有找到数据", groupId));
			return null;
		}

		Map<Integer, Integer> skillMap = new HashMap<Integer, Integer>();

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		Enumeration<GroupSkillItem> researchSkill = groupData.getResearchSkill();
		while (researchSkill.hasMoreElements()) {
			GroupSkillItem skillItem = researchSkill.nextElement();// 帮派研发的技能
			int skillId = Integer.parseInt(skillItem.getId());
			int researchLevel = skillItem.getLevel();// 研发到的等级
			int studySkillLevel = userGroupData.getStudySkillLevel(skillId);
			studySkillLevel = researchLevel <= studySkillLevel ? researchLevel : studySkillLevel;
			if (studySkillLevel <= 0) {// 没学习
				continue;
			}

			skillMap.put(skillId, studySkillLevel);
		}

		return getGroupSkillAttrMap(skillMap);
	}

	/**
	 * 获取帮派技能转换的属性Map
	 * 
	 * @param skillMap
	 * @return
	 */
	public static HashMap<Integer, AttributeItem> getGroupSkillAttrMap(Map<Integer, Integer> skillMap) {
		HashMap<Integer, AttributeItem> map = new HashMap<Integer, AttributeItem>(skillMap.size());

		GroupSkillAttributeCfgDAO cfgDAO = GroupSkillAttributeCfgDAO.getCfgDAO();
		GroupSkillLevelCfgDAO dao = GroupSkillLevelCfgDAO.getDAO();

		for (Entry<Integer, Integer> e : skillMap.entrySet()) {
			int skillId = e.getKey();
			int studySkillLevel = e.getValue();

			GroupSkillLevelTemplate tmp = dao.getSkillLevelTemplate(skillId, studySkillLevel);
			if (tmp == null) {
				continue;
			}

			GroupSkillAttributeCfg skillAttr = cfgDAO.getGroupSkillAttribute(tmp.getAttributeId());
			if (skillAttr == null) {
				continue;
			}

			AttributeUtils.calcAttribute(skillAttr.getAttrDataMap(), skillAttr.getPrecentAttrDataMap(), map);
		}

		return map;
	}

	/**
	 * 更新帮派成员的捐献时间
	 * 
	 * @param userId
	 * @param donateTimes
	 * @param lastDonateTime
	 */
	public void resetMemberDataDonateTimes(String userId, long lastDonateTime) {
		UserGroupAttributeData userGroupData = holder.getUserGroupData();
		if (userGroupData == null) {
			return;
		}

		userGroupData.setDonateTimes(0);
		userGroupData.setLastDonateTime(lastDonateTime);
		userGroupData.setDayContribution(0);
		holder.flush();
		holder.synData(PlayerMgr.getInstance().find(userId));
	}

	/**
	 * 更新帮派成员的捐献时间
	 * 
	 * @param userId
	 * @param donateTimes
	 * @param lastDonateTime
	 */
	public void gmResetMemberDataDonateTimes(String userId, long lastDonateTime) {
		UserGroupAttributeData userGroupData = holder.getUserGroupData();
		if (userGroupData == null) {
			return;
		}

		userGroupData.setDonateTimes(0);
		userGroupData.setLastDonateTime(lastDonateTime);
		holder.flush();
		holder.synData(PlayerMgr.getInstance().find(userId));
	}
}