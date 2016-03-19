package com.rwbase.dao.tower.pojo;

import java.util.List;

import com.rwbase.common.attrdata.AttrDataIF;
import com.rwbase.dao.skill.pojo.SkillIF;

public interface TowerEnemyInfoIF {
	/**
	 * 模型id
	 * @return
	 */
	public String getUserId();
	
	/**
	 * 塔层id
	 * @return
	 */
	public int getTowerId();
	
	/**
	 * 主角总属性
	 * @return
	 */
	public AttrDataIF getPlayerAttrData();
	
	/**
	 * 主角技能列表
	 * @return
	 */
	public List<? extends SkillIF> getPlayerSkill();
	
	/**
	 * 佣兵表 数据
	 * @return
	 */
	public List<? extends TableTowerHeroDataIF> getHeros();
}
