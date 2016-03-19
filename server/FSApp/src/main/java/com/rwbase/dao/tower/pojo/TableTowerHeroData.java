package com.rwbase.dao.tower.pojo;

import java.util.ArrayList;
import java.util.List;

import com.rwbase.common.attrdata.AttrData;
import com.rwbase.dao.skill.pojo.Skill;

public class TableTowerHeroData implements TableTowerHeroDataIF {
	private String modeId;//英雄Id
	
	private List<Skill> skillLIst=new ArrayList<Skill>();
	private AttrData tableAttrData=new AttrData();//总属性 汇总
	public List<Skill> getSkillLIst() {
		return skillLIst;
	}
	public void setSkillLIst(List<Skill> skillLIst) {
		this.skillLIst = skillLIst;
	}
	public String getModeId() {
		return modeId;
	}
	public void setModeId(String modeId) {
		this.modeId = modeId;
	}
	public AttrData getTableAttrData() {
		return tableAttrData;
	}
	public void setTableAttrData(AttrData tableAttrData) {
		this.tableAttrData = tableAttrData;
	}
}
