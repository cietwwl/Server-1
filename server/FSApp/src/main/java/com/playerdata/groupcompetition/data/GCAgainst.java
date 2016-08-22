package com.playerdata.groupcompetition.data;

import java.util.List;

import com.playerdata.groupcompetition.data.match.GCMatcher;

/**
 * 
 * 帮派争霸中的战斗对垒
 * 
 * @author CHEN.P
 *
 */
public interface GCAgainst {
	
	/**
	 * 
	 * 检查公会是否在这场对垒里面
	 * 
	 * @param groupId
	 * @return
	 */
	public boolean isGroupInThisAgainst(String groupId);

	/**
	 * 
	 * 获取胜利的帮派
	 * 
	 * @return
	 */
	public GCGroup getWinGroup();
	
	/**
	 * 
	 * 获取交战记录
	 * 
	 * @return
	 */
	public List<GCCombatRecord> getHistorys();
	
	/**
	 * 
	 * 获取匹配器
	 * 
	 * @return
	 */
	public GCMatcher<IGCUnit> getMatcher();
}
