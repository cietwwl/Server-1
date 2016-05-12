package com.rw.service.Privilege.datamodel;

import com.rwproto.PrivilegeProtos.CopyPrivilegeNames;

@SuppressWarnings("unused")
public class copyPrivilege extends AbstractConfigChargeSource<CopyPrivilegeNames> {
  private String source; //特权来源
  private int copyRewardAdd; //普通副本金币掉落提高x%（万分比）
  private boolean isAllowTenSweep; //开启一键扫十次
  private int copyResetCnt; //普通副本可重置次数
  private int eliteRewardAdd; //精英副本金币收益增加x%（万分比）
  private int eliteResetCnt; //精英副本可重置次数


	@Override
	public String getSource() {
		return source;
	}

	@Override
	public void ExtraInitAfterLoad(IPrivilegeConfigSourcer<CopyPrivilegeNames> cfgHelper) {
		Throwable cause = null;
		try {
			ExtraInitAfterLoad(CopyPrivilegeNames.class, cfgHelper);
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