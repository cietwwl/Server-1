package com.rw.service.Privilege.datamodel;

import com.rwproto.PrivilegeProtos.ArenaPrivilegeNames;

public class arenaPrivilege extends AbstractConfigChargeSource<ArenaPrivilegeNames> {
	private String source; // 特权来源
	@SuppressWarnings("unused")
	private int arenaMaxCount; // 可购买竞技场门票次数
	@SuppressWarnings("unused")
	private boolean isAllowResetArena; // 开启重置竞技场CD
	@SuppressWarnings("unused")
	private int arenaRewardAdd; // 竞技场结算奖励增加x（万分比）
	@SuppressWarnings("unused")
	private int arenaChallengeDec; // 竞技场挑战cd减少x秒

	@Override
	public String getSource() {
		return source;
	}

	@Override
	public void ExtraInitAfterLoad(IPrivilegeConfigSourcer<ArenaPrivilegeNames> cfgHelper) {
		Throwable cause = null;
		try {
			ExtraInitAfterLoad(ArenaPrivilegeNames.class, cfgHelper);
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