package com.playerdata.activity.dailyCountType;

import java.util.HashMap;

public enum ActivityDailyTypeEnum{
	
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
	
	private static HashMap<String, ActivityDailyTypeEnum> map;
	
	static {
		ActivityDailyTypeEnum[] array = values();
		map = new HashMap<String, ActivityDailyTypeEnum>();
		for(int i = 0;i< array.length;i++){
			ActivityDailyTypeEnum activityDailyTypeEnum = array[i];
			map.put(activityDailyTypeEnum.getCfgId(), activityDailyTypeEnum);
		}
	}
	
	public static ActivityDailyTypeEnum getById(String id){
		return map.get(id);
	}
}
