package com.rwbase.common.timer;

import java.util.List;

import com.playerdata.Player;

/**
 * 
 * <pre>
 * player收集器
 * </pre>
 * 
 * @author CHEN.P
 *
 */
public interface IPlayerGatherer {

	/**
	 * 
	 * 收集player
	 * 
	 * @return
	 */
	public List<Player> gatherPlayers();
}
