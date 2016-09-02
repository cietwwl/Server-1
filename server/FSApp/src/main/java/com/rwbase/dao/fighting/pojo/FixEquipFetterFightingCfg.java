package com.rwbase.dao.fighting.pojo;

import com.rwbase.common.FightingIndexKey;

public class FixEquipFetterFightingCfg extends FightingCfgBase {
	
	private int level;
	private int requiredLv;
	@FightingIndexKey(1)
	private int fightingOfFetter1;
	
	public int getLevel() {
		return level;
	}

	@Override
	public int getRequiredLv() {
		return requiredLv;
	}

	
}
