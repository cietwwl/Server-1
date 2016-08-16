package com.rwbase.dao.fighting.pojo;

import com.rwbase.common.FightingIndexKey;

/**
 * 
 * 神器品质的战斗力配置
 * 
 * @author CHEN.P
 *
 */
public class FixEquipQualityFightingCfg extends FightingCfgBase {

	private int quality;
	private int requiredLv; // 需求等级
	@FightingIndexKey(1)
	private int fightingOfFixExpEquip1; // 0号位置的神器战斗力
	@FightingIndexKey(2)
	private int fightingOfFixExpEquip2; // 1号位置的神器战斗力
	@FightingIndexKey(3)
	private int fightingOfFixExpEquip3; // 2号位置的神器战斗力
	@FightingIndexKey(4)
	private int fightingOfFixExpEquip4; // 3号位置的神器战斗力
	@FightingIndexKey(5)
	private int fightingOfFixExpEquip5; // 4号位置的神器战斗力
	@FightingIndexKey(6)
	private int fightingOfFixExpEquip6; // 5号位置的神器战斗力
	
	public int getQuality() {
		return quality;
	}
	
	public int getRequiredLv() {
		return requiredLv;
	}

}
