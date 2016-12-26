package com.playerdata.fightinggrowth.fightingfunc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.Hero;
import com.playerdata.fightinggrowth.calc.FightingCalcComponentType;
import com.playerdata.hero.core.FSHeroMgr;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.common.IFunction;
import com.rwbase.common.attribute.param.GroupSkillParam.GroupSkillBuilder;
import com.rwbase.dao.group.pojo.db.GroupSkillItem;

public class FSGetGroupSkillFightingOfSingleFunc implements IFunction<Hero, Integer> {

	private static FSGetGroupSkillFightingOfSingleFunc _instance = new FSGetGroupSkillFightingOfSingleFunc();

	// private GroupSkillFightingCfgDAO groupSkillFightingCfgDAO;

	protected FSGetGroupSkillFightingOfSingleFunc() {
		// groupSkillFightingCfgDAO = GroupSkillFightingCfgDAO.getInstance();
	}

	public static FSGetGroupSkillFightingOfSingleFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Hero hero) {
		if (!GroupHelper.hasGroup(hero.getOwnerUserId())) {
			return 0;
		}

		List<GroupSkillItem> groupSkillItemList = FSHeroMgr.getInstance().getOwnerOfHero(hero).getUserGroupAttributeDataMgr().getUserGroupAttributeData().getSkillItemList();
		if (groupSkillItemList.isEmpty()) {
			return 0;
		}

		int size = groupSkillItemList.size();
		Map<Integer, Integer> groupSkillMap = new HashMap<Integer, Integer>(size);

		for (int i = 0; i < size; i++) {
			GroupSkillItem groupSkillItem = groupSkillItemList.get(i);
			groupSkillMap.put(Integer.valueOf(groupSkillItem.getId()), groupSkillItem.getLevel());
		}

		GroupSkillBuilder gsb = new GroupSkillBuilder();
		gsb.setHeroId(hero.getTemplateId());
		gsb.setGroupSkillMap(groupSkillMap);
		return FightingCalcComponentType.GROUP_SKILL.calc.calc(gsb.build());
	}

}
