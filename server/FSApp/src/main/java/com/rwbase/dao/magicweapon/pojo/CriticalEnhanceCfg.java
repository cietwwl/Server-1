package com.rwbase.dao.magicweapon.pojo;

import com.common.ListParser;

public class CriticalEnhanceCfg {
	private int matID;
	private String criticalPlans;
	private int[] plans;

	public void ExtraInit() {
		ParsePlans();
	}

	private void ParsePlans() {
		plans = ListParser.ParseIntList(criticalPlans,",", "法宝", "配置错误", "暴击组合序列无效：");
	}

	public int getMatID() {
		return matID;
	}

	public String getCriticalPlans() {
		return criticalPlans;
	}

	public int[] getPlans() {
		return plans;
	}

}
