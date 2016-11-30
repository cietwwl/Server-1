package com.playerdata.activityCommon.activityType;

public interface ActivitySubCfgIF {
	
	int getId();
	
	String getDay();
	
	/**
	 * 父类型id（主表的配置id）
	 * @return
	 */
	int getType();
	
}
