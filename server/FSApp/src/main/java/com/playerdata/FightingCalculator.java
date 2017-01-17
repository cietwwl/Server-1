package com.playerdata;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.common.BeanOperationHelper;
import com.playerdata.hero.core.FSHeroMgr;
import com.rwbase.common.IFunction;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attribute.AttributeType;
import com.rwbase.dao.fighting.FightingWeightCfgDAO;
import com.rwbase.dao.fighting.pojo.FightingWeightCfg;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;

public class FightingCalculator {
	private static final String PHYSIC_ATTAK = "physiqueAttack";// 物理攻击
	private static final String SPRITE_ATTAK = "spiritAttack";// 法术攻击
	private static final String SKILL_LEVEL = "skillLevel";// 技能等级
	private static final String MAGIC_LEVEL = "magicLevel";// 法宝等级
	// private static final float COMMON_ATK_RATE_OLD = 1.5f;// 普通攻击除的系数
	private static final float COMMON_ATK_RATE = 3.0f;// 普通攻击除的系数 修改于 2016-09-06
	
//	private static <T> FightingCalculateComponentType getComponentType(IFunction<T, Integer> func) {
//		java.util.EnumSet<FightingCalculateComponentType> es = java.util.EnumSet.allOf(FightingCalculateComponentType.class);
//		for(FightingCalculateComponentType type : es) {
//			if(type.getComponentFunc() == func || type.getPlayerOnlyComponentFunc() == func) {
//				return type;
//			}
//		}
//		return null;
//	}
//
//	private static <T> int calFighting(T target, List<IFunction<T, Integer>> funcList, StringBuilder strBld) {
//		int fighting = 0;
//		IFunction<T, Integer> currentFunc;
//		int currentFighting;
//		for (int i = 0, size = funcList.size(); i < size; i++) {
//			currentFunc = funcList.get(i);
//			currentFighting = currentFunc.apply(target);
//			fighting += currentFighting;
//			FightingCalculateComponentType type = getComponentType(currentFunc);
//			strBld.append("{").append(type.getChineseName()).append("=").append(currentFighting).append("} ");
//		}
//		return fighting;
//	}
//
//	public static int calFighting(Hero roleP, AttrData totalAttrData) {
//		// 新的战力计算
//		List<IFunction<Hero, Integer>> allComponentsOfHero = FightingCalculateComponentType.getAllHeroComponents();
//		StringBuilder strBld = new StringBuilder("========== 开始计算[").append(roleP.getName()).append("]的战斗力 ==========\n");
//		int fighting = calFighting(roleP, allComponentsOfHero, strBld);
//		if (roleP.isMainRole()) {
//			// 主角独有的
//			Player p = FSHeroMgr.getInstance().getOwnerOfHero(roleP);
//			fighting += calFighting(p, FightingCalculateComponentType.getAllPlayerComponents(), strBld);
//		}
//		strBld.append("\n========== 结束计算[").append(roleP.getName()).append("]的战斗力 ==========\n");
//		System.err.println(strBld.toString());
//		return fighting;
//	}
	
	private static <T> int calFighting(T target, List<IFunction<T, Integer>> funcList) {
		int fighting = 0;
		IFunction<T, Integer> currentFunc;
		int currentFighting;
		for (int i = 0, size = funcList.size(); i < size; i++) {
			currentFunc = funcList.get(i);
			currentFighting = currentFunc.apply(target);
			fighting += currentFighting;
		}
		return fighting;
	}

	public static int calFighting(Hero roleP, AttrData totalAttrData) {
		// 新的战力计算
		List<IFunction<Hero, Integer>> allComponentsOfHero = FightingCalculateComponentType.getAllHeroComponents();
		int fighting = calFighting(roleP, allComponentsOfHero);
		if (roleP.isMainRole()) {
			// 主角独有的
			Player p = FSHeroMgr.getInstance().getOwnerOfHero(roleP);
			fighting += calFighting(p, FightingCalculateComponentType.getAllPlayerComponents());
		}
		return fighting;
	}

	// /**
	// * 计算战斗力
	// *
	// * @param heroTemplateId 英雄的模版Id
	// * @param skillLevel
	// * @param magicLevel
	// * @param magicModelId 法宝的模版Id
	// * @param totalAttrData
	// * @return
	// */
	// public static int calFighting(String heroTemplateId, int skillLevel, int magicLevel, String magicModelId, AttrData totalAttrData) {
	// float fighting = 0;
	//
	// String attackId = RoleCfgDAO.getInstance().getAttackId(heroTemplateId);
	// SkillCfg skillCfg = SkillCfgDAO.getInstance().getCfg(attackId);
	// String skillEffectId = "";
	// if (skillCfg != null) {
	// skillEffectId = skillCfg.getSkillEffectId();
	// }
	//
	// float attackCD = 0;
	// SkillEffectCfg skillEffect = SkillEffectCfgDAO.getCfgDAO().getCfgById(skillEffectId);
	// if (skillEffect != null) {
	// attackCD = skillEffect.getCD();
	// }
	//
	// List<FightingWeightCfg> listInfo = FightingWeightCfgDAO.getInstance().getAllCfg();
	//
	// String magicLevelQuality = "";
	// MagicCfg magicCfg = MagicCfgDAO.getInstance().getCfgById(magicModelId);
	// if (magicCfg != null) {
	// magicLevelQuality = MAGIC_LEVEL + "_" + magicCfg.getQuality();
	// }
	//
	// // StringBuilder sb = new StringBuilder();
	// // sb.append("------------------").append(heroTemplateId).append("------------------\n");
	//
	// for (FightingWeightCfg cfg : listInfo) {
	// float attrValue = 0;
	//
	// String attrName = cfg.getAttrName();
	// if (attrName.equals(SKILL_LEVEL)) {
	// attrValue = skillLevel;
	// } else if (attrName.equals(magicLevelQuality)) {
	// attrValue = magicLevel;
	// } else {
	// attrValue = BeanOperationHelper.getValueByName(totalAttrData, attrName);
	// }
	//
	// if (attrName.equals(PHYSIC_ATTAK) || attrName.equals(SPRITE_ATTAK)) {
	// float reactionTime = 0;
	// RoleCfg roleCfg = RoleCfgDAO.getInstance().getCfgById(heroTemplateId);
	// if (roleCfg != null) {
	// reactionTime = roleCfg.getReactionTime();
	// }
	// fighting += attrValue / ((attackCD + reactionTime) / COMMON_ATK_RATE_OLD) * cfg.getWeight();
	//
	// } else {
	// fighting += attrValue * cfg.getWeight();
	// }
	// }
	//
	// return (int) fighting;
	// }

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
			if (tempFighting > 0) {
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

				/* 1.攻击战力=物理攻击/(普攻总时长/3)*物理攻击权重+法术攻击/（普攻总时长/3）*法术攻击权重 */
				fighting += attrValue / (totalTimePerNormAtk / COMMON_ATK_RATE) * weight;

			} else {
				fighting += attrValue * weight;
			}
		}
		return fighting;
	}
}