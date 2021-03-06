package com.playerdata.groupcompetition.data.match;

/**
 * 
 * 匹配源
 * 
 * @author CHEN.P
 *
 */
public interface IGCMatchSource {

	/**
	 * 
	 * 获取上一次被匹配到的时间
	 * 
	 * @return
	 */
	public long getLastMatchTime();
	
	/**
	 * 
	 * 设置上一次被匹配到的时间
	 * 
	 * @param time
	 */
	public void setLastMatchTime(long time);
	
	/**
	 * 
	 * 获取总共匹配过的次数
	 * 
	 * @return
	 */
	public int getTotalMatchTimes();
	
	/**
	 * 
	 * 设置总共的匹配次数
	 * 
	 * @param pTimes
	 */
	public void setTotalMatchTimes(int pTimes);
	
	/**
	 * 
	 * 获取最后一个挑战单位的id
	 * 
	 * @return
	 */
	public String getIdOfLastCompetitor();
	
	/**
	 * 
	 * 设置最后一个挑战单位的id
	 * 
	 * @param competitorId
	 */
	public void setIdOfLastCompetitor(String competitorId);
}
