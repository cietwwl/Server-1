package com.playerdata.activity.fortuneCatType;


public enum ActivityFortuneTypeEnum {
	FortuneCat("90001");// 超值欢乐购

	private String cfgId;

	private ActivityFortuneTypeEnum(String cfgId) {
		this.cfgId = cfgId;
	}

	public String getCfgId() {
		return cfgId;
	}

	public static ActivityFortuneTypeEnum getById(String cfgId) {
		if (FortuneCat.cfgId.equals(cfgId)) {
			return FortuneCat;
		} else {
			return null;
		}
	}
}
