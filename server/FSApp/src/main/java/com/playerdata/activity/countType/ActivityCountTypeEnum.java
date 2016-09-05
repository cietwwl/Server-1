package com.playerdata.activity.countType;

import java.util.HashMap;

public enum ActivityCountTypeEnum { // implements TypeIdentification
	Login("1"), GoldSpending("2"), CopyWin("3"), ElityCopyWin("4"), BattleTower("5"), GambleCoin("6"), Charge("7"), GambleGold("8");

	private String cfgId;

	private ActivityCountTypeEnum(String cfgId) {
		this.cfgId = cfgId;
	}

	public String getCfgId() {
		return cfgId;
	}

	private static HashMap<String, ActivityCountTypeEnum> map;

	static {
		ActivityCountTypeEnum[] array = values();
		map = new HashMap<String, ActivityCountTypeEnum>();
		for (int i = 0; i < array.length; i++) {
			ActivityCountTypeEnum typeEnum = array[i];
			map.put(typeEnum.getCfgId(), typeEnum);
		}
	}

	public static ActivityCountTypeEnum getById(String cfgId) {
		return map.get(cfgId);
	}

}
