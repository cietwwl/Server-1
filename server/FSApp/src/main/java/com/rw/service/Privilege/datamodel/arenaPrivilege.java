package com.rw.service.Privilege.datamodel;

import com.rwproto.PrivilegeProtos.ArenaPrivilegeNames;

public class arenaPrivilege  extends AbstractConfigChargeSource<ArenaPrivilegeNames>{
	private String source; // 特权来源
	private int arenaMaxCount; // 可购买竞技场门票次数
	private boolean isAllowResetArena; // 开启重置竞技场CD
	private int arenaRewardAdd; // 竞技场结算奖励增加x（万分比）
	private int arenaChallengeDec; // 竞技场挑战cd减少x秒

	@Override
	public String getSource() {
		return source;
	}

	public int getArenaMaxCount() {
		return arenaMaxCount;
	}

	public boolean getIsAllowResetArena() {
		return isAllowResetArena;
	}

	public int getArenaRewardAdd() {
		return arenaRewardAdd;
	}

	public int getArenaChallengeDec() {
		return arenaChallengeDec;
	}

	@Override
	public void ExtraInitAfterLoad() {
		Throwable cause = null;
		try {
			ExtraInitAfterLoad(ArenaPrivilegeNames.class, arenaPrivilegeHelper.getInstance());
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