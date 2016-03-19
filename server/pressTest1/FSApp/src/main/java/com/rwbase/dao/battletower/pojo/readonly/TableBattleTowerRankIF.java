package com.rwbase.dao.battletower.pojo.readonly;

/*
 * @author HC
 * @date 2015年9月3日 下午6:13:25
 * @Description 
 */
public interface TableBattleTowerRankIF {
	/**
	 * 获取角色Id
	 * 
	 * @return
	 */
	public String getUserId();

	/**
	 * 获取试练塔排行中的数据
	 * 
	 * @return
	 */
	public BattleTowerRoleInfoIF getRoleInfo();
}