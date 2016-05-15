package com.rw.service.Privilege.datamodel;

import com.rwproto.PrivilegeProtos.HeroPrivilegeNames;

@SuppressWarnings("unused")
public class heroPrivilege extends AbstractConfigChargeSource<HeroPrivilegeNames> {
  private String source; //特权来源
  private int skillThreshold; //技能点上限
  private boolean isAllowBuySkillPoint; //开启购买技能点
  private int skillTimeDec; //技能点回复减少x秒
  private boolean isAllowAttach; //开启一键附灵


	@Override
	public String getSource() {
		return source;
	}

	@Override
	public void ExtraInitAfterLoad(IPrivilegeConfigSourcer<HeroPrivilegeNames> cfgHelper) {
		Throwable cause = null;
		try {
			ExtraInitAfterLoad(HeroPrivilegeNames.class, cfgHelper);
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