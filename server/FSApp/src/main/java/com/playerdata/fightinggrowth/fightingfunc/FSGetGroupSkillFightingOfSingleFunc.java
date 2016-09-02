package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fighting.GroupSkillFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.OneToOneTypeFightingCfg;
import com.rwbase.dao.group.pojo.db.GroupSkillItem;

public class FSGetGroupSkillFightingOfSingleFunc implements IFunction<Hero, Integer> {

	@Override
	public Integer apply(Hero hero) {
		int fighting = 0;
		List<GroupSkillItem> groupSkillItemList = hero.getPlayer().getUserGroupAttributeDataMgr().getUserGroupAttributeData().getSkillItemList();
		if (groupSkillItemList.size() > 0) {
			GroupSkillItem groupSkillItem;
			OneToOneTypeFightingCfg fightingCfg;
			for (int i = 0; i < groupSkillItemList.size(); i++) {
				groupSkillItem = groupSkillItemList.get(i);
				if (groupSkillItem.getLevel() > 0) {
					fightingCfg = GroupSkillFightingCfgDAO.getInstance().getCfgById(groupSkillItem.getId());
					fighting += fightingCfg.getFighting() * groupSkillItem.getLevel();
				}
			}
		}
		return fighting;
	}

}
