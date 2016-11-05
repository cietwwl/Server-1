package com.rwbase.dao.fighting;

import com.rwbase.dao.fighting.pojo.MagicSkillFightingCfg;

public abstract class AbsMagicSkillFightingCfgDAO extends FightingByRequiredLvCfgDAOBase<MagicSkillFightingCfg> {
	
	@Override
	protected Class<MagicSkillFightingCfg> getElementClass() {
		return MagicSkillFightingCfg.class;
	}
}
