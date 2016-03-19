package com.playerdata;

import java.util.List;

import com.common.BeanOperationHelper;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.dao.fighting.FightingWeightCfgDAO;
import com.rwbase.dao.fighting.pojo.FightingWeightCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.skill.pojo.Skill;

public class FightingCalculator {
	public static final String SKILL_LEVEL = "skillLevel";
	public static final String MAGIC_LEVEL = "magicLevel";

	public static int calFighting(Hero roleP, AttrData totalAttrData) {
		int fighting = 0;
		float attrValue = 0;

		int m_SkillLevel = calTotalSkillLevel(roleP);
		int m_MagicLevel = calTotalMagicLevel(roleP);
		List<FightingWeightCfg> listInfo = FightingWeightCfgDAO.getInstance().getAllCfg();
		
		for (FightingWeightCfg cfg : listInfo) {
			if(cfg.getAttrName().equals(SKILL_LEVEL)){
				attrValue = m_SkillLevel;
			}else if(cfg.getAttrName().equals(MAGIC_LEVEL)){
				attrValue = m_MagicLevel;
			}else{
				attrValue = BeanOperationHelper.getValueByName(totalAttrData, cfg.getAttrName());
			}
			fighting += (int) (attrValue * cfg.getWeight());
		}

		return fighting;
	}

	private static int calTotalSkillLevel(Hero roleP) {
		int skillTotalLevel = 0;
		for (Skill skill : roleP.getSkillMgr().getSkillList()) {
			skillTotalLevel += skill.getLevel();
		}
		return skillTotalLevel;
	}

	private static int calTotalMagicLevel(Hero roleP) {
		int magicTotalLevel = 0;
		if (roleP.getRoleType() == eRoleType.Player) {
			ItemData magic = roleP.getPlayer().getMagic();
			if (magic != null) {
				magicTotalLevel = magic.getMagicLevel();
			}
		}
		return magicTotalLevel;

	}
}
