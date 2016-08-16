package com.playerdata.activity.limitHeroType;

import com.playerdata.activity.fortuneCatType.ActivityFortuneTypeEnum;

public enum ActivityLimitHeroEnum {
	LimitHero("120001");// 超值欢乐购

	private String cfgId;

	private ActivityLimitHeroEnum(String cfgId) {
		this.cfgId = cfgId;
	}

	public String getCfgId() {
		return cfgId;
	}

	public static ActivityLimitHeroEnum getById(String cfgId) {
		if (LimitHero.cfgId.equals(cfgId)) {
			return LimitHero;
		} else {
			return null;
		}
	}
}
