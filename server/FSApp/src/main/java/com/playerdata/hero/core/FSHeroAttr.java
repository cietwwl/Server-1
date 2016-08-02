package com.playerdata.hero.core;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attrdata.RoleAttrData;

@SynClass
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown=true)
public class FSHeroAttr implements  RoleAttrData {

	@Id
	@JsonIgnore
	private String heroId;
	@JsonIgnore
	private AttrData roleBaseTotalData; // 基础属性
	@JsonProperty("1")
	private AttrData totalData; // 总属性
	@JsonProperty("2")
	private int fighting; // 战斗力
	
	public FSHeroAttr() {}
	
	public void setHeroId(String pHeroId) {
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
	
	@Override
	public String getHeroId() {
		return heroId;
	}
	
	@Override
	public AttrData getRoleBaseTotalData() {
		return roleBaseTotalData;
	}
	
	@Override
	public AttrData getTotalData() {
		return totalData;
	}
	
	@Override
	public int getFighting() {
		return fighting;
	}
}
