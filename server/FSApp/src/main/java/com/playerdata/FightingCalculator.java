package com.playerdata;

import java.util.List;

import com.common.BeanOperationHelper;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.dao.fighting.FightingWeightCfgDAO;
import com.rwbase.dao.fighting.pojo.FightingWeightCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.skill.SkillCfgDAO;
import com.rwbase.dao.skill.SkillEffectCfgDAO;
import com.rwbase.dao.skill.pojo.Skill;
import com.rwbase.dao.skill.pojo.SkillCfg;
import com.rwbase.dao.skill.pojo.SkillEffectCfg;

public class FightingCalculator {
	public static final String ATTAK = "attack";
	public static final String SKILL_LEVEL = "skillLevel";
	public static final String MAGIC_LEVEL = "magicLevel";
	private static final float COMMON_ATK_RATE = 1.5f;// 普通攻击除的系数

	public static int calFighting(Hero roleP, AttrData totalAttrData) {
		float fighting = 0;
		float attrValue = 0;

		int m_SkillLevel = calTotalSkillLevel(roleP);
		int m_MagicLevel = calTotalMagicLevel(roleP);

		String attackId = RoleCfgDAO.getInstance().getAttackId(roleP.getTemplateId());
		SkillCfg skillCfg = SkillCfgDAO.getInstance().getCfg(attackId);
		String skillEffectId = "";
		if (skillCfg != null) {
			skillEffectId = skillCfg.getSkillEffectId();
		}

		float attackCD = 0;
		SkillEffectCfg skillEffect = SkillEffectCfgDAO.getCfgDAO().getCfgById(skillEffectId);
		if (skillEffect != null) {
			attackCD = skillEffect.getCD();
		}

		List<FightingWeightCfg> listInfo = FightingWeightCfgDAO.getInstance().getAllCfg();

		StringBuilder sb = new StringBuilder();
		sb.append("------------------").append(roleP.getTemplateId()).append("------------------\n");
		for (FightingWeightCfg cfg : listInfo) {
			String attrName = cfg.getAttrName();
			if (attrName.equals(SKILL_LEVEL)) {
				attrValue = m_SkillLevel;
			} else if (attrName.equals(MAGIC_LEVEL)) {
				attrValue = m_MagicLevel;
			} else {
				attrValue = BeanOperationHelper.getValueByName(totalAttrData, attrName);
			}

			if (attrName.equals(ATTAK)) {
				fighting += attrValue / (attackCD / COMMON_ATK_RATE) * cfg.getWeight();
			} else {
				fighting += attrValue * cfg.getWeight();
			}

			sb.append(attrName).append(":").append(attrValue).append("，战力：").append(fighting).append("\n");
		}

		System.err.println(sb.toString());

		return (int) fighting;
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
