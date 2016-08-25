package com.playerdata.activity.rateType;

import org.apache.commons.lang3.StringUtils;

import com.rwbase.dao.copypve.CopyType;

public enum ActivityRateTypeEnum{	
	// implements TypeIdentification
	ELITE_copy_DOUBLE("1"),//精英副本道具双倍
	Normal_copy_DOUBLE("2"),//普通副本道具双倍
	JBZD_DOUBLE("3"),//聚宝之地道具双倍
	LXSG_DOUBLE("4"),//炼息山谷道具双倍
	SCHJ_DOUBLE("5"),//生存幻境道具双倍
	ELITE_copy_EXP_DOUBLE("6"),//精英副本经验双倍
	Normal_copy_EXP_DOUBLE("7");//普通副本经验双倍

	
	private String cfgId;
	private ActivityRateTypeEnum(String cfgId){
		this.cfgId = cfgId;
	} 
	
	public String getCfgId(){
		return cfgId;
	}
	
	
	public static ActivityRateTypeEnum getById(String cfgId){
		ActivityRateTypeEnum target = null;
		for (ActivityRateTypeEnum enumTmp : values()) {
			if(StringUtils.equals(cfgId, enumTmp.getCfgId())){
				target = enumTmp;
				break;
			}
		}	
		
		return target;
	}
	
	/**rewardsType ,0为道具，1为经验*/
	public static ActivityRateTypeEnum getByCopyTypeAndRewardsType(int copyType,int rewardsType){
		if(copyType == CopyType.COPY_TYPE_NORMAL){
			if(rewardsType == 0){
				return Normal_copy_DOUBLE;
			}else{
				return Normal_copy_EXP_DOUBLE;
			}
		}else if(copyType == CopyType.COPY_TYPE_ELITE){
			if(rewardsType == 0){
				return ELITE_copy_DOUBLE;
			}else{
				return ELITE_copy_EXP_DOUBLE;
			}
		}else if(copyType == CopyType.COPY_TYPE_TRIAL_JBZD){
			return JBZD_DOUBLE;
		}else if(copyType == CopyType.COPY_TYPE_TRIAL_LQSG){
			return LXSG_DOUBLE;
		}else if (copyType == CopyType.COPY_TYPE_CELESTIAL){
			return SCHJ_DOUBLE;
		}		
		return null;
	}	
}
