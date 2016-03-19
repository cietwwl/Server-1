package com.playerdata.readonly;

/*
 * 副本关卡记录接口
 * @author Luther
 */

public interface CopyLevelRecordIF {
	
	/*
	 * 获取用户ID
	 */
	public String getUserId();

	/*
	 * 获取通关星级数
	 */
	public int getPassStar();

	/*
	 * 获取当前关卡战斗了多少次
	 */
	public int getCurrentCount();

	/*
	 * 获取今日购买挑战次数
	 */
	public int getBuyCount();

	/*
	 * 获取关卡ID
	 */
	public int getLevelId();
	
	
	public boolean isFirst();
}
