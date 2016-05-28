package com.bm.player;

import com.playerdata.Player;

/*
 * @author HC
 * @date 2016年2月1日 下午3:28:09
 * @Description 
 */
public interface Observer {

	/**
	 * 角色修改了名字
	 * 
	 * @param p
	 */
	public void playerChangeName(Player p);

	/**
	 * 角色修改了等级
	 * 
	 * @param p
	 */
	public void playerChangeLevel(Player p);

	/**
	 * 角色修改了Vip等级
	 * 
	 * @param p
	 */
	public void playerChangeVipLevel(Player p);

	/**
	 * 角色修改了模版Id
	 * 
	 * @param p
	 */
	public void playerChangeTemplateId(Player p);

	/**
	 * 角色修改了头像图标
	 * 
	 * @param p
	 */
	public void playerChangeHeadIcon(Player p);

	/**
	 * 获取观察者的类型
	 */
	public int getObserverType();
	
	/**
	 * 角色修改了头像框
	 * @param p
	 */
	public void playerChangeHeadBox(Player p);
}