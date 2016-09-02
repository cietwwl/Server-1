package com.rwbase.dao.fighting.pojo;

import com.rwbase.common.FightingIndexKey;

/**
 * 
 * 神器等级的战斗力配置
 * 
 * @author CHEN.P
 *
 */
public class FixEquipLevelFightingCfg extends FightingCfgBase {

	private int level;
	@FightingIndexKey(1)
	private int fightingOfFixExpEquip1; // 0号位置的神器战力
	@FightingIndexKey(2)
	private int fightingOfFixExpEquip2; // 1号位置的神器战力
	@FightingIndexKey(3)
	private int fightingOfFixExpEquip3; // 2号位置的神器战力
	@FightingIndexKey(4)
	private int fightingOfFixExpEquip4; // 3号位置的神器战力
	@FightingIndexKey(5)
	private int fightingOfFixExpEquip5; // 4号位置的神器战力
	@FightingIndexKey(6)
	private int fightingOfFixExpEquip6; // 5号位置的神器战力
	
	public int getLevel() {
		return level;
	}

	public int getRequiredLv() {
		return level;
	}
}
