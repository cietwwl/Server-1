//package com.rwbase.common.attrdata;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import com.rwbase.common.attribute.AttributeType;
//
//public class AttrDataHelper {
//
//	public static final int DIVISION = 10000;// 百分比的属性
//
//	/**
//	 * 获取属性的值对应的Map，用来存储到数据库中优化
//	 * 
//	 * @param attrData
//	 * @return
//	 */
//	public static Map<Integer, Float> parseAttrData2Map(AttrData attrData) {
//		Map<Integer, Float> map = new HashMap<Integer, Float>();
//
//		if (attrData != null) {
//			checkValueCanPutIntoMap(map, AttributeType.LIFE, attrData.getLife());// 最大生命值
//			checkValueCanPutIntoMap(map, AttributeType.ENERGY, attrData.getEnergy()); // 能量值
//			checkValueCanPutIntoMap(map, AttributeType.SPIRIT_ATTACK, attrData.getAttack());// 攻击
//			checkValueCanPutIntoMap(map, AttributeType.PHYSIQUE_DEF, attrData.getPhysiqueDef()); // 体魄防御
//			checkValueCanPutIntoMap(map, AttributeType.SPIRIT_DEF, attrData.getSpiritDef());// 精神防御
//			checkValueCanPutIntoMap(map, AttributeType.ATTACK_VAMPIRE, attrData.getAttackVampire());// 攻击吸血
//			checkValueCanPutIntoMap(map, AttributeType.CRITICAL, attrData.getCritical());// 暴击率
//			checkValueCanPutIntoMap(map, AttributeType.CRITICAL_HURT, attrData.getCriticalHurt());// 暴击伤害提升
//			checkValueCanPutIntoMap(map, AttributeType.TOUGHNESS, attrData.getToughness());// 韧性
//			checkValueCanPutIntoMap(map, AttributeType.LIFE_RECEIVE, attrData.getLifeReceive()); // 生命回复
//			checkValueCanPutIntoMap(map, AttributeType.ENERGY_RECEIVE, attrData.getEnergyReceive()); // 能量值回复
//			checkValueCanPutIntoMap(map, AttributeType.STRUCK_ENERGY_RECEIVE, attrData.getStruckEnergy());// 击杀增加能量
//			checkValueCanPutIntoMap(map, AttributeType.ATTACK_ENERGY_RECEIVE, attrData.getAttackEnergy());// 攻击能量
//			checkValueCanPutIntoMap(map, AttributeType.ENERGY_TRANS, attrData.getEnergyTrans());// 能量转化
//			checkValueCanPutIntoMap(map, AttributeType.CUT_HURT, attrData.getCutHurt());// 伤害减免
//			checkValueCanPutIntoMap(map, AttributeType.CUT_CRIT_HURT, attrData.getCutCritHurt());// 暴击伤害减免
//			checkValueCanPutIntoMap(map, AttributeType.RESIST, attrData.getResist());// 抵抗
//			checkValueCanPutIntoMap(map, AttributeType.ADD_CURE, attrData.getAddCure());// 受到治疗效果增加
//			checkValueCanPutIntoMap(map, AttributeType.CUT_CURE, attrData.getCutCure());// 受到治疗效果减少
//			checkValueCanPutIntoMap(map, AttributeType.LIFE_GROWUP, attrData.getLifeGrowUp());// 生命成长
//			checkValueCanPutIntoMap(map, AttributeType.ATTACK_GROWUP, attrData.getAttackGrowUp());// 攻击成长
//			checkValueCanPutIntoMap(map, AttributeType.PHYSICQUE_DEF_GROWUP, attrData.getPhysicqueDefGrowUp());// 体魄防御成长
//			checkValueCanPutIntoMap(map, AttributeType.SPIRITD_EFG_ROWUP, attrData.getSpiritDefGrowUp());// 精神防御成长
//			checkValueCanPutIntoMap(map, AttributeType.ENCHANT_EXP, attrData.getEnchantExp());// 附灵经验
//			checkValueCanPutIntoMap(map, AttributeType.SKILLL_EVEL, attrData.getSkillLevel());// 技能总等级
//			checkValueCanPutIntoMap(map, AttributeType.ATTACK_TYPE, attrData.getAttackType());// 攻击类型
//			checkValueCanPutIntoMap(map, AttributeType.DODGE, attrData.getDodge());// 闪避
//			checkValueCanPutIntoMap(map, AttributeType.HIT, attrData.getHit());// 命中
//			checkValueCanPutIntoMap(map, AttributeType.ENERGY_PER_SECOND, attrData.getEnergyPerSecond());// 每秒恢复的能量
//			checkValueCanPutIntoMap(map, AttributeType.HARD_STRAIGHT, attrData.getHardStraight());// 硬直阀值
//			checkValueCanPutIntoMap(map, AttributeType.REACTION_TIME, attrData.getReactionTime());// 反应时间
//			checkValueCanPutIntoMap(map, AttributeType.ATTACK_DISTANCE, attrData.getAttackDistance());// 攻击距离
//			checkValueCanPutIntoMap(map, AttributeType.ATTACK_SPEED, attrData.getAttackSpeed());// 攻击速度
//			checkValueCanPutIntoMap(map, AttributeType.MOVE_SPEED, attrData.getMoveSpeed());// 移动速度
//			checkValueCanPutIntoMap(map, AttributeType.ATTACK_HURT, attrData.getAttackHurt());// 攻击伤害
//			checkValueCanPutIntoMap(map, AttributeType.VIEW_RANGE, attrData.getViewRange());// 视野范围
//			checkValueCanPutIntoMap(map, AttributeType.VOLUME_RADIUS, attrData.getVolumeRadius());// 人物半径
//			checkValueCanPutIntoMap(map, AttributeType.DO_HURT, attrData.getDoHurt());// 硬直界限
//		}
//
//		return map;
//	}
//
//	public static AttrData parseMap2AttrData(Map<Integer, Float> map) {
//		AttrData attrData = new AttrData();
//		if (map == null) {
//			return attrData;
//		}
//
//		for (Entry<Integer, Float> e : map.entrySet()) {
//			int attrType = e.getKey();
//			float value = e.getValue();
//			// 最大生命值
//			if (attrType == AttributeType.LIFE.getOrder()) {
//				attrData.setLife((int) value);
//			}
//			// 能量值
//			if (attrType == AttributeType.ENERGY.getOrder()) {
//				attrData.setEnergy((int) value);
//			}
//			// 攻击
//			if (attrType == AttributeType.SPIRIT_ATTACK.getOrder()) {
//				attrData.setAttack((int) value);
//			}// 体魄防御
//			if (attrType == AttributeType.PHYSIQUE_DEF.getOrder()) {
//				attrData.setPhysiqueDef((int) value);
//			} // 精神防御
//			if (attrType == AttributeType.SPIRIT_DEF.getOrder()) {
//				attrData.setSpiritDef((int) value);
//			}// 攻击吸血
//			if (attrType == AttributeType.ATTACK_VAMPIRE.getOrder()) {
//				attrData.setAttackVampire((int) value);
//			}// 暴击率
//			if (attrType == AttributeType.CRITICAL.getOrder()) {
//				attrData.setCritical((int) value);
//			}
//			// 暴击伤害提升
//			if (attrType == AttributeType.CRITICAL_HURT.getOrder()) {
//				attrData.setCriticalHurt((int) value);
//			}
//			// 韧性
//			if (attrType == AttributeType.TOUGHNESS.getOrder()) {
//				attrData.setToughness((int) value);
//			}
//			// 生命回复
//			if (attrType == AttributeType.LIFE_RECEIVE.getOrder()) {
//				attrData.setLifeReceive((int) value);
//			}
//			// 能量值回复
//			if (attrType == AttributeType.ENERGY_RECEIVE.getOrder()) {
//				attrData.setEnergyReceive((int) value);
//			}
//			// 击杀增加能量
//			if (attrType == AttributeType.STRUCK_ENERGY_RECEIVE.getOrder()) {
//				attrData.setStruckEnergy((int) value);
//			}
//			// 攻击能量
//			if (attrType == AttributeType.ATTACK_ENERGY_RECEIVE.getOrder()) {
//				attrData.setAttackEnergy((int) value);
//			}
//			// 能量转化
//			if (attrType == AttributeType.ENERGY_TRANS.getOrder()) {
//				attrData.setEnergyTrans((int) value);
//			}
//			// 伤害减免
//			if (attrType == AttributeType.CUT_HURT.getOrder()) {
//				attrData.setCutHurt((int) value);
//			}
//			// 暴击伤害减免
//			if (attrType == AttributeType.CUT_CRIT_HURT.getOrder()) {
//				attrData.setCutCritHurt((int) value);
//			}
//			// 抵抗
//			if (attrType == AttributeType.RESIST.getOrder()) {
//				attrData.setResist((int) value);
//			}
//			// 受到治疗效果增加
//			if (attrType == AttributeType.ADD_CURE.getOrder()) {
//				attrData.setAddCure((int) value);
//			}
//			// 受到治疗效果减少
//			if (attrType == AttributeType.CUT_CURE.getOrder()) {
//				attrData.setCutCure((int) value);
//			}
//			// 生命成长
//			if (attrType == AttributeType.LIFE_GROWUP.getOrder()) {
//				attrData.setLifeGrowUp((int) value);
//			}
//			// 攻击成长
//			if (attrType == AttributeType.ATTACK_GROWUP.getOrder()) {
//				attrData.setAttackGrowUp((int) value);
//			}
//			// 体魄防御成长
//			if (attrType == AttributeType.PHYSICQUE_DEF_GROWUP.getOrder()) {
//				attrData.setPhysicqueDefGrowUp((int) value);
//			}
//			// 精神防御成长
//			if (attrType == AttributeType.SPIRITD_EFG_ROWUP.getOrder()) {
//				attrData.setSpiritDefGrowUp((int) value);
//			}
//			// 附灵经验
//			if (attrType == AttributeType.ENCHANT_EXP.getOrder()) {
//				attrData.setEnchantExp((int) value);
//			}
//			// 技能总等级
//			if (attrType == AttributeType.SKILLL_EVEL.getOrder()) {
//				attrData.setSkillLevel((int) value);
//			}
//			// 攻击类型
//			if (attrType == AttributeType.ATTACK_TYPE.getOrder()) {
//				attrData.setAttackType((int) value);
//			}
//			// 闪避
//			if (attrType == AttributeType.DODGE.getOrder()) {
//				attrData.setDodge((int) value);
//			}
//			// 命中
//			if (attrType == AttributeType.HIT.getOrder()) {
//				attrData.setHit((int) value);
//			}
//			// 每秒恢复的能量
//			if (attrType == AttributeType.ENERGY_PER_SECOND.getOrder()) {
//				attrData.setEnergyPerSecond((int) value);
//			}
//			// 硬直阀值
//			if (attrType == AttributeType.HARD_STRAIGHT.getOrder()) {
//				attrData.setHardStraight(value);
//			}
//			// 反应时间
//			if (attrType == AttributeType.REACTION_TIME.getOrder()) {
//				attrData.setReactionTime(value);
//			}
//			// 攻击距离
//			if (attrType == AttributeType.ATTACK_DISTANCE.getOrder()) {
//				attrData.setAttackDistance(value);
//			}
//			// 攻击速度
//			if (attrType == AttributeType.ATTACK_SPEED.getOrder()) {
//				attrData.setAttackSpeed(value);
//			}
//			// 移动速度
//			if (attrType == AttributeType.MOVE_SPEED.getOrder()) {
//				attrData.setMoveSpeed(value);
//			}
//			// 攻击伤害
//			if (attrType == AttributeType.ATTACK_HURT.getOrder()) {
//				attrData.setAttackHurt(value);
//			}
//			// 视野范围
//			if (attrType == AttributeType.VIEW_RANGE.getOrder()) {
//				attrData.setViewRange(value);
//			}
//			// 人物半径
//			if (attrType == AttributeType.VOLUME_RADIUS.getOrder()) {
//				attrData.setVolumeRadius(value);
//			}
//			// 硬直界限
//			if (attrType == AttributeType.DO_HURT.getOrder()) {
//				attrData.setDoHurt(value);
//			}
//		}
//
//		return attrData;
//	}
//
//	/**
//	 * 检查属性值是否可以放到列表中去
//	 * 
//	 * @param map
//	 * @param attrType
//	 * @param value
//	 */
//	private static void checkValueCanPutIntoMap(Map<Integer, Float> map, AttributeType attrType, float value) {
//		if (map == null || value <= 0) {
//			return;
//		}
//
//		map.put(attrType.getOrder(), value);
//	}
// }