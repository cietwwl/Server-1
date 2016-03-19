package com.rw.handler.group.data;

import java.util.List;
import java.util.Map;

import com.rw.dataSyn.SynItem;

/*
 * @author HC
 * @date 2016年3月15日 下午4:27:08
 * @Description 角色的帮派信息
 */
public class UserGroupData implements SynItem {

	private String userId;// 角色Id
	private String groupId;// 角色加入帮派的Id，如果没有帮派的时候是空
	private long quitGroupTime;// 退出帮派的时间，包括被踢出帮派的时间
	private long sendEmailTime;// 发送邮件的时间
	private Map<Integer, GroupSkillItem> studySkill;// 已经学习了的帮派技能列表
	private List<String> applyGroupIdList;// 申请的列表

	@Override
	public String getId() {
		return userId;
	}

	public String getGroupId() {
		return groupId;
	}

	public long getQuitGroupTime() {
		return quitGroupTime;
	}

	public long getSendEmailTime() {
		return sendEmailTime;
	}

	public Map<Integer, GroupSkillItem> getStudySkill() {
		return studySkill;
	}

	public List<String> getApplyGroupIdList() {
		return applyGroupIdList;
	}
}