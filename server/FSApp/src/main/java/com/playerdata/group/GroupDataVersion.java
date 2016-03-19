package com.playerdata.group;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupDataVersion {

	private int groupBaseData;

	private int groupMemberData;

	private int applyMemberData;

	private int researchSkill;

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
}