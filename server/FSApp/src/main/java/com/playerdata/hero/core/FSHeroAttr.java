package com.playerdata.hero.core;

import javax.persistence.Id;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.common.attrdata.AttrData;

@SynClass
public class FSHeroAttr {

	@Id
	private final String heroId;
	private AttrData roleBaseTotalData; // 基础属性
	private AttrData totalData; // 总属性
	private int fighting; // 战斗力
	
	public FSHeroAttr(String pHeroId) {
		this.heroId = pHeroId;
	}
	
	public void updateTotalData(AttrData pTotal) {
		this.totalData = pTotal;
	}
	
	public void updateRoleBaseTotalData(AttrData pRoleBaseTotalData) {
		this.roleBaseTotalData = pRoleBaseTotalData;
	}
	
	public void updateFighting(int pFighting) {
		this.fighting = pFighting;
	}
	
	public AttrData getRoleBaseTotalData() {
		return roleBaseTotalData;
	}
	
	public AttrData getTotalData() {
		return totalData;
	}
	
	public int getFighting() {
		return fighting;
	}
}
