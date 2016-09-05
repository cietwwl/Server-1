package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.rw.service.skill.SkillConstant;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fighting.SkillFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.SkillFightingCfg;
import com.rwbase.dao.skill.pojo.SkillItem;

public class FSGetSkillCurrentFightingOfSingleFunc implements IFunction<Hero, Integer> {
	
	private SkillFightingCfgDAO skillFightingCfgDAO;
	
	public FSGetSkillCurrentFightingOfSingleFunc() {
		skillFightingCfgDAO = SkillFightingCfgDAO.getInstance();
	}

	@Override
	public Integer apply(Hero hero) {
		List<SkillItem> skillList = hero.getSkillMgr().getSkillList(hero.getId());
		SkillItem skillItem;
		SkillFightingCfg skillFightingCfg;
		int fighting = 0;
		for (int i = 0; i < skillList.size(); i++) {
			skillItem = skillList.get(i);
			if (skillItem.getOrder() == SkillConstant.NORMAL_SKILL_ORDER) {
				continue;
			}
			skillFightingCfg = skillFightingCfgDAO.getByLevel(skillItem.getLevel());
			fighting += skillFightingCfg.getFightingOfIndex(skillItem.getOrder() + 1);
		}
		return fighting;
	}

}
