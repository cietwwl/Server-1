package com.playerdata.activity.timeCardType;



public enum ActivityTimeCardTypeEnum { // implements TypeIdentification
	Month("100001");

	private String cfgId;

	private ActivityTimeCardTypeEnum(String cfgId) {
		this.cfgId = cfgId;
	}

	public String getCfgId() {
		return cfgId;
	}
	
	public static ActivityTimeCardTypeEnum getById(String cfgId) {
		if (Month.cfgId.equals(cfgId)) {
			return Month;
		} else {
			return null;
		}
	}
}
