package com.playerdata.activity.rateType;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.rwbase.dao.copypve.CopyType;

public enum ActivityRateTypeEnum{	
	// implements TypeIdentification
	
	Normal_copy_EXP_DOUBLE("307"),//普通副本经验双倍
	Normal_copy_DOUBLE("302"),//普通副本道具双倍
	
	ELITE_copy_DOUBLE("301"),//精英副本道具双倍
	ELITE_copy_EXP_DOUBLE("306"),//精英副本经验双倍
	
	JBZD_DOUBLE("303"),//聚宝之地道具双倍
	
	LXSG_DOUBLE("304"),//炼息山谷道具双倍
	
	SCHJ_DOUBLE("305"),//生存幻境道具双倍		
	
	TOWER_DOUBLE("308"),//万仙阵道具金币双倍
	
	WARFARE_DOUBLE("309");//无尽战火道具双倍
	
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
	
	/**rewardsType ,0为道具，1为经验,2为金币,3为金币+道具*/
	public static ActivityRateTypeEnum getByCopyTypeAndRewardsType(int copyType,int rewardsType){
		if(copyType == CopyType.COPY_TYPE_NORMAL){
			if(rewardsType == 0){
				return Normal_copy_DOUBLE;
			}else if(rewardsType == 1){
				return Normal_copy_EXP_DOUBLE;
			}
		}else if(copyType == CopyType.COPY_TYPE_ELITE){
			if(rewardsType == 0){
				return ELITE_copy_DOUBLE;
			}else if(rewardsType == 1){
				return ELITE_copy_EXP_DOUBLE;
			}
		}else if(copyType == CopyType.COPY_TYPE_TRIAL_JBZD){
			return JBZD_DOUBLE;
		}else if(copyType == CopyType.COPY_TYPE_TRIAL_LQSG){
			return LXSG_DOUBLE;
		}else if (copyType == CopyType.COPY_TYPE_CELESTIAL){
			return SCHJ_DOUBLE;
		}else if(copyType == CopyType.COPY_TYPE_TOWER){
			if(rewardsType == 3){
				return TOWER_DOUBLE;
			}		
		}else if (copyType == CopyType.COPY_TYPE_WARFARE){
			return WARFARE_DOUBLE;
		}
		return null;
	}	
}
