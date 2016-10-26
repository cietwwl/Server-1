package com.playerdata.fightinggrowth.calc;

/**
 * @Author HC
 * @date 2016年10月25日 下午4:39:31
 * @desc
 **/

public enum FightingCalcComponentType {

	BASE("基础", new FSGetBaseFightingCalc()), // 基础战力
	EQUIP("装备", new FSGetEquipFightingCalc()), // 装备战力
	FASHION("时装", new FSGetFashionFightingCalc()), // 时装战力
	FETTERS("羁绊", new FSGetFettersFightingCalc()), // 羁绊战力
	FIX_EQUIP("神器", new FSGetFixEquipFightingCalc()), // 神器战力
	GEM("宝石", new FSGetGemFightingCalc()), // 宝石战力
	GROUP_SKILL("帮派技能", new FSGetGroupSkillFightingCalc()), // 帮派技能战力
	MAGIC("法宝", new FSGetMagicFightingCalc()), // 法宝战力
	SKILL("英雄技能", new FSGetSkillFightingCalc()), // 技能战力
	TAOIST("道术", new FSGetTaoistFightingCalc()), // 道术战力
	;

	public final String desc;
	public final IFightingCalc calc;

	private FightingCalcComponentType(String desc, IFightingCalc calc) {
		this.desc = desc;
		this.calc = calc;
	}
}