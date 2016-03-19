package com.rwbase.common.attrdata;

import javax.persistence.Id;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class RoleAttrData {

	@Id
	private String heroId;

	private AttrData equipTotalData;

	private AttrData inlayTotalData;

	private AttrData roleBaseTotalData;

	private AttrData fashionTotalData;

	private AttrData skillTotalData;

	private AttrData groupSkillTotalData;// 帮派技能属性

	private AttrData totalData;

	private int fighting;

	private String log;

	public RoleAttrData(String heroIdP, AttrData equipTotalDataP, AttrData inlayTotalDataP, AttrData roleBaseTotalDataP, AttrData skillAttrDataP, AttrData fashionAttrDataP, AttrData groupSkillTotalData) {
		this.heroId = heroIdP;
		this.equipTotalData = equipTotalDataP;
		this.inlayTotalData = inlayTotalDataP;
		this.roleBaseTotalData = roleBaseTotalDataP;
		this.skillTotalData = skillAttrDataP;
		this.groupSkillTotalData = groupSkillTotalData;
		this.fashionTotalData = fashionAttrDataP;
		this.totalData = getTotalData();
	}

	public String getHeroId() {
		return heroId;
	}

	public AttrDataIF getEquipTotalData() {
		return equipTotalData;
	}

	public void setEquipTotalData(AttrData equipTotalData) {
		this.equipTotalData = equipTotalData;
	}

	public AttrDataIF getInlayTotalData() {
		return inlayTotalData;
	}

	public void setInlayTotalData(AttrData inlayTotalData) {
		this.inlayTotalData = inlayTotalData;
	}

	public AttrDataIF getRoleBaseTotalData() {
		return roleBaseTotalData;
	}

	public void setRoleBaseTotalData(AttrData roleBaseTotalData) {
		this.roleBaseTotalData = roleBaseTotalData;
	}

	public AttrData getFashionTotalData() {
		return fashionTotalData;
	}

	public void setFashionTotalData(AttrData fashionTotalData) {
		this.fashionTotalData = fashionTotalData;
	}

	public AttrData getSkillTotalData() {
		return skillTotalData;
	}

	/**
	 * 获取帮派技能属性加成
	 * 
	 * @return
	 */
	public AttrData getGroupSkillTotalData() {
		return groupSkillTotalData;
	}

	/**
	 * 设置帮派技能属性
	 * 
	 * @param groupSkillTotalData
	 */
	public void setGroupSkillTotalData(AttrData groupSkillTotalData) {
		this.groupSkillTotalData = groupSkillTotalData;
	}

	public void setSkillTotalData(AttrData skillTotalData) {
		this.skillTotalData = skillTotalData;
	}

	public void setTotalData(AttrData totalData) {
		this.totalData = totalData;
	}

	public int getFighting() {
		return fighting;
	}

	public void setFighting(int fighting) {
		this.fighting = fighting;
	}

	public AttrData getTotalData() {
		AttrData total = new AttrData().plus(roleBaseTotalData).plus(equipTotalData).plus(inlayTotalData).plus(skillTotalData).plus(groupSkillTotalData);
		if (fashionTotalData != null) {
			total.plus(fashionTotalData);
		}
		this.totalData = total;
		return this.totalData;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

}
