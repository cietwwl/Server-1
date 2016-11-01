package com.playerdata.charge.cfg;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public enum ChargeTypeEnum {
	None("0"),
	Normal("1"),//普通充值
	MonthCard("2"),//月卡
	VipMonthCard("3");//至尊终身卡
	
	private String cfgId;
	
	private static final Map<String, ChargeTypeEnum> _mapByCfgId ;
	static {
		ChargeTypeEnum[] allValues = values();
		Map<String, ChargeTypeEnum> map = new HashMap<String, ChargeTypeEnum>(allValues.length, 1.5f);
		for (ChargeTypeEnum e : allValues) {
			map.put(e.getCfgId(), e);
		}
		_mapByCfgId = Collections.unmodifiableMap(map);
	}

	
	public String getCfgId(){
		return cfgId;
	}
	
	private ChargeTypeEnum (String cfgId){
		this.cfgId = cfgId;
	} 

	public static ChargeTypeEnum getById(String cfgId) {
		return _mapByCfgId.get(cfgId);
	}
	
	public String getName(){
		if(StringUtils.equals(cfgId, "2")){
			return "月卡";
		}else if(StringUtils.equals(cfgId, "3")){
			return "至尊终身卡";
		}
		return "";
	}
}

