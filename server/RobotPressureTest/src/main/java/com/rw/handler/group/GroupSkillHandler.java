package com.rw.handler.group;

import java.util.Map;
import java.util.Random;

import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.group.data.GroupSkillItem;
import com.rw.handler.group.data.UserGroupData;
import com.rw.handler.group.msg.GroupSkillMsgReceiver;
import com.rwproto.GroupCommonProto.RequestType;
import com.rwproto.GroupSkillServiceProto.GroupSkillCommonReqMsg;
import com.rwproto.MsgDef.Command;

/*
 * @author HC
 * @date 2016年3月19日 下午11:33:26
 * @Description 
 */
public class GroupSkillHandler {
	private static final Random r = new Random();
	private static GroupSkillHandler handler = new GroupSkillHandler();

	public static GroupSkillHandler getHandler() {
		return handler;
	}

	private static final Command command = Command.MSG_GROUP_SKILL;
	private static final String functionName = "帮派技能请求";

	private GroupSkillHandler() {
	}

	/**
	 * 研发帮派技能
	 * 
	 * @param client
	 * @return
	 */
	public boolean researchSkill(Client client) {
		GroupSkillItem randomSkill = client.getResearchSkillDataHolder().getRandomSkill(r);
		if (randomSkill == null) {
			RobotLog.info("研发技能出现问题，客户端没有发现技能缓存");
			return false;
		}

		GroupSkillCommonReqMsg.Builder commonReq = GroupSkillCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.RESEARCH_GROUP_SKILL_TYPE);
		commonReq.setSkillId(Integer.parseInt(randomSkill.getId()));
		String groupVersion = client.getGroupVersion();
		if (groupVersion != null && !groupVersion.isEmpty()) {
			commonReq.setVersion(groupVersion);
		}

		int hasLevel = randomSkill.getLevel();
		int researchLevel = hasLevel;
		if (r.nextBoolean()) {
			researchLevel--;
		} else {
			researchLevel++;
		}
		commonReq.setSkillLevel(researchLevel);

		return client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), getMsgReciver("研发技能"));
	}

	/**
	 * 学习帮派技能
	 * 
	 * @param client
	 * @return
	 */
	public boolean studySkill(Client client) {
		GroupSkillItem randomSkill = client.getResearchSkillDataHolder().getRandomSkill(r);
		if (randomSkill == null) {
			RobotLog.info("学习技能出现问题，客户端没有发现技能缓存");
			return false;
		}

		UserGroupData userGroupData = client.getUserGroupDataHolder().getUserGroupData();
		if (userGroupData == null) {
			RobotLog.info("学习技能出现问题，客户端没有发现个人帮派数据");
			return false;
		}

		GroupSkillCommonReqMsg.Builder commonReq = GroupSkillCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.STUDY_GROUP_SKILL_TYPE);
		int skillId = Integer.parseInt(randomSkill.getId());
		commonReq.setSkillId(skillId);
		String groupVersion = client.getGroupVersion();
		if (groupVersion != null && !groupVersion.isEmpty()) {
			commonReq.setVersion(groupVersion);
		}

		int hasLevel = randomSkill.getLevel();
		int studyLevel = 0;

		Map<Integer, GroupSkillItem> studySkill = userGroupData.getStudySkill();
		if (studySkill == null || studySkill.isEmpty()) {
			studyLevel++;
		} else {
			int result = r.nextInt(3);
			if (result == 0) {// 出现大于研发技能
				studyLevel = ++hasLevel;
			} else if (result == 1) {// 正常学习等级
				studyLevel++;
			} else {// 学习当前等级
				studyLevel = r.nextBoolean() ? studyLevel - 1 : studyLevel;
			}
		}

		commonReq.setSkillLevel(studyLevel);

		return client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), getMsgReciver("学习技能"));
	}

	/**
	 * 获取MsgReciver
	 *
	 * @param protoType
	 * @return
	 */
	private MsgReciver getMsgReciver(String protoType) {
		return new GroupSkillMsgReceiver(command, functionName, protoType);
	}
}