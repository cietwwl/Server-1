package com.rwbase.common.attrdata;

import javax.persistence.Id;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class RoleAttrData {

	@Id
	private final String heroId;
	private final AttrData roleBaseTotalData;
	private final AttrData totalData;
	private final int fighting;

	// private AttrData equipTotalData;
	//
	// private AttrData inlayTotalData;
	//
	// private AttrData fashionTotalData;
	//
	// private AttrData skillTotalData;
	//
	// private AttrData groupSkillTotalData;// 帮派技能属性
	//
	// private AttrData heroFettersTotalData;// 羁绊属性

	private String log;

	public RoleAttrData(String heroId, AttrData roleBaseTotalData, AttrData totalData, int fighting, String log) {
		this.heroId = heroId;
		this.roleBaseTotalData = roleBaseTotalData;
		this.totalData = totalData;
		this.fighting = fighting;
		this.log = log;
	}

	// public RoleAttrData(String heroIdP, AttrData equipTotalDataP, AttrData inlayTotalDataP, AttrData roleBaseTotalDataP, AttrData skillAttrDataP,
	// AttrData fashionAttrDataP,
	// AttrData groupSkillTotalData, AttrData heroFettersTotalData) {
	// this.heroId = heroIdP;
	// this.equipTotalData = equipTotalDataP;
	// this.inlayTotalData = inlayTotalDataP;
	// this.roleBaseTotalData = roleBaseTotalDataP;
	// this.skillTotalData = skillAttrDataP;
	// this.groupSkillTotalData = groupSkillTotalData;
	// this.fashionTotalData = fashionAttrDataP;
	// this.heroFettersTotalData = heroFettersTotalData;
	// this.totalData = getTotalData();
	// }

	public String getHeroId() {
		return heroId;
	}

	// public AttrDataIF getEquipTotalData() {
	// return equipTotalData;
	// }
	//
	// public void setEquipTotalData(AttrData equipTotalData) {
	// this.equipTotalData = equipTotalData;
	// }
	//
	// public AttrDataIF getInlayTotalData() {
	// return inlayTotalData;
	// }
	//
	// public void setInlayTotalData(AttrData inlayTotalData) {
	// this.inlayTotalData = inlayTotalData;
	// }

	public AttrDataIF getRoleBaseTotalData() {
		return roleBaseTotalData;
	}

	// public void setRoleBaseTotalData(AttrData roleBaseTotalData) {
	// this.roleBaseTotalData = roleBaseTotalData;
	// }
	//
	// public AttrData getFashionTotalData() {
	// return fashionTotalData;
	// }
	//
	// public void setFashionTotalData(AttrData fashionTotalData) {
	// this.fashionTotalData = fashionTotalData;
	// }
	//
	// public AttrData getSkillTotalData() {
	// return skillTotalData;
	// }
	//
	// /**
	// * 获取帮派技能属性加成
	// *
	// * @return
	// */
	// public AttrData getGroupSkillTotalData() {
	// return groupSkillTotalData;
	// }
	//
	// /**
	// * 设置帮派技能属性
	// *
	// * @param groupSkillTotalData
	// */
	// public void setGroupSkillTotalData(AttrData groupSkillTotalData) {
	// this.groupSkillTotalData = groupSkillTotalData;
	// }
	//
	// public void setSkillTotalData(AttrData skillTotalData) {
	// this.skillTotalData = skillTotalData;
	// }
	//
	// public void setTotalData(AttrData totalData) {
	// this.totalData = totalData;
	// }

	public int getFighting() {
		return fighting;
	}

	// public void setFighting(int fighting) {
	// this.fighting = fighting;
	// }
	//
	// public AttrData getHeroFettersTotalData() {
	// return heroFettersTotalData;
	// }
	//
	// public void setHeroFettersTotalData(AttrData heroFettersTotalData) {
	// this.heroFettersTotalData = heroFettersTotalData;
	// }

	public AttrData getTotalData() {
		// AttrData total = new
		// AttrData().plus(roleBaseTotalData).plus(equipTotalData).plus(inlayTotalData).plus(skillTotalData).plus(groupSkillTotalData);
		// if (fashionTotalData != null) {
		// total.plus(fashionTotalData);
		// }
		//
		// if (heroFettersTotalData != null) {
		// total.plus(heroFettersTotalData);
		// }
		//
		// this.totalData = total;
		return totalData;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public static class Builder {
		private String heroId;
		private AttrData roleBaseTotalData;
		private AttrData totalData;
		private int fighting;
		private String log;

		public void setHeroId(String heroId) {
			this.heroId = heroId;
		}

		public void setRoleBaseTotalData(AttrData roleBaseTotalData) {
			this.roleBaseTotalData = roleBaseTotalData;
		}

		public void setTotalData(AttrData totalData) {
			this.totalData = totalData;
		}

		public void setFighting(int fighting) {
			this.fighting = fighting;
		}

		public void setLog(String log) {
			this.log = log;
		}

		public RoleAttrData build() {
			return new RoleAttrData(heroId, roleBaseTotalData, totalData, fighting, log);
		}
	}
}