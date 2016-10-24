package com.rwbase.dao.fighting.pojo;

import com.rwbase.common.FightingIndexKey;

public class SpriteAttachFightingCfg extends FightingCfgBase{
	
	private int level;
	private int requiredLv;
	@FightingIndexKey(1)
	private int spriteItem1;
	@FightingIndexKey(2)
	private int spriteItem2;
	@FightingIndexKey(3)
	private int spriteItem3;
	@FightingIndexKey(4)
	private int spriteItem4;
	@FightingIndexKey(5)
	private int spriteItem5;
	@FightingIndexKey(6)
	private int spriteItem6;
	
	public int getLevel() {
		return level;
	}

	@Override
	public int getRequiredLv() {
		// TODO Auto-generated method stub
		return requiredLv;
	}
}
