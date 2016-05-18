package com.rw.service.Privilege.datamodel;

import com.rwproto.PrivilegeProtos.LoginPrivilegeNames;

@SuppressWarnings("unused")
public class loginPrivilege extends AbstractConfigChargeSource<LoginPrivilegeNames> {
  private String source; //特权来源
  private int buyPowerCount; //可购买体力次数
  private int useCoinTransCount; //可使用点金手次数
  private int getSweepTicketNum; //每日领取扫荡卷张数


	@Override
	public String getSource() {
		return source;
	}

	@Override
	public void ExtraInitAfterLoad(IPrivilegeConfigSourcer<LoginPrivilegeNames> cfgHelper) {
		Throwable cause = null;
		try {
			ExtraInitAfterLoad(LoginPrivilegeNames.class, cfgHelper);
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