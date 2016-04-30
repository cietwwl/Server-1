package com.rw.service.Privilege.datamodel;

import com.common.BaseConfig;

public class peakArenaPrivilegeProperties extends BaseConfig {
	private com.rwproto.PrivilegeProtos.PeakArenaPrivilegeNames nameField; // 关键字段:特权控制点
	private int threshold; // 上限
	private String buyTip; // 特权提升提示模板
	private String nomoreTip; // 特权到达上限提示模板

	public com.rwproto.PrivilegeProtos.PeakArenaPrivilegeNames getName() {
		return nameField;
	}

	public int getThreshold() {
		return threshold;
	}

	public String getBuyTip() {
		return buyTip;
	}

	public String getNomoreTip() {
		return nomoreTip;
	}
}