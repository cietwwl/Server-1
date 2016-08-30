package com.rwbase.dao.fighting.pojo;

import com.rwbase.common.FightingIndexKey;

public class FixExpEquipLevelFightingCfg extends FightingCfgBase {

	private int level;
	@FightingIndexKey(1)
	private int fightingOfFixExpEquip1;
	@FightingIndexKey(2)
	private int fightingOfFixExpEquip2;
	@FightingIndexKey(3)
	private int fightingOfFixExpEquip3;
	@FightingIndexKey(4)
	private int fightingOfFixExpEquip4;
	@FightingIndexKey(5)
	private int fightingOfFixExpEquip5;
	@FightingIndexKey(6)
	private int fightingOfFixExpEquip6;
	
	public int getLevel() {
		return level;
	}

	public int getRequiredLv() {
		return level;
	}
}
