package com.rwbase.dao.battletower.pojo.readonly;

import java.util.List;

/*
 * @author HC
 * @date 2015年9月3日 下午5:58:28
 * @Description 试练塔角色信息接口
 */
public interface BattleTowerRoleInfoIF {

	/**
	 * 获取角色Id
	 * 
	 * @return
	 */
	public String getUserId();

	/**
	 * 获取角色的等级
	 * 
	 * @return
	 */
	public int getLevel();

	/**
	 * 获取头像Id
	 * 
	 * @return
	 */
	public String getHeadIcon();

	/**
	 * 获取名字
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * 获取历史最高层数
	 * 
	 * @return
	 */
	public int getFloor();

	/**
	 * 资源法宝的图标Id
	 * 
	 * @return
	 */
	public String getMagicIcon();

	/**
	 * 获取参与试练塔阵容的佣兵的快照
	 * 
	 * @return
	 */
	public List<? extends BattleTowerHeroInfoIF> getHeroInfoList();
}