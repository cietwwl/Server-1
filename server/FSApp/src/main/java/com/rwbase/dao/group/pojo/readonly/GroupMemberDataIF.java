package com.rwbase.dao.group.pojo.readonly;

/*
 * @author HC
 * @date 2016年2月24日 上午9:39:36
 * @Description 帮派成员数据只读接口
 */
public interface GroupMemberDataIF {
	/**
	 * 对应的角色Id
	 *
	 * @return
	 */
	public String getUserId();

	/**
	 * 获取成员所属的帮派Id
	 * 
	 * @return
	 */
	public String getGroupId();

	/**
	 * 获取成员在帮派中的职位
	 * 
	 * @return
	 */
	public int getPost();

	/**
	 * 获取成员最近登出游戏的时间
	 * 
	 * @return
	 */
	public long getLogoutTime();

	/**
	 * 获取成员的名字
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * 获取成员的等级
	 * 
	 * @return
	 */
	public int getLevel();

	/**
	 * 获取成员的头像Id
	 * 
	 * @return
	 */
	public String getHeadId();

	/**
	 * 获取成员的Vip等级
	 * 
	 * @return
	 */
	public int getVipLevel();

	/**
	 * 获取成员的职业
	 * 
	 * @return
	 */
	public int getJob();

	/**
	 * 获取个人的贡献
	 *
	 * @return
	 */
	public int getContribution();

	// /**
	// * 获取贡献次数
	// *
	// * @return
	// */
	// public int getDonateTimes();

	/**
	 * 获取成员请求时的战斗力
	 * 
	 * @return
	 */
	public int getFighting();

	/**
	 * 获取成员的申请时间
	 * 
	 * @return
	 */
	public long getApplyTime();

	/**
	 * 获取成员被接收的时间
	 * 
	 * @return
	 */
	public long getReceiveTime();

	/**
	 * 获取成员的模版Id
	 * 
	 * @return
	 */
	public String getTemplateId();

	// /**
	// * 获取上次捐献的时间
	// *
	// * @return
	// */
	// public long getLastDonateTime();

	/**
	 * 获取帮派总贡献
	 * 
	 * @return
	 */
	public int getTotalContribution();

	/**
	 * 获取今天获取的贡献
	 * 
	 * @return
	 */
	public int getDayContribution();

	/**
	 * 获取帮派管理员帮派副本的当前手动分配次数
	 */
	public int getAllotRewardCount();

	/**
	 * 获取祈福的进度
	 * 
	 * @return
	 */
	public int getPrayProcess();

	/**
	 * 获取祈福的卡Id
	 * 
	 * @return
	 */
	public int getPrayCardId();

	/**
	 * 获取背包中当前的数量
	 * 
	 * @return
	 */
	public int getBagHasNum();
}