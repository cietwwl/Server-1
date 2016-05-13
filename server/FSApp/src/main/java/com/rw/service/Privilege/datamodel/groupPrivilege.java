package com.rw.service.Privilege.datamodel;

import com.rwproto.PrivilegeProtos.GroupPrivilegeNames;

@SuppressWarnings("unused")
public class groupPrivilege extends AbstractConfigChargeSource<GroupPrivilegeNames> {
  private String source; //特权来源
  private int donateCount; //帮派可捐献次数
  private int mysteryChallengeCount; //帮派秘境挑战次数


	@Override
	public String getSource() {
		return source;
	}

	@Override
	public void ExtraInitAfterLoad(IPrivilegeConfigSourcer<GroupPrivilegeNames> cfgHelper) {
		Throwable cause = null;
		try {
			ExtraInitAfterLoad(GroupPrivilegeNames.class, cfgHelper);
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