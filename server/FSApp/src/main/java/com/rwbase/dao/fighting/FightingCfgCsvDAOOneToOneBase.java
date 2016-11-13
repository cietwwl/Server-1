package com.rwbase.dao.fighting;

import com.rwbase.dao.fighting.pojo.OneToOneTypeFightingCfg;

public abstract class FightingCfgCsvDAOOneToOneBase extends FightingByRequiredLvCfgDAOBase<OneToOneTypeFightingCfg> {

	
	@Override
	protected Class<OneToOneTypeFightingCfg> getElementClass() {
		return OneToOneTypeFightingCfg.class;
	}
}
