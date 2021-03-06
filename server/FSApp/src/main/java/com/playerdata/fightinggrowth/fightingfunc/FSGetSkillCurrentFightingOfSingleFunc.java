package com.playerdata.fightinggrowth.fightingfunc;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Hero;
import com.playerdata.fightinggrowth.calc.FightingCalcComponentType;
import com.playerdata.team.SkillInfo;
import com.rwbase.common.IFunction;
import com.rwbase.common.attribute.param.SkillParam.SkillBuilder;
import com.rwbase.dao.skill.pojo.SkillItem;

public class FSGetSkillCurrentFightingOfSingleFunc implements IFunction<Hero, Integer> {

	private static FSGetSkillCurrentFightingOfSingleFunc _instance = new FSGetSkillCurrentFightingOfSingleFunc();

	// private SkillFightingCfgDAO skillFightingCfgDAO;

	protected FSGetSkillCurrentFightingOfSingleFunc() {
		// skillFightingCfgDAO = SkillFightingCfgDAO.getInstance();
	}

	public static FSGetSkillCurrentFightingOfSingleFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Hero hero) {
		List<SkillItem> skillList = hero.getSkillMgr().getSkillList(hero.getId());
		// int fighting = 0;

		int size = skillList.size();

		List<SkillInfo> skillInfoList = new ArrayList<SkillInfo>(size);
		for (int i = 0; i < size; i++) {
			SkillItem skillItem = skillList.get(i);

			SkillInfo skillInfo = new SkillInfo();
			skillInfo.setSkillId(skillItem.getSkillId());
			skillInfo.setSkillLevel(skillItem.getLevel());

			skillInfoList.add(skillInfo);
		}

		SkillBuilder sb = new SkillBuilder();
		sb.setHeroTemplateId(hero.getTemplateId());
		sb.setSkillList(skillInfoList);
		return FightingCalcComponentType.SKILL.calc.calc(sb.build());
	}
}
