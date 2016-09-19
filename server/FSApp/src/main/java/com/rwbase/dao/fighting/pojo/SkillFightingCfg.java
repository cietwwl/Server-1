package com.rwbase.dao.fighting.pojo;

import com.rwbase.common.FightingIndexKey;

public class SkillFightingCfg extends FightingCfgBase {

	private int level;
	@FightingIndexKey(1)
	private int fightingOfSkill1;
	@FightingIndexKey(2)
	private int fightingOfSkill2;
	@FightingIndexKey(3)
	private int fightingOfSkill3;
	@FightingIndexKey(4)
	private int fightingOfSkill4;
	
	public int getLevel() {
		return level;
	}
	
	public int getRequiredLv() {
		return level;
	}
}
