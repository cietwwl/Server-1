package com.rwbase.dao.skill.pojo;


public class SkillMappingCfg {

	private String skillId;
	private String roleId;
	private int order;

	
	public SkillMappingCfg() {
	}

	public String getSkillId() {
		return skillId;
	}

	public void setSkillId(String skillId) {
		this.skillId = skillId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
}
