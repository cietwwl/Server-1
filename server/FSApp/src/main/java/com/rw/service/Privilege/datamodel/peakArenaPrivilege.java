package com.rw.service.Privilege.datamodel;

import com.rwproto.PrivilegeProtos.PeakArenaPrivilegeNames;

public class peakArenaPrivilege extends AbstractConfigChargeSource<PeakArenaPrivilegeNames>{
	private String source; // 特权来源
	private int peakMaxCount; // 可购买巅峰竞技场门票次数
	private boolean isAllowResetPeak; // 开启重置巅峰竞技场CD
	
	@Override
	public String getSource() {
		return source;
	}

	public int getPeakMaxCount() {
		return peakMaxCount;
	}

	public boolean getIsAllowResetPeak() {
		return isAllowResetPeak;
	}

	@Override
	public void ExtraInitAfterLoad() {
		Throwable cause = null;
		try {
			ExtraInitAfterLoad(PeakArenaPrivilegeNames.class, peakArenaPrivilegeHelper.getInstance());
		} catch (IllegalArgumentException e) {
			cause = e;
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			cause = e;
			e.printStackTrace();
		}
		if (cause != null){
			throw new RuntimeException(cause);
		}
	}
}