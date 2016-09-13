package com.rwbase.dao.fighting.pojo;

import com.rwbase.common.FightingIndexKey;

public class MagicFetterFightingCfg extends FightingCfgBase {

	private int level;
	private int requiredLv;
	@FightingIndexKey(1)
	private int fightingOfFetter1;
	@FightingIndexKey(2)
	private int fightingOfFetter2;
	@FightingIndexKey(3)
	private int fightingOfFetter3;
	@FightingIndexKey(4)
	private int fightingOfFetter4;
	@FightingIndexKey(5)
	private int fightingOfFetter5;
	@FightingIndexKey(6)
	private int fightingOfFetter6;
	
	public int getLevel() {
		return level;
	}
	
	@Override
	public int getRequiredLv() {
		return requiredLv;
	}
	
}
