package com.playerdata.activity.rateType;

import org.apache.commons.lang3.StringUtils;

import com.rwbase.dao.copypve.CopyType;

public enum ActivityRateTypeEnum{	
	// implements TypeIdentification
	ELITE_copy_DOUBLE("1"),
	Normal_copy_DOUBLE("2"),
	JBZD_DOUBLE("3"),
	LXSG_DOUBLE("4"),
	SCHJ_DOUBLE("5"),
	ELITE_copy_EXP_DOUBLE("6"),
	Normal_copy_EXP_DOUBLE("7");

	
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
			
		}else if(copyType == CopyType.COPY_TYPE_ELITE){
			
		}else if(copyType == CopyType.COPY_TYPE_TRIAL_JBZD){
			System.out.println(" activityrate .类型=" + JBZD_DOUBLE );
			return JBZD_DOUBLE;
		}else if(copyType == CopyType.COPY_TYPE_TRIAL_LQSG){
			
		}else if (copyType == CopyType.COPY_TYPE_CELESTIAL){
			
		}		
		return null;
	}	
}
