package com.rw.handler.groupCompetition.data.group;

import java.util.List;

/**
 * 
 * 帮派争霸里面，战斗单位的数据接口
 * 
 * @author CHEN.P
 *
 */
public interface IGCUnit {
	
	/**
	 * 
	 * 获取玩家的userId
	 * 
	 * @return
	 */
	public String getUserId();
	
	/**
	 * 
	 * 获取出战的英雄id列表
	 * 
	 * @return
	 */
	public List<String> getHeroIds();
	
	/**
	 * 
	 * 是否机器人
	 * 
	 * @return
	 */
	public boolean isRobot();
}
