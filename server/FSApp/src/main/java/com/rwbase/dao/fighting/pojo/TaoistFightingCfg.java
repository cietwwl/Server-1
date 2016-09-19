package com.rwbase.dao.fighting.pojo;

import com.rwbase.common.FightingIndexKey;

public class TaoistFightingCfg extends FightingCfgBase {

	private int level;
	@FightingIndexKey(1)
	private int fightingOfTaoist1;
	@FightingIndexKey(2)
	private int fightingOfTaoist2;
	@FightingIndexKey(3)
	private int fightingOfTaoist3;

	public int getLevel() {
		return level;
	}
	
	public int getRequiredLv() {
		return level;
	}
}
