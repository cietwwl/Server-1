package com.rwbase.common.timer;

import com.playerdata.Player;

/**
 * 
 * <pre>
 * 玩家操作接口
 * 默认会对所有在缓存中的玩家进行操作
 * 如果需要对特定的玩家群体操作，则可以实现{@link IPlayerGatherer}
 * </pre>
 * 
 * @author CHEN.P
 *
 */
public interface IPlayerOperable {
	
	/**
	 * 
	 * <pre>
	 * 是否对此玩家感兴趣
	 * 如果是对全服玩家或者在线玩家操作的，可以直接返回true
	 * 其他业务自行判断。
	 * 此方法会在玩家上线的时候调用，如果感兴趣，则会调用{@link #operate(Player)}
	 * </pre>
	 * 
	 * @param player
	 * @return
	 */
	public boolean isInterestingOn(Player player);

	/**
	 * 
	 * 对player执行操作
	 * 
	 * @param player
	 */
	public void operate(Player player);
	
}
