package com.rwbase.dao.battletower.pojo.readonly;

/*
 * @author HC
 * @date 2015年9月1日 下午3:43:42
 * @Description 
 */
public interface TableBattleTowerIF {

	/**
	 * 获取历史最高层数
	 * 
	 * @return
	 */
	public int getHighestFloor();

	/**
	 * 获取当天最高层
	 * 
	 * @return
	 */
	public int getCurFloor();

	/**
	 * 获取当前重置次数
	 * 
	 * @return
	 */
	public int getResetTimes();

	/**
	 * 获取扫荡开始的时间
	 * 
	 * @return
	 */
	public long getSweepStartTime();

	/**
	 * 获取扫荡的状态&成功或者失败
	 * 
	 * @return
	 */
	public boolean getSweepState();

	/**
	 * 获取扫荡开始的层数
	 * 
	 * @return
	 */
	public int getSweepStartFloor();

	/**
	 * 获取当天产生Boss的次数
	 * 
	 * @return
	 */
	public int getCurBossTimes();
}