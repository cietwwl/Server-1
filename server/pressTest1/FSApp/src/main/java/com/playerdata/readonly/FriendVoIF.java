package com.playerdata.readonly;


public interface FriendVoIF {
	/**领取体力数*/
	public int getReceivePower();
		
	/**
	 * 今日还可领取次数 
	 * @param level 角色等级
	 * @return
	 */
	public int getSurplusCount(int level);
	
	/**
	 * 是否达到了领取上限
	 * @param level 角色等级
	 * @return
	 */
	public boolean isCanReceive(int level);
}
