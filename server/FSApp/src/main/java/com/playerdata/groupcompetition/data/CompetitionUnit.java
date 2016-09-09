package com.playerdata.groupcompetition.data;

/**
 * 
 * 帮派争霸里面，战斗单位的数据接口
 * 
 * @author CHEN.P
 *
 */
public interface CompetitionUnit {

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
	 * 获取最后一个挑战单位的id
	 * 
	 * @return
	 */
	public String getIdOfLastCompetitor();
	
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
	
	/**
	 * 
	 * 获取最后一次匹配开始的时间
	 * 
	 * @return
	 */
	public long getMatchBeginTime();
	
	/**
	 * 
	 * 设置匹配时间
	 * 
	 * @param pTime
	 */
	public void setMatchBeginTime(long pTime);
}
