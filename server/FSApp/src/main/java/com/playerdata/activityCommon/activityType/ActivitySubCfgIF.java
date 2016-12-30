package com.playerdata.activityCommon.activityType;

public interface ActivitySubCfgIF {
	
	int getId();
	
	int getDay();
	
	/**
	 * 父类型id（主表的配置id）
	 * @return
	 */
	int getType();
	
	/**
	 * 该方法用在用gm修改配置表的时候
	 * @param reward
	 */
	void setCfgReward(String reward);
}
