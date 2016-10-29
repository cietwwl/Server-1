package com.bm.worldBoss.cfg;

import com.rwbase.common.enu.eSpecialItemId;


public class WBBuyBuffCfg {

	private String id;
	
	private eSpecialItemId costType;
	
	private int costCount;		//消耗数量

	private int buffValue;	//攻击力增加万分比
	


	public int getBuffValue() {
		return buffValue;
	}

	public String getId() {
		return id;
	}

	public eSpecialItemId getCostType() {
		return costType;
	}

	public int getCostCount() {
		return costCount;
	}

	

	
	
	



	
	
}
