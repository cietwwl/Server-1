package com.rwbase.common.attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.HPCUtil;
import com.rw.fsutil.common.TypeIdentification;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attrdata.AttrData.Builder;

/**
 * <pre>
 * <b>目前这些枚举当中的第三个参数，
 * 和第四个参数视作无任何作用，
 * 先不要用于逻辑中<b>
 * </pre>
 * 
 * @author HC
 *
 */
public enum AttributeType implements TypeIdentification {

	/** 基础生命值... */
	LIFE(1, "life", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setLife(value);
		}
	},
	/** 当前生命值 */
	CURRENTL_LIFE(2, "", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
		}
	},
	/** 能量值 */
	ENERGY(3, "energy", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setEnergy(value);
		}
	},
	/** 当前能量值 */
	CURRENT_ENERGY(4, "", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
		}
	},
	/** 攻击.. */
	PHYSIQUE_ATTACK(5, "physiqueAttack", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setPhysiqueAttack(value);
		}
	},
	/** 攻击.. */
	SPIRIT_ATTACK(23, "spiritAttack", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setSpiritAttack(value);
		}
	},
	/** 体魄防御... */
	PHYSIQUE_DEF(6, "physiqueDef", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setPhysiqueDef(value);
		}
	},
	/** 精神防御... */
	SPIRIT_DEF(7, "spiritDef", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setSpiritDef(value);
		}
	},
	/** 攻击吸血... */
	ATTACK_VAMPIRE(8, "attackVampire", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setAttackVampire(value);
		}
	},
	/** 暴击率... */
	CRITICAL(9, "critical", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setCritical(value);
		}
	},
	/** 暴击伤害提升... */
	CRITICAL_HURT(10, "criticalHurt", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setCriticalHurt(value);
		}
	},
	/** 韧性... */
	TOUGHNESS(11, "toughness", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setToughness(value);
		}
	},
	/** 生命回复... */
	LIFE_RECEIVE(12, "lifeReceive", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setLifeReceive(value);
		}
	},
	/** 躲闪 */
	DODGE(13, "dodge", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setDodge(value);
		}
	},
	/** 命中 */
	HIT(14, "hit", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setHit(value);
		}
	},
	/** 能量回复... */
	ENERGY_RECEIVE(15, "energyReceive", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setEnergyReceive(value);
		}
	},
	/** 击杀回复能量... */
	STRUCK_ENERGY_RECEIVE(16, "struckEnergy", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setStruckEnergy(value);
		}
	},
	/** 攻击回复能量... */
	ATTACK_ENERGY_RECEIVE(17, "attackEnergy", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setAttackEnergy(value);
		}
	},
	/** 能量转化... */
	ENERGY_TRANS(18, "energyTrans", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setEnergyTrans(value);
		}
	},
	/** 攻击距离... */
	ATTACK_DISTANCE(19, "attackDistance", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setAttackDistance(value / AttributeConst.BIG_FLOAT);
		}
	},
	/** 攻击速度... */
	ATTACK_SPEED(20, "attackSpeed", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setAttackSpeed(value / AttributeConst.BIG_FLOAT);
		}
	},
	/** 攻击频率... */
	ATTACK_FREQUENCE(21, "", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
		}
	},
	/** 移动速度... */
	MOVE_SPEED(22, "moveSpeed", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setMoveSpeed(value / AttributeConst.BIG_FLOAT);
		}
	},
	/** 伤害减免 */
	CUT_HURT(24, "cutHurt", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setCutHurt(value);
		}
	},
	/** 暴击伤害减免 */
	CUT_CRIT_HURT(25, "cutCritHurt", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setCutCritHurt(value);
		}
	},
	/** 抵抗 */
	RESIST(26, "resist", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setResist(value);
		}
	},
	/** 受到治疗效果增加 */
	ADD_CURE(27, "addCure", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setAddCure(value);
		}
	},
	/** 受到治疗效果减少 */
	CUT_CURE(28, "cutCure", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setCutCure(value);
		}
	},
	/** 反应时间 */
	REACTION_TIME(29, "reactionTime", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setReactionTime(value / AttributeConst.BIG_FLOAT);
		}
	},
	/** 硬直阀值 */
	HARD_STRAIGHT(30, "hardStraight", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setHardStraight(value / AttributeConst.BIG_FLOAT);
		}
	},
	/** 反弹伤害 */
	DAMAGE_REFLECT(31, "", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
		}
	},
	/** 护甲穿透 */
	ARMOR_PENTRATION(32, "", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
		}
	},
	// ------------------------------------
	// /** 击杀增加能量 */
	// STRUCK_ENERGY(33),
	// /** 攻击能量 */
	// ATTACK_ENERGY(34),
	/** 生命成长 */
	LIFE_GROWUP(35, "lifeGrowUp", false, 1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setLifeGrowUp(value);
		}
	},
	/** 攻击成长 */
	ATTACK_GROWUP(36, "attackGrowUp", false, 5) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setAttackGrowUp(value);
		}
	},
	/** 体魄防御成长 */
	PHYSICQUE_DEF_GROWUP(37, "physicqueDefGrowUp", false, 6) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setPhysicqueDefGrowUp(value);
		}
	},
	/** 精神防御成长 */
	SPIRITD_EFG_ROWUP(38, "spiritDefGrowUp", false, 7) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setSpiritDefGrowUp(value);
		}
	},
	/** 附灵经验 */
	ENCHANT_EXP(39, "enchantExp", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setEnchantExp(value);
		}
	},
	/** 技能总等级 */
	SKILLL_EVEL(40, "skillLevel", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setSkillLevel(value);
		}
	},
	/** 攻击类型 */
	ATTACK_TYPE(41, "attackType", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setAttackType(value);
		}
	},
	/** 每秒恢复的能量 */
	ENERGY_PER_SECOND(42, "energyPerSecond", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setEnergyPerSecond(value);
		}
	},
	/** 攻击伤害 */
	ATTACK_HURT(43, "attackHurt", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setAttackHurt(value / AttributeConst.BIG_FLOAT);
		}
	},
	/** 视野范围 */
	VIEW_RANGE(44, "viewRange", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setViewRange(value / AttributeConst.BIG_FLOAT);
		}
	},
	/** 人物半径 */
	VOLUME_RADIUS(45, "volumeRadius", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setVolumeRadius(value / AttributeConst.BIG_FLOAT);
		}
	},
	/** 硬直界限 */
	DO_HURT(46, "doHurt", false, -1) {
		@Override
		public void setAttributeValue(Builder builder, int value) {
			builder.setDoHurt(value / AttributeConst.BIG_FLOAT);
		}
	};

	private int order;
	/**
	 * 对应的AttrData中的字段
	 */
	public final String attrFieldName;
	public final boolean impactReCalc;// 是否会影响到其他属性，导致其他属性需要二次计算
	/**
	 * 被影响到的属性，就说明凡是这个类型的属性，无论算出来是多少，都是直接参与到被影响属性中
	 */
	public final int impactAttrType;// 影响到的属性类型。

	// public final boolean isRate;// 是否是百分比属性

	public abstract void setAttributeValue(AttrData.Builder builder, int value);

	AttributeType(int order, String attrFieldName, boolean impactReCalc, int impactAttrType) {
		this.order = order;
		this.attrFieldName = attrFieldName;
		this.impactReCalc = impactReCalc;
		this.impactAttrType = impactAttrType;
	}

	public int getOrder() {
		return order;
	}

	/** <需要重算的类型,是那个属性影响了> */
	private static List<AttributeType> recalcAttrList;// 需要二次计算的属性列表
	private static Map<Integer, Integer> recalcAttrMap;// 需要二次计算的属性被那个属性影响的对应
	private static AttributeType[] array;

	static {
		AttributeType[] values = values();
		TypeIdentification[] ordinalArray = HPCUtil.toMappedArray(values);
		array = new AttributeType[ordinalArray.length];
		HPCUtil.copy(ordinalArray, array);

		Map<Integer, Integer> recalcAttrMap = new HashMap<Integer, Integer>();
		List<AttributeType> recalcAttrList = new ArrayList<AttributeType>();
		for (int i = 0, len = array.length; i < len; i++) {
			AttributeType attributeType = array[i];
			if (attributeType == null) {
				continue;
			}

			if (attributeType.impactReCalc) {// 有影响到其他的属性
				AttributeType impactAttr = AttributeType.getAttributeType(attributeType.impactAttrType);
				if (impactAttr != null) {
					recalcAttrMap.put(attributeType.impactAttrType, attributeType.getTypeValue());
					recalcAttrList.add(impactAttr);
				}
			}
		}

		AttributeType.recalcAttrMap = Collections.unmodifiableMap(recalcAttrMap);
		AttributeType.recalcAttrList = Collections.unmodifiableList(recalcAttrList);
	}

	public static AttributeType getAttributeType(int type) {
		AttributeType att = array[type];
		if (att == null) {
			throw new ExceptionInInitializerError("找不到类型：" + type);
		}
		return att;
	}

	/**
	 * 获取需要二次计算的属性列表
	 * 
	 * @return
	 */
	public static List<AttributeType> getReCalcAttributeTypeList() {
		return recalcAttrList;
	}

	/**
	 * 获取某个需要重新计算的属性是受那个属性的影响
	 * 
	 * @param type
	 * @return
	 */
	public static AttributeType getReCalcAttributeImpactType(int type) {
		if (recalcAttrMap.containsKey(type)) {
			return null;
		}

		return getAttributeType(recalcAttrMap.get(type));
	}

	@Override
	public int getTypeValue() {
		return order;
	}
}