package com.playerdata.activity.fortuneCatType;


public enum ActivityFortuneTypeEnum {
	
	FortuneCat(90001);// 招财猫

	private int cfgId;

	private ActivityFortuneTypeEnum(int cfgId) {
		this.cfgId = cfgId;
	}

	public int getCfgId() {
		return cfgId;
	}

	public static ActivityFortuneTypeEnum getById(int cfgId) {
		if (FortuneCat.cfgId == cfgId) {
			return FortuneCat;
		} else {
			return null;
		}
	}
}
