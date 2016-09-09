package com.playerdata.groupcompetition.data;

/**
 * 
 * 帮派争霸中的战斗对垒
 * 
 * @author CHEN.P
 *
 */
public interface CompetitionAgainst {

	/**
	 * 
	 * 获取胜利的帮派
	 * 
	 * @return
	 */
	public CompetitionGroup getWinGroup();
	
	/**
	 * 
	 * 获取匹配器
	 * 
	 * @return
	 */
	public CompetitionMatcher getMatcher();
}
