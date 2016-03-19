package com.bm.rank.group;

/*
 * @author HC
 * @date 2016年1月20日 上午10:32:21
 * @Description 帮派排行榜中简单的扩展信息
 */
public class GroupSimpleExtAttribute {
	private String groupId;// 帮派的Id
	private volatile long leaderLogoutTime;// 帮主登出游戏的时间

	/**
	 * 获取帮派的Id
	 * 
	 * @return
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * 获取帮主登出游戏的时间
	 * 
	 * @return
	 */
	public long getLeaderLogoutTime() {
		return leaderLogoutTime;
	}

	/**
	 * 设置帮派的Id
	 * 
	 * @param groupId
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * 设置帮主登出游戏的时间
	 * 
	 * @param leaderLogoutTime
	 */
	public void setLeaderLogoutTime(long leaderLogoutTime) {
		this.leaderLogoutTime = leaderLogoutTime;
	}
}