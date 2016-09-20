package com.rwbase.dao.fighting.pojo;

import com.rwbase.common.FightingIndexKey;

/**
 * 
 * 英雄羁绊的战斗力配置
 * 
 * @author CHEN.P
 *
 */
public class HeroFetterFightingCfg extends FightingCfgBase {

	private int level;
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
	
	public int getLevel() {
		return level;
	}
	
	@Override
	public int getRequiredLv() {
		return 0;
	}

}
