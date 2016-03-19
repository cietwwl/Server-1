package com.rwbase.common;

import com.playerdata.Player;

/**
 * <pre>
 * 当服务器数据发生变化会同步
 * 数据同步器同步到客户端
 * </pre>
 * @author Jamaz
 *
 */
public interface RecordSynchronization {

	/**
	 * <pre>
	 * 按版本同步数据
	 * </pre>
	 * @param player
	 * @param version
	 */
	public void synAllData(Player player, int version);
	
}
