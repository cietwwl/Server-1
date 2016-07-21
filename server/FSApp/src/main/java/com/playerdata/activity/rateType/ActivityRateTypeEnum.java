package com.playerdata.activity.rateType;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.copypve.CopyType;

public enum ActivityRateTypeEnum{	
	// implements TypeIdentification
	
	Normal_copy_EXP_DOUBLE("301",eSpecialItemId.Coin.getValue(),eSpecialItemId.PlayerExp.getValue(),eSpecialItemId.item.getValue(),-1),//普通副本
	
	ELITE_copy_DOUBLE("302",eSpecialItemId.Coin.getValue(),eSpecialItemId.PlayerExp.getValue(),eSpecialItemId.item.getValue(),-1),//精英副本,魂石未实现
	
	LXSG_DOUBLE("303",-1,-1,eSpecialItemId.item.getValue(),-1),//炼息山谷
	
	JBZD_DOUBLE("304",eSpecialItemId.Coin.getValue(),-1,-1,-1),//聚宝之地	
	
	SCHJ_DOUBLE("305",-1,-1,eSpecialItemId.item.getValue(),-1),//生存幻境		
	
	TOWER_DOUBLE("306",eSpecialItemId.Coin.getValue(),eSpecialItemId.BraveCoin.getValue(),eSpecialItemId.item.getValue(),-1),//万仙阵
	
	WARFARE_DOUBLE("309",-1,-1,-1,-1);//无尽战火，功能已另做
	
	private String cfgId;
	private int num0;
	private int num1;
	private int num2;
	private int num3;
	private ActivityRateTypeEnum(String cfgId,int num0,int num1,int num2,int num3){
		this.cfgId = cfgId;
		this.num0 = num0;
		this.num1 = num1;
		this.num2 = num2;
		this.num3 = num3;
	} 
	
	public String getCfgId(){
		return cfgId;
	}
	
	
	public int getNum0() {
		return num0;
	}

	public void setNum0(int num0) {
		this.num0 = num0;
	}

	public int getNum1() {
		return num1;
	}

	public void setNum1(int num1) {
		this.num1 = num1;
	}

	public int getNum2() {
		return num2;
	}

	public void setNum2(int num2) {
		this.num2 = num2;
	}

	public int getNum3() {
		return num3;
	}

	public void setNum3(int num3) {
		this.num3 = num3;
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
			 if(rewardsType == 1){
				return Normal_copy_EXP_DOUBLE;
			}
		}else if(copyType == CopyType.COPY_TYPE_ELITE){
			if(rewardsType == 0){
				return ELITE_copy_DOUBLE;
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
