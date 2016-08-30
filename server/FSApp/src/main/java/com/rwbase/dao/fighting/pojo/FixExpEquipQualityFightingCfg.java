package com.rwbase.dao.fighting.pojo;

import com.rwbase.common.FightingIndexKey;

public class FixExpEquipQualityFightingCfg extends FightingCfgBase {

	private int quality;
	private int requiredLv;
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
	
	public int getQuality() {
		return quality;
	}
	
	public int getRequiredLv() {
		return requiredLv;
	}

}
