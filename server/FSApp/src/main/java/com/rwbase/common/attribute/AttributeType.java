package com.rwbase.common.attribute;

import com.common.HPCUtil;
import com.rw.fsutil.common.TypeIdentification;

public enum AttributeType implements TypeIdentification {

	/** 基础生命值... */
	LIFE(1),
	/** 当前生命值 */
	CURRENTL_LIFE(2),
	/** 能量值 */
	ENERGY(3), CURRENT_ENERGY(4),
	/** 攻击.. */
	ATTACK(5),
	/** 体魄防御... */
	PHYSIQUE_DEF(6),
	/** 精神防御... */
	SPIRIT_DEF(7),
	/** 攻击吸血... */
	ATTACK_VAMPIRE(8),
	/** 暴击率... */
	CRITICAL(9),
	/** 暴击伤害提升... */
	CRITICAL_HURT(10),
	/** 韧性... */
	TOUGHNESS(11),
	/** 生命回复... */
	LIFE_RECEIVE(12),
	/** 躲闪 */
	DODGE(13),
	/** 命中 */
	HIT(14),
	/** 能量回复... */
	ENERGY_RECEIVE(15),
	/** 击杀回复能量... */
	STRUCK_ENERGY_RECEIVE(16),
	/** 攻击回复能量... */
	ATTACK_ENERGY_RECEIVE(17),
	/** 能量转化... */
	ENERGY_TRANS(18),
	/** 攻击距离... */
	ATTACK_DISTANCE(19),
	/** 攻击速度... */
	ATTACK_SPEED(20),
	/** 攻击频率... */
	ATTACK_FREQUENCE(21),
	/** 移动速度... */
	MOVE_SPEED(22),
	// /** 伤害增加 */
	// ATTACK_HURT_ADD(23),
	/** 伤害减免 */
	CUT_HURT(24),
	/** 暴击伤害减免 */
	CUT_CRIT_HURT(25),
	/** 抵抗 */
	RESIST(26),
	/** 受到治疗效果增加 */
	ADD_CURE(27),
	/** 受到治疗效果减少 */
	CUT_CURE(28),
	/** 反应时间 */
	REACTION_TIME(29),
	/** 硬直阀值 */
	HARD_STRAIGHT(30),
	/** 反弹伤害 */
	DAMAGE_REFLECT(31),
	/** 护甲穿透 */
	ARMOR_PENTRATION(32),
	// ------------------------------------
	// /** 击杀增加能量 */
	// STRUCK_ENERGY(33),
	// /** 攻击能量 */
	// ATTACK_ENERGY(34),
	/** 生命成长 */
	LIFE_GROWUP(35),
	/** 攻击成长 */
	ATTACK_GROWUP(36),
	/** 体魄防御成长 */
	PHYSICQUE_DEF_GROWUP(37),
	/** 精神防御成长 */
	SPIRITD_EFG_ROWUP(38),
	/** 附灵经验 */
	ENCHANT_EXP(39),
	/** 技能总等级 */
	SKILLL_EVEL(40),
	/** 攻击类型 */
	ATTACK_TYPE(41),
	/** 每秒恢复的能量 */
	ENERGY_PER_SECOND(42),
	/** 攻击伤害 */
	ATTACK_HURT(43),
	/** 视野范围 */
	VIEW_RANGE(44),
	/** 人物半径 */
	VOLUME_RADIUS(45),
	/** 硬直界限 */
	DO_HURT(46);

	private int order;

	AttributeType(int order) {
		this.order = order;
	}

	public int getOrder() {
		return order;
	}

	private static AttributeType[] array;

	static {
		AttributeType[] values = values();
		TypeIdentification[] ordinalArray = HPCUtil.toMappedArray(values);
		array = new AttributeType[ordinalArray.length];
		HPCUtil.copy(ordinalArray, array);
	}

	public static AttributeType getAttributeType(int type) {
		AttributeType att = array[type];
		if (att == null) {
			throw new ExceptionInInitializerError("找不到类型：" + type);
		}
		return att;
	}

	@Override
	public int getTypeValue() {
		return order;
	}

}
