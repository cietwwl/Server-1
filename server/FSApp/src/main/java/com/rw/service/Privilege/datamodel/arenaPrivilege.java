package com.rw.service.Privilege.datamodel;

import java.util.HashMap;

import com.common.BaseConfig;
import com.rwproto.PrivilegeProtos.ArenaPrivilegeNames;

public class arenaPrivilege extends BaseConfig implements IConfigChargeSource<ArenaPrivilegeNames>{
	private String source; // 特权来源
	private int arenaMaxCount; // 可购买竞技场门票次数
	private boolean isAllowResetArena; // 开启重置竞技场CD
	private int arenaRewardAdd; // 竞技场结算奖励增加x（万分比）
	private int arenaChallengeDec; // 竞技场挑战cd减少x秒

	private HashMap<ArenaPrivilegeNames,Object> fieldValues;
	
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
		fieldValues.put(ArenaPrivilegeNames.arenaMaxCount, arenaMaxCount);
		fieldValues.put(ArenaPrivilegeNames.isAllowResetArena, isAllowResetArena);
		fieldValues.put(ArenaPrivilegeNames.arenaRewardAdd, arenaRewardAdd);
		fieldValues.put(ArenaPrivilegeNames.arenaChallengeDec, arenaChallengeDec);
	}

	@Override
	public Object getValueByName(ArenaPrivilegeNames pname) {
		return fieldValues.get(pname);
	}
}