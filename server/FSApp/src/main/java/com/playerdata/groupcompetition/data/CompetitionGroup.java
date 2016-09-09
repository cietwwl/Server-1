package com.playerdata.groupcompetition.data;

import java.util.List;

/**
 * 
 * 参与帮派战的帮派数据接口
 * 
 * @author CHEN.P
 *
 */
public interface CompetitionGroup {

	/**
	 * 
	 * 获取帮派的id
	 * 
	 * @return
	 */
	public String getGroupId();
	
	/**
	 * 
	 * 获取帮派的名字
	 * 
	 * @return
	 */
	public String getGroupName();
	
	/**
	 * 
	 * 获取帮派的icon
	 * 
	 * @return
	 */
	public String getIcon();
	
	/**
	 * 
	 * 获取当前的积分
	 * 
	 * @return
	 */
	public int getCurrentScore();
	
	/**
	 * 
	 * 获取所有可用的战斗单位
	 * 
	 * @return
	 */
	public List<CompetitionUnit> getAllUnits();
}
