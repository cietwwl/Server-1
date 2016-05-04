package com.rw.service.Privilege.datamodel;

import com.rwproto.PrivilegeProtos.PeakArenaPrivilegeNames;

public class peakArenaPrivilege extends AbstractConfigChargeSource<PeakArenaPrivilegeNames> {
	private String source; // 特权来源
	@SuppressWarnings("unused")
	private int peakMaxCount; // 可购买巅峰竞技场门票次数
	@SuppressWarnings("unused")
	private boolean isAllowResetPeak; // 开启重置巅峰竞技场CD

	@Override
	public String getSource() {
		return source;
	}

	@Override
	public void ExtraInitAfterLoad(IPrivilegeConfigSourcer<PeakArenaPrivilegeNames> cfgHelper) {
		Throwable cause = null;
		try {
			ExtraInitAfterLoad(PeakArenaPrivilegeNames.class, cfgHelper);
		} catch (IllegalArgumentException e) {
			cause = e;
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			cause = e;
			e.printStackTrace();
		}
		if (cause != null) {
			throw new RuntimeException(cause);
		}
	}
}