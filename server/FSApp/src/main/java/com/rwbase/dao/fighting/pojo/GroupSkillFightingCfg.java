package com.rwbase.dao.fighting.pojo;

import com.rwbase.common.FightingIndexKey;

public class GroupSkillFightingCfg extends FightingCfgBase {

	private int level;
	private int requiredLv;
	@FightingIndexKey(1)
	private int fightingOfGroupSkill1;
	@FightingIndexKey(1)
	private int fightingOfGroupSkill2;
	@FightingIndexKey(1)
	private int fightingOfGroupSkill3;
	
	public int getLevel() {
		return level;
	}
	
	public int getRequiredLv() {
		return requiredLv;
	}
}
