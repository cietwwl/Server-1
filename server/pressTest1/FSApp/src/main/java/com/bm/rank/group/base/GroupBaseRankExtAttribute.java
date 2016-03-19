package com.bm.rank.group.base;

/*
 * @author HC
 * @date 2016年1月19日 下午8:42:09
 * @Description 帮派基础排行榜比较条件
 */
public class GroupBaseRankExtAttribute {
	private String groupId;// 帮派的Id
	private volatile String groupName;// 帮派名字
	private volatile String groupIcon;// 帮派的图标
	private volatile int groupLevel;// 帮派等级
	private volatile int groupExp;// 帮派的经验
	private volatile int groupSupplies;// 帮派物资
	private volatile int groupMemberNum;// 帮派成员数量
	private volatile long leaderLogoutTime;// 帮主离线的时间

	// //////////////////////////////////////////////GET区域

	/**
	 * 获取帮派Id
	 * 
	 * @return
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * 获取帮派的名字
	 * 
	 * @return
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * 获取帮派的图标Id
	 * 
	 * @return
	 */
	public String getGroupIcon() {
		return groupIcon;
	}

	/**
	 * 获取帮派的等级
	 * 
	 * @return
	 */
	public int getGroupLevel() {
		return groupLevel;
	}

	/**
	 * 获取帮派的经验
	 * 
	 * @return
	 */
	public int getGroupExp() {
		return groupExp;
	}

	/**
	 * 获取帮派物资
	 * 
	 * @return
	 */
	public int getGroupSupplies() {
		return groupSupplies;
	}

	/**
	 * 获取帮派成员数量
	 * 
	 * @return
	 */
	public int getGroupMemberNum() {
		return groupMemberNum;
	}

	/**
	 * 获取帮主离线时间
	 * 
	 * @return
	 */
	public long getLeaderLogoutTime() {
		return leaderLogoutTime;
	}

	// //////////////////////////////////////////////SET区域
	/**
	 * 设置帮派Id
	 * 
	 * @param groupId
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * 设置帮派名字
	 * 
	 * @param groupName
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * 设置帮派图标Id
	 * 
	 * @param groupIcon
	 */
	public void setGroupIcon(String groupIcon) {
		this.groupIcon = groupIcon;
	}

	/**
	 * 设置帮派等级
	 * 
	 * @param groupLevel
	 */
	public void setGroupLevel(int groupLevel) {
		this.groupLevel = groupLevel;
	}

	/**
	 * 设置帮派物资
	 * 
	 * @param groupSupplies
	 */
	public void setGroupSupplies(int groupSupplies) {
		this.groupSupplies = groupSupplies;
	}

	/**
	 * 设置帮派成员数量
	 * 
	 * @param groupMemberNum
	 */
	public void setGroupMemberNum(int groupMemberNum) {
		this.groupMemberNum = groupMemberNum;
	}

	/**
	 * 设置帮派的经验值
	 * 
	 * @param groupExp
	 */
	public void setGroupExp(int groupExp) {
		this.groupExp = groupExp;
	}

	/**
	 * 设置帮主的离线时间
	 * 
	 * @param leaderLogoutTime
	 */
	public void setLeaderLogoutTime(long leaderLogoutTime) {
		this.leaderLogoutTime = leaderLogoutTime;
	}
}