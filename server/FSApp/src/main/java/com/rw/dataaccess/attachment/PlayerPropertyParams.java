package com.rw.dataaccess.attachment;

public interface PlayerPropertyParams {

	/**
	 * 获取玩家ID
	 * @return
	 */
	public String getUserId();

	/**
	 * 获取等级
	 * @return
	 */
	public int getLevel();

	/**
	 * 获取创建时间
	 * @return
	 */
	public long getCreateTime();
	
	/**
	 * 获取当前时间
	 * @return
	 */
	public long getCurrentTime();
}
