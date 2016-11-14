package com.rwbase.dao.commonsoul.pojo;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class CommonSoulConfig {

	private boolean canExchangeMainRoleSoul; // 是否可兑换主角魂石
	private boolean canExchangeOwnedHeroSoul; // 是否可兑换已拥有的英雄魂石
	private boolean canExchangeNotOwnedHeroSoul; // 是否可兑换为拥有的英雄魂石
//	private int exchangeRate; // 多少个万能魂石兑换一个普通魂石
	private int commonSoulStoneCfgId; // 万能魂石的itemCfgId
	private String commonSoulStoneName; // 万能魂石的名字
	
	public boolean isCanExchangeMainRoleSoul() {
		return canExchangeMainRoleSoul;
	}
	
	public boolean isCanExchangeOwnedHeroSoul() {
		return canExchangeOwnedHeroSoul;
	}
	
	public boolean isCanExchangeNotOwnedHeroSoul() {
		return canExchangeNotOwnedHeroSoul;
	}
	
//	public int getExchangeRate() {
//		return exchangeRate;
//	}

	public int getCommonSoulStoneCfgId() {
		return commonSoulStoneCfgId;
	}

	public void setCommonSoulStoneCfgId(int commonSoulStoneCfgId) {
		this.commonSoulStoneCfgId = commonSoulStoneCfgId;
	}

	public String getCommonSoulStoneName() {
		return commonSoulStoneName;
	}

	public void setCommonSoulStoneName(String commonSoulStoneName) {
		this.commonSoulStoneName = commonSoulStoneName;
	}
}
