package com.playerdata.activity.dailyCountType;

import org.apache.commons.lang3.StringUtils;

public enum ActivityDailyTypeEnum{	// implements TypeIdentification

	Daily("19999"),
	
	
	LoginDaily("10001"),
	TreasureLandDaily("10002"),//聚宝之地
	BattleTowerDaily("10003"),//封神台
	AttachDaily("10004"),//附灵
	UpGradeStarDaily("10005"),//升星
	AdvanceDaily("10006"),//进阶
	ArenaDaily("10007"),//竞技场
	CoinSpendDaily("10008"),//花金币
	GambleGoldDaily("10009"),//钻石钓鱼次数 
	ChargeDaily("10010"),//充值
	GoldSpendDaily("10011");//花钻石 
	
	
	
	private String cfgId;
	private ActivityDailyTypeEnum(String cfgId){
		this.cfgId = cfgId;
	} 
	
	public String getCfgId(){
		return cfgId;
	}
	
	
	public static ActivityDailyTypeEnum getById(String cfgId){
		ActivityDailyTypeEnum target = null;
		for (ActivityDailyTypeEnum enumTmp : values()) {
			if(StringUtils.equals(cfgId, enumTmp.getCfgId())){
				target = enumTmp;
				break;
			}
		}	
		
		return target;
	}

	
}
