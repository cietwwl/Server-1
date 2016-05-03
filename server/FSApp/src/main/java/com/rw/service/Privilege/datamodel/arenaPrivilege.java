package com.rw.service.Privilege.datamodel;

import com.common.BaseConfig;

public class arenaPrivilege extends BaseConfig implements IFieldReflector{
	private String source; // 特权来源
	private int arenaMaxCount; // 可购买竞技场门票次数
	private boolean isAllowResetArena; // 开启重置竞技场CD
	private int arenaRewardAdd; // 竞技场结算奖励增加x（万分比）
	private int arenaChallengeDec; // 竞技场挑战cd减少x秒

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
	public Object getValueByName(String pname) {
		// TODO Auto-generated method stub
		return null;
	}
}