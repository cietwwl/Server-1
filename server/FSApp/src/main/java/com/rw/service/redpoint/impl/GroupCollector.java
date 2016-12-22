package com.rw.service.redpoint.impl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.bm.group.GroupMemberMgr;
import com.playerdata.Player;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.cfg.GroupSkillLevelTemplate;
import com.rwbase.dao.group.pojo.cfg.dao.GroupFunctionCfgDAO;
import com.rwbase.dao.group.pojo.cfg.dao.GroupSkillLevelCfgDAO;
import com.rwbase.dao.group.pojo.db.GroupSkillItem;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwproto.GroupCommonProto.GroupFunction;

/*
 * @author HC
 * @date 2016年7月12日 下午5:26:17
 * @Description 
 */
public class GroupCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		String userId = player.getUserId();
		UserGroupAttributeDataIF userGroupAttributeData = UserGroupAttributeDataMgr.getMgr().getUserGroupAttributeData(userId);
		if (userGroupAttributeData == null) {
			return;
		}

		String groupId = userGroupAttributeData.getGroupId();
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

		GroupMemberMgr groupMemberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF memberData = groupMemberMgr.getMemberData(userId, false);
		if (memberData == null) {
			return;
		}

		// 个人学习技能的列表
		GroupSkillLevelCfgDAO dao = GroupSkillLevelCfgDAO.getDAO();
		int contribution = userGroupAttributeData.getContribution();// 当前个人的贡献值
		boolean canStudySkill = false;
		Enumeration<GroupSkillItem> researchSkill = groupData.getResearchSkill();
		while (researchSkill.hasMoreElements()) {
			GroupSkillItem skillItem = researchSkill.nextElement();
			if (skillItem == null) {
				continue;
			}

			int researchSkillLevel = skillItem.getLevel();

			int skillId = Integer.parseInt(skillItem.getId());
			int studySkillLevel = userGroupAttributeData.getStudySkillLevel(skillId);
			if (researchSkillLevel <= studySkillLevel) {
				continue;
			}

			int checkLevel = studySkillLevel <= 0 ? 1 : (studySkillLevel + 1);
			GroupSkillLevelTemplate skillLevelTemplate = dao.getSkillLevelTemplate(skillId, checkLevel);
			if (skillLevelTemplate == null) {
				continue;
			}

			int studyNeedContribution = skillLevelTemplate.getStudyNeedContribution();
			if (studyNeedContribution > contribution) {
				continue;
			}

			canStudySkill = true;
			break;
		}

		if (canStudySkill) {
			map.put(RedPointType.GROUP_WINDOW_SKILL_LEARN, Collections.EMPTY_LIST);
		}

		// 帮派入帮管理界面
		String tip = GroupFunctionCfgDAO.getDAO().canUseFunction(GroupFunction.MEMBER_RECEIVE_VALUE, memberData.getPost(), groupData.getGroupLevel());
		if (StringUtils.isEmpty(tip)) {
			if (groupMemberMgr.getApplyMemberSize() > 0) {
				map.put(RedPointType.GROUP_WINDOW_MANAGER_ENTER_GROUP, Collections.EMPTY_LIST);
			}
		}
	}

	@Override
	public eOpenLevelType getOpenType() {
		return null;
	}
}