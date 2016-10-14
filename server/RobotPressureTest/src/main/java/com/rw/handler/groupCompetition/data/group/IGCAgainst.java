package com.rw.handler.groupCompetition.data.group;

import com.rw.handler.groupCompetition.util.GCompEventsStatus;

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
	public void setCurrentStatus(GCompEventsStatus currentStatus);
	
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
}
