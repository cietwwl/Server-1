package com.playerdata.activity;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author 阳小飞；活动页签枚举；前个变量为该页签对应的枚举，后个变量表明此页签属于哪个活动功能；此枚举主要对应红点
 * 每当某个活动的数据枚举或事件枚举新增了一项枚举会触发新的页签时，需要在红点枚举同步增加
 * 通用活动一的每一个触发事件都会新增页签，通用活动三每个双倍类型也会触发；不同的排行榜奖励或者兑换活动都会新增页签
 *
 */
public enum ActivityRedPointEnum {
	Login("11",ActivityTypeEnum.ActivityCountType),//通用活动一
	GoldSpending("12",ActivityTypeEnum.ActivityCountType),
	CopyWin("13",ActivityTypeEnum.ActivityCountType),
	ElityCopyWin("14",ActivityTypeEnum.ActivityCountType),
	BattleTower("15",ActivityTypeEnum.ActivityCountType),
	GambleCoin("16",ActivityTypeEnum.ActivityCountType),
	Charge("17",ActivityTypeEnum.ActivityCountType),
	GambleGold("18",ActivityTypeEnum.ActivityCountType),
	
	Daily("201",ActivityTypeEnum.ActivityDailyType),// 通用活动二	
	
	ELITE_copy_DOUBLE("301",ActivityTypeEnum.ActivityRateType),//精英副本道具双倍
	Normal_copy_DOUBLE("302",ActivityTypeEnum.ActivityRateType),//普通副本道具双倍
	JBZD_DOUBLE("303",ActivityTypeEnum.ActivityRateType),//聚宝之地道具双倍	
	LXSG_DOUBLE("304",ActivityTypeEnum.ActivityRateType),//炼息山谷道具双倍	
	SCHJ_DOUBLE("305",ActivityTypeEnum.ActivityRateType),//生存幻境道具双倍		
	ELITE_copy_EXP_DOUBLE("306",ActivityTypeEnum.ActivityRateType),//精英副本经验双倍
	Normal_copy_EXP_DOUBLE("307",ActivityTypeEnum.ActivityRateType),//普通副本经验双倍
	TOWER_DOUBLE("308",ActivityTypeEnum.ActivityRateType),//万仙阵道具金币双倍	
	WARFARE_DOUBLE("309",ActivityTypeEnum.ActivityRateType),//无尽战火道具双倍
	
	role_online("401",ActivityTypeEnum.ActivityTimeCountType),
	
	redEnvelope("501",ActivityTypeEnum.ActivityRedEnvelope),
	
	Vitality("801",ActivityTypeEnum.ActivityVitalyType),
	VitalityTwo("810",ActivityTypeEnum.ActivityVitalyType),
	
	DragonBoatFestival("901",ActivityTypeEnum.ActivityExchangeType),
	MidAutumnFestival("902",ActivityTypeEnum.ActivityExchangeType),
	ExChangeActive("951",ActivityTypeEnum.ActivityExchangeType),
	
	FIGHTING("1001",ActivityTypeEnum.ActivityRankType),//战力大比拼
	ARENA("1002",ActivityTypeEnum.ActivityRankType),//竞技之王
	
	DailyDiscount("1101",ActivityTypeEnum.ActivityDailyDiscountType);//超值欢乐购	
	
	
	
	
	private String cfgId;
	
	private ActivityTypeEnum type;
	
	/**
	 * 
	 * @param 此类用来将通用活动的枚举统一；
	 */
	private ActivityRedPointEnum(String cfgid,ActivityTypeEnum type){
		this.cfgId = cfgid;
		this.type = type;
	}
	
	public String getCfgId(){
		return cfgId;
	}
	
	public ActivityTypeEnum getType(){
		return type;
	}
	
	public static ActivityRedPointEnum getEnumByCfgId(String cfgid){
		ActivityRedPointEnum target = null;
		for(ActivityRedPointEnum eNum:values()){
			if(StringUtils.equals(eNum.getCfgId(), cfgid)){
				target = eNum;
				break;
			}
		}		
		return target;
	}
	
	
}