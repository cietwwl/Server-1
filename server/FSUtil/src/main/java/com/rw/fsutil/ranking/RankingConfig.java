package com.rw.fsutil.ranking;

/**
 * 排行榜配置
 * @author Jamaz
 *
 */
public interface RankingConfig {

	/**
	 * 获取排行榜的最大容量
	 * @return
	 */
	public int getMaxCapacity();
	
	/**
	 * 获取排行榜名字
	 * @return
	 */
	public String getName();
	
	/**
	 * 获取排行榜类型
	 * @return
	 */
	public int getType();
	
	/**
	 * 获取排行榜的扩展对象
	 * @return
	 */
	public Class<? extends RankingExtension> getRankingExtension();
	
	/**
	 * 获取排行榜更新周期的分钟数(当数据发生变化时，多少分钟内同步到数据库一次)
	 * @return
	 */
	public int getUpdatePeriodMinutes();
}
