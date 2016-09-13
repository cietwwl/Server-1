package com.playerdata;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.common.BeanOperationHelper;
import com.rwbase.common.IFunction;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attribute.AttributeType;
import com.rwbase.dao.fighting.FightingWeightCfgDAO;
import com.rwbase.dao.fighting.pojo.FightingWeightCfg;
import com.rwbase.dao.item.MagicCfgDAO;
import com.rwbase.dao.item.pojo.MagicCfg;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.skill.SkillCfgDAO;
import com.rwbase.dao.skill.SkillEffectCfgDAO;
import com.rwbase.dao.skill.pojo.SkillCfg;
import com.rwbase.dao.skill.pojo.SkillEffectCfg;

public class FightingCalculator {
	private static final String PHYSIC_ATTAK = "physiqueAttack";// 物理攻击
	private static final String SPRITE_ATTAK = "spiritAttack";// 法术攻击
	private static final String SKILL_LEVEL = "skillLevel";// 技能等级
	private static final String MAGIC_LEVEL = "magicLevel";// 法宝等级
	private static final float COMMON_ATK_RATE_OLD = 1.5f;// 普通攻击除的系数
	private static final float COMMON_ATK_RATE = 3.0f;// 普通攻击除的系数 修改于 2016-09-06
	
//	private static <T> FightingCalculateComponentType getComponentType(IFunction<T, Integer> func, Class<?> clazz) {
//		EnumSet<FightingCalculateComponentType> es = EnumSet.allOf(FightingCalculateComponentType.class);
//		boolean getHeroFunc = false;
//		if (Hero.class.isAssignableFrom(clazz)) {
//			getHeroFunc = true;
//		}
//		FightingCalculateComponentType type;
//		for(Iterator<FightingCalculateComponentType> itr = es.iterator(); itr.hasNext();) {
//			type = itr.next();
//			if(getHeroFunc) {
//				if(type.getComponentFunc() == func) {
//					return type;
//				}
//			} else {
//				if(type.getPlayerOnlyComponentFunc() == func) {
//					return type;
//				}
//			}
//		}
//		return null;
//	}
	
	private static <T> int calFighting(T target, List<IFunction<T, Integer>> funcList) {
		int fighting = 0;
		IFunction<T, Integer> currentFunc;
		int currentFighting;
		for(int i = 0, size = funcList.size(); i < size; i++) {
			currentFunc = funcList.get(i);
			currentFighting = currentFunc.apply(target);
			fighting += currentFighting;
//			System.out.println(String.format("%s, 战力：%d", getComponentType(currentFunc, target.getClass()).getChineseName(), currentFighting));
		}
		return fighting;
	}

	public static int calFighting(Hero roleP, AttrData totalAttrData) {
//		// 技能的总等级
//		int skillLevel = 0;
//		for (SkillItem skill : roleP.getSkillMgr().getSkillList(roleP.getUUId())) {
//			skillLevel += skill.getLevel();
//		}
//
//		String magicModelId = "";
//		int magicLevel = 0;
//		if (roleP.isMainRole()) {
//			ItemData magic = roleP.getPlayer().getMagic();
//			if (magic != null) {
//				magicLevel = magic.getMagicLevel();
//				magicModelId = String.valueOf(magic.getModelId());
//			}
//		}
//
//		int fighting = calFighting(roleP.getTemplateId(), skillLevel, magicLevel, magicModelId, totalAttrData);
//		int fightingNew = calFightingNew(roleP);
//		System.out.println("hero:[" + roleP.getId() + "," + roleP.getName() + "], 旧战力：" + fighting + ", 新战力：" + fightingNew);
//		return fighting;
		
		// 新的战力计算
//		System.out.println("----------开始计算[" + roleP.getName() + "]的战斗力----------");
		List<IFunction<Hero, Integer>> allComponentsOfHero = FightingCalculateComponentType.getAllHeroComponents();
		int fighting = calFighting(roleP, allComponentsOfHero);
		if (roleP.isMainRole()) {
			// 主角独有的
			Player p = roleP.getPlayer();
			fighting += calFighting(p, FightingCalculateComponentType.getAllPlayerComponents());
		}
//		System.out.println("----------结束计算[" + roleP.getName() + "]的战斗力----------");
		return fighting;
	}

	/**
	 * 计算战斗力
	 * 
	 * @param heroTemplateId
	 *            英雄的模版Id
	 * @param skillLevel
	 * @param magicLevel
	 * @param magicModelId
	 *            法宝的模版Id
	 * @param totalAttrData
	 * @return
	 */
	public static int calFighting(String heroTemplateId, int skillLevel, int magicLevel, String magicModelId, AttrData totalAttrData) {
		float fighting = 0;

		String attackId = RoleCfgDAO.getInstance().getAttackId(heroTemplateId);
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

		String magicLevelQuality = "";
		MagicCfg magicCfg = MagicCfgDAO.getInstance().getCfgById(magicModelId);
		if (magicCfg != null) {
			magicLevelQuality = MAGIC_LEVEL + "_" + magicCfg.getQuality();
		}

		// StringBuilder sb = new StringBuilder();
		// sb.append("------------------").append(heroTemplateId).append("------------------\n");

		for (FightingWeightCfg cfg : listInfo) {
			float attrValue = 0;

			String attrName = cfg.getAttrName();
			if (attrName.equals(SKILL_LEVEL)) {
				attrValue = skillLevel;
			} else if (attrName.equals(magicLevelQuality)) {
				attrValue = magicLevel;
			} else {
				attrValue = BeanOperationHelper.getValueByName(totalAttrData, attrName);
			}

			if (attrName.equals(PHYSIC_ATTAK) || attrName.equals(SPRITE_ATTAK)) {
				float reactionTime = 0;
				RoleCfg roleCfg = RoleCfgDAO.getInstance().getCfgById(heroTemplateId);
				if (roleCfg != null) {
					reactionTime = roleCfg.getReactionTime();
				}
				fighting += attrValue / ((attackCD + reactionTime) / COMMON_ATK_RATE_OLD) * cfg.getWeight();

			} else {
				fighting += attrValue * cfg.getWeight();
			}

			// sb.append(attrName).append(":").append(attrValue).append("，战力：").append(fighting).append("\n");
		}

		// System.err.println(sb.toString());

		return (int) fighting;
	}
	
	/**
	 * 计算战斗力
	 * 
	 * @param heroTemplateId 英雄的模版Id
	 * @param attrData 属性
	 * @return
	 */
	public static int calOnlyAttributeFighting(String heroTemplateId, AttrData attrData) {
		float fighting = 0;

		List<FightingWeightCfg> listInfo = FightingWeightCfgDAO.getInstance().getAllCfg();

		RoleCfg roleCfg = RoleCfgDAO.getInstance().getCfgById(heroTemplateId);
		float totalTimePerNormAtk = roleCfg == null ? 0 : roleCfg.getTotalTimePerNormAtk();
		float tempFighting;
		for (FightingWeightCfg cfg : listInfo) {
			float attrValue = 0;

			String attrName = cfg.getAttrName();
			if (attrName.equals(SKILL_LEVEL)) {
				continue;
			} else if (attrName.contains(MAGIC_LEVEL)) {
				continue;
			} else {
				attrValue = BeanOperationHelper.getValueByName(attrData, attrName);
			}

			if (attrName.equals(PHYSIC_ATTAK) || attrName.equals(SPRITE_ATTAK)) {

				/* 1.攻击战力=物理攻击/(普攻总时长/3)*物理攻击权重+法术攻击/（普攻总时长/3）*法术攻击权重 */
				tempFighting = attrValue / (totalTimePerNormAtk / COMMON_ATK_RATE) * cfg.getWeight();

			} else {
				tempFighting = attrValue * cfg.getWeight();
			}
			if(tempFighting > 0) {
				fighting += tempFighting;
			}
		}

		return Math.round(fighting);
	}
	
	public static int calculateFighting(String heroTemplateId, Map<Integer, Integer> attrMap) {
		int fighting = 0;
		Map<String, Float> fightingWeights = FightingWeightCfgDAO.getInstance().getWeightsOfAttrName();
		RoleCfg roleCfg = RoleCfgDAO.getInstance().getCfgById(heroTemplateId);
		float totalTimePerNormAtk = roleCfg == null ? 0 : roleCfg.getTotalTimePerNormAtk();
		
		for (Iterator<Map.Entry<Integer, Integer>> itr = attrMap.entrySet().iterator(); itr.hasNext();) {
			Map.Entry<Integer, Integer> entry = itr.next();
			AttributeType type = AttributeType.getAttributeType(entry.getKey());
			String attrName = type.attrFieldName;
			Float weight = fightingWeights.get(type.attrFieldName);
			int attrValue = entry.getValue();
			if (weight == null) {
				continue;
			}
			if (attrName.equals(PHYSIC_ATTAK) || attrName.equals(SPRITE_ATTAK)) {
				
				/*1.攻击战力=物理攻击/(普攻总时长/3)*物理攻击权重+法术攻击/（普攻总时长/3）*法术攻击权重*/
				fighting += attrValue / (totalTimePerNormAtk / COMMON_ATK_RATE) * weight;

			} else {
				fighting += attrValue * weight;
			}
		}
		return fighting;
	}
	// private static int calTotalSkillLevel(Hero roleP) {
	// int skillTotalLevel = 0;
	// for (Skill skill : roleP.getSkillMgr().getSkillList()) {
	// skillTotalLevel += skill.getLevel();
	// }
	// return skillTotalLevel;
	// }
	//
	// private static int calTotalMagicLevel(Hero roleP) {
	// int magicTotalLevel = 0;
	// if (roleP.getRoleType() == eRoleType.Player) {
	// ItemData magic = roleP.getPlayer().getMagic();
	// if (magic != null) {
	// magicTotalLevel = magic.getMagicLevel();
	// }
	// }
	// return magicTotalLevel;
	//
	// }
}