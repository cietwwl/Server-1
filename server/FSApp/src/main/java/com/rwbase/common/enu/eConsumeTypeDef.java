package com.rwbase.common.enu;

public enum eConsumeTypeDef {
	EquipStrengthConsume(9), // 主角装备强化道具
	MagicForgingConsume(10), // 法宝锻造道具
	EquipEnchantConsume(12), // 装备附灵道具
	ExpConsume(11), // 经验道具
	PowerConsume(13), // 体力道具
	CopySweepConsume(14), // 副本扫荡券
	OnlySellConsume(15),// 仅用于出售
	VipExpConsume(16);//Vip经验卡
	private int order;

	eConsumeTypeDef(int order) {
		this.order = order;
	}

	public int getOrder() {
		return order;
	}

	public static eConsumeTypeDef getDef(int value) {
		eConsumeTypeDef eAttr = null;
		for (int i = 0; i < eConsumeTypeDef.values().length; i++) {
			eAttr = eConsumeTypeDef.values()[i];
			if (eAttr.getOrder() == value) {
				break;
			}
		}
		return eAttr;
	}
}
