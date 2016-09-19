package com.playerdata.groupcompetition.data;

import java.util.List;

import com.playerdata.groupcompetition.data.match.IGCMatcher;
import com.playerdata.groupcompetition.util.GCEventsStatus;

/**
 * 
 * 帮派争霸中的战斗对垒
 * 
 * @author CHEN.P
 *
 */
public interface IGCAgainst {
	
	/**
	 * 
	 * 获取对垒的比赛id
	 * 
	 * @return
	 */
	public int getId();
	
	/**
	 * 
	 * @param currentStatus
	 */
	public void setCurrentStatus(GCEventsStatus currentStatus);
	
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
	 * 获取对阵的A帮派
	 * 
	 * @return
	 */
	public IGCGroup getGroupA();
	
	/**
	 * 
	 * 获取对阵的B帮派
	 * 
	 * @return
	 */
	public IGCGroup getGroupB();

	/**
	 * 
	 * 获取胜利的帮派
	 * 
	 * @return
	 */
	public IGCGroup getWinGroup();
	
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
	public IGCMatcher<IGCUnit> getMatcher();
}
