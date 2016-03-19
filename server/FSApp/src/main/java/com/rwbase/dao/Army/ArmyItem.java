package com.rwbase.dao.Army;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.army.CurAttrData;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArmyItem {

	private String uesrId;
	
	private List<String> heroIdList;
	
	private List<CurAttrData> curAttrDataList;

	public String getUesrId() {
		return uesrId;
	}

	public void setUesrId(String uesrId) {
		this.uesrId = uesrId;
	}

	public List<String> getHeroIdList() {
		return heroIdList;
	}

	public void setHeroIdList(List<String> heroIdList) {
		this.heroIdList = heroIdList;
	}

	public List<CurAttrData> getCurAttrDataList() {
		return curAttrDataList;
	}

	public void setCurAttrDataList(List<CurAttrData> curAttrDataList) {
		this.curAttrDataList = curAttrDataList;
	}
	
	
	
}
