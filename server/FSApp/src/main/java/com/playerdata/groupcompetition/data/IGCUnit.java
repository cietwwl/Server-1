package com.playerdata.groupcompetition.data;

import com.playerdata.groupcompetition.data.match.IGCMatchSource;

/**
 * 
 * 帮派争霸里面，战斗单位的数据接口
 * 
 * @author CHEN.P
 *
 */
public interface IGCUnit extends IGCMatchSource {

	/**
	 * 
	 * 战斗单位的唯一id
	 * 
	 * @return
	 */
	public String getId();
	
	/**
	 * 
	 * 获取战斗单位的等级
	 * 
	 * @return
	 */
	public int getLevel();
	
	/**
	 * 
	 * 获取总共胜利的次数
	 * 
	 * @return
	 */
	public int getTotalWinTimes();
	
	/**
	 * 
	 * 获取最高的连胜次数
	 * 
	 * @return
	 */
	public int getHighestContinuousWinTimes();
	
	/**
	 * 
	 * 获取当前连胜的次数
	 * 
	 * @return
	 */
	public int getCurrentContinousWinTimes();
	
	/**
	 * 
	 * 获取当前的积分
	 * 
	 * @return
	 */
	public int getCurrentScore();
	
	/**
	 * 
	 * 获取当前添加到帮派的积分
	 * 
	 * @return
	 */
	public int getCurrentScoreForGroup();
}
