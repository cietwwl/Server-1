package com.rw.handler.group.data;

/*
 * @author HC
 * @date 2016年3月15日 下午6:15:56
 * @Description 
 */
public class GroupDataVersion {
	private int groupBaseData;// 帮派基础数据版本号
	private int groupMemberData;// 帮派成员信息版本号
	private int applyMemberData;// 帮派申请成员信息版本号
	private int researchSkill;// 研发帮派技能信息版本号

	public int getGroupBaseData() {
		return groupBaseData;
	}

	public int getGroupMemberData() {
		return groupMemberData;
	}

	public int getResearchSkill() {
		return researchSkill;
	}

	public int getApplyMemberData() {
		return applyMemberData;
	}

	public void setGroupBaseData(int groupBaseData) {
		this.groupBaseData = groupBaseData;
	}

	public void setGroupMemberData(int groupMemberData) {
		this.groupMemberData = groupMemberData;
	}

	public void setApplyMemberData(int applyMemberData) {
		this.applyMemberData = applyMemberData;
	}

	public void setResearchSkill(int researchSkill) {
		this.researchSkill = researchSkill;
	}
}