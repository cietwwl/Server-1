package com.rw.service.Privilege.datamodel;

import com.rwproto.PrivilegeProtos.GeneralPrivilegeNames;

@SuppressWarnings("unused")
public class generalPrivilege extends AbstractConfigChargeSource<GeneralPrivilegeNames> {
  private String source; //特权来源
  private boolean isAllowVipHeadIcon; //VIP头像框
  private int battleSpeed; //战斗加速倍数
  private boolean isAllowBuyFashion; //开启时装购买
  private boolean isAllowSoulBox; //开启第三宝箱（魂匣）开启
  private boolean isAllowReplenish; //开启补签开启


	@Override
	public String getSource() {
		return source;
	}

	@Override
	public void ExtraInitAfterLoad(IPrivilegeConfigSourcer<GeneralPrivilegeNames> cfgHelper) {
		Throwable cause = null;
		try {
			ExtraInitAfterLoad(GeneralPrivilegeNames.class, cfgHelper);
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