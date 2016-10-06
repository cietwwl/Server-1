package com.rw.dataaccess.attachment;

public class PlayerPropertyParams {

	private final String userId;
	private final int level;
	private final long createTime;
	private final long currentTime;

	public PlayerPropertyParams(String userId, int level, long createTime, long currentTime) {
		super();
		this.userId = userId;
		this.level = level;
		this.createTime = createTime;
		this.currentTime = currentTime;
	}

	/**
	 * 获取玩家ID
	 * 
	 * @return
	 */
	public String getUserId() {
		return this.userId;
	}

	/**
	 * 获取等级
	 * 
	 * @return
	 */
	public int getLevel() {
		return this.level;
	}

	/**
	 * 获取创建时间
	 * 
	 * @return
	 */
	public long getCreateTime() {
		return this.createTime;
	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public long getCurrentTime() {
		return this.currentTime;
	}
}
