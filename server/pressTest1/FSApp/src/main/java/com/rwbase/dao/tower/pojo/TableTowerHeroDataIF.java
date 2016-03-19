package com.rwbase.dao.tower.pojo;

import java.util.List;

import com.rwbase.common.attrdata.AttrDataIF;
import com.rwbase.dao.skill.pojo.SkillIF;

public interface TableTowerHeroDataIF {
	/**
	 * 英雄模型id
	 * @return
	 */
	public String getModeId();
	
	/**
	 * 技能列表
	 * @return
	 */
	public List<? extends SkillIF> getSkillLIst();
	
	/**
	 * 总属性
	 * @return
	 */
	public AttrDataIF getTableAttrData();//XXX
}
