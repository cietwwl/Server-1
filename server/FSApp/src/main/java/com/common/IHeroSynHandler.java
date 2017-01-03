package com.common;

import com.playerdata.Player;
import com.rwproto.BattleCommon.eBattlePositionType;

public interface IHeroSynHandler {

	/**
	 * 
	 * 同步英雄数据
	 * 
	 * @param player
	 */
	public void synHeroData(Player player, eBattlePositionType posKey, String key);
}
