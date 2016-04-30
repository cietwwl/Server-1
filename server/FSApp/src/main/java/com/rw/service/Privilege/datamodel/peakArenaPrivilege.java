package com.rw.service.Privilege.datamodel;

import com.common.BaseConfig;

public class peakArenaPrivilege extends BaseConfig {
	private int key; // 关键字段
	private String source; // 特权来源
	private int peakMaxCount; // 可购买巅峰竞技场门票次数
	private boolean isAllowResetPeak; // 开启重置巅峰竞技场CD

	public int getKey() {
		return key;
	}

	public String getSource() {
		return source;
	}

	public int getPeakMaxCount() {
		return peakMaxCount;
	}

	public boolean getIsAllowResetPeak() {
		return isAllowResetPeak;
	}
}