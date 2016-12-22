package com.rw.service.group;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.bm.group.GroupBaseDataMgr;
import com.bm.group.GroupMemberMgr;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.rw.service.group.helper.GroupCmdHelper;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.cfg.GroupSkillCfg;
import com.rwbase.dao.group.pojo.cfg.GroupSkillLevelTemplate;
import com.rwbase.dao.group.pojo.cfg.dao.GroupFunctionCfgDAO;
import com.rwbase.dao.group.pojo.cfg.dao.GroupSkillCfgDAO;
import com.rwbase.dao.group.pojo.cfg.dao.GroupSkillLevelCfgDAO;
import com.rwbase.dao.group.pojo.db.GroupLog;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;
import com.rwproto.GroupCommonProto.GroupFunction;
import com.rwproto.GroupCommonProto.GroupLogType;
import com.rwproto.GroupCommonProto.RequestType;
import com.rwproto.GroupSkillServiceProto.GroupSkillCommonReqMsg;
import com.rwproto.GroupSkillServiceProto.GroupSkillCommonRspMsg;

/*
 * @author HC
 * @date 2016年2月18日 下午3:16:30
 * @Description 帮派的基础处理
 */
public class GroupSkillHandler {
	private static GroupSkillHandler handler = new GroupSkillHandler();

	public static GroupSkillHandler getInstance() {
		return handler;
	}

	protected GroupSkillHandler() {
	}

	/**
	 * 研发帮派技能
	 *
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString researchGroupSkillHandler(Player player, GroupSkillCommonReqMsg req) {
		String userId = player.getUserId();

		GroupSkillCommonRspMsg.Builder commonRsp = GroupSkillCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.RESEARCH_GROUP_SKILL_TYPE);

		// 检查角色有没有帮派
		UserGroupAttributeDataIF baseData = UserGroupAttributeDataMgr.getMgr().getUserGroupAttributeData(userId);
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupSkillFillFailMsg(commonRsp, "您当前还没有帮派");
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("研发帮派技能", userId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupSkillFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		GroupBaseDataMgr groupBaseDataMgr = group.getGroupBaseDataMgr();
		GroupBaseDataIF groupData = groupBaseDataMgr.getGroupData();
		if (groupData == null) {
			GameLog.error("研发帮派技能", userId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupSkillFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		GroupMemberDataIF memberData = group.getGroupMemberMgr().getMemberData(userId, false);
		if (memberData == null) {
			GameLog.error("研发帮派技能", userId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, userId));
			return GroupCmdHelper.groupSkillFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		// 检查个人的权限
		int post = memberData.getPost();// 职位
		int groupLevel = groupData.getGroupLevel();// 帮派等级

		String tip = GroupFunctionCfgDAO.getDAO().canUseFunction(GroupFunction.RESEARCH_GROUP_SKILL_VALUE, post, groupLevel);
		if (!StringUtils.isEmpty(tip)) {
			return GroupCmdHelper.groupSkillFillFailMsg(commonRsp, tip);
		}

		// 检查技能的数据
		int skillId = req.getSkillId();// 请求的技能Id
		int skillLevel = req.getSkillLevel();// 请求的技能等级

		// 检查请求研发的技能条件满不满足
		GroupSkillCfgDAO skillCfgDao = GroupSkillCfgDAO.getDAO();
		GroupSkillCfg skillCfg = skillCfgDao.getSkillCfg(skillId);
		if (skillCfg == null) {
			GameLog.error("研发帮派技能", userId, String.format("帮派技能Id[%s]没有发现到GroupSkillCfg配置", skillId));
			return GroupCmdHelper.groupSkillFillFailMsg(commonRsp, "技能不存在");
		}

		// 检查帮派等级
		GroupSkillLevelCfgDAO skillLevelDao = GroupSkillLevelCfgDAO.getDAO();
		GroupSkillLevelTemplate skillLevelTemplate = skillLevelDao.getSkillLevelTemplate(skillId, skillLevel);
		if (skillLevelTemplate == null) {// 等级不存在
			return GroupCmdHelper.groupSkillFillFailMsg(commonRsp, "技能等级不存在");
		}

		// 准备去扣钱以及升级帮派技能了
		String resultTip = groupBaseDataMgr.updateGroupDataWhenResearchSkill(player, skillLevelTemplate.getResearchNeedSupply(), skillId, skillLevel, skillLevelTemplate.getResearchCondation());
		if (!StringUtils.isEmpty(resultTip)) {
			return GroupCmdHelper.groupSkillFillFailMsg(commonRsp, resultTip);
		}

		// 研发技能记录日志
		GroupLog log = new GroupLog();
		log.setLogType(GroupLogType.GROUP_SKILL_REASERCH_VALUE);
		log.setTime(System.currentTimeMillis());
		log.setSkillName(skillCfg.getSkillName());
		log.setSkillLevel(skillLevel);
		group.getGroupLogMgr().addLog(player, log);

		commonRsp.setIsSuccess(true);
		commonRsp.setTipMsg("研发技能成功");
		return commonRsp.build().toByteString();
	}

	/**
	 * 个人学习帮派技能
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString studyGroupSkillHandler(Player player, GroupSkillCommonReqMsg req) {
		String userId = player.getUserId();

		GroupSkillCommonRspMsg.Builder commonRsp = GroupSkillCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.STUDY_GROUP_SKILL_TYPE);

		// 检查角色有没有帮派技能
		UserGroupAttributeDataMgr userGroupDataMgr = UserGroupAttributeDataMgr.getMgr();
		UserGroupAttributeDataIF baseData = userGroupDataMgr.getUserGroupAttributeData(userId);
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupSkillFillFailMsg(commonRsp, "您当前还没有帮派");
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("学习帮派技能", userId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupSkillFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		GroupBaseDataMgr groupBaseDataMgr = group.getGroupBaseDataMgr();
		GroupBaseDataIF groupData = groupBaseDataMgr.getGroupData();
		if (groupData == null) {
			GameLog.error("学习帮派技能", userId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupSkillFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		GroupMemberMgr groupMemberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF memberData = groupMemberMgr.getMemberData(userId, false);
		if (memberData == null) {
			GameLog.error("学习帮派技能", userId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, userId));
			return GroupCmdHelper.groupSkillFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		// 检查个人的权限
		int post = memberData.getPost();// 职位
		int groupLevel = groupData.getGroupLevel();// 帮派等级

		String tip = GroupFunctionCfgDAO.getDAO().canUseFunction(GroupFunction.STUDY_GROUP_SKILL_VALUE, post, groupLevel);
		if (!StringUtils.isEmpty(tip)) {
			GameLog.error("学习帮派技能", userId, String.format("角色职位[%s],帮派等级[%s],权限不足", post, groupLevel));
			return GroupCmdHelper.groupSkillFillFailMsg(commonRsp, tip);
		}

		// 检查技能的数据
		int skillId = req.getSkillId();// 请求的技能Id
		int skillLevel = req.getSkillLevel();// 请求的技能等级

		// 检查请求学习的技能条件满不满足
		GroupSkillCfgDAO skillCfgDao = GroupSkillCfgDAO.getDAO();
		GroupSkillCfg skillCfg = skillCfgDao.getSkillCfg(skillId);
		if (skillCfg == null) {
			GameLog.error("学习帮派技能", userId, String.format("帮派技能Id[%s]没有发现到GroupSkillCfg配置", skillId));
			return GroupCmdHelper.groupSkillFillFailMsg(commonRsp, "技能不存在");
		}

		// 检查帮派等级
		GroupSkillLevelCfgDAO skillLevelDao = GroupSkillLevelCfgDAO.getDAO();
		GroupSkillLevelTemplate skillLevelTemplate = skillLevelDao.getSkillLevelTemplate(skillId, skillLevel);
		if (skillLevelTemplate == null) {// 等级不存在
			return GroupCmdHelper.groupSkillFillFailMsg(commonRsp, "技能等级不存在");
		}

		// 验证帮派已经研发的等级能不能被学习
		if (!groupBaseDataMgr.checkGroupSkillCanStudy(skillId, skillLevel)) {
			GameLog.error("学习帮派技能", userId, String.format("学习技能[%s],等级[%s],帮派Id[%s]还没研发这个技能", skillId, skillLevel, groupId));
			return GroupCmdHelper.groupSkillFillFailMsg(commonRsp, "无法学习，因帮主/副帮主还没研发该等级帮派技能");
		}

		// 检查个人的帮派贡献
		int contribution = baseData.getContribution();
		int studyNeedContribution = skillLevelTemplate.getStudyNeedContribution();
		if (studyNeedContribution > contribution) {
			return GroupCmdHelper.groupSkillFillFailMsg(commonRsp, "帮派贡献不足");
		}

		// 个人技能学习检测是否条件合适并且更新数据
		if (!userGroupDataMgr.updateUserGroupDataWhenStudySkill(player, skillId, skillLevel, -1, -1)) {
			return GroupCmdHelper.groupSkillFillFailMsg(commonRsp, "学习技能失败");
		}

		// 扣除个人贡献
		groupMemberMgr.updateMemberContribution(userId, -skillLevelTemplate.getStudyNeedContribution(), false);
		UserEventMgr.getInstance().LearnSkillInfactionVitality(player, 1);
		commonRsp.setIsSuccess(true);
		commonRsp.setTipMsg("学习技能成功");
		return commonRsp.build().toByteString();
	}
}