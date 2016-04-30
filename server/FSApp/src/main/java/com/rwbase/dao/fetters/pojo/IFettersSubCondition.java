package com.rwbase.dao.fetters.pojo;

import com.playerdata.Player;

/*
 * @author HC
 * @date 2016年4月27日 下午5:50:28
 * @Description 子条件
 */
public interface IFettersSubCondition {
	/**
	 * 检查子条件是否能达成
	 * 
	 * @param player
	 * @param checkId
	 * @param value
	 * @return
	 */
	public boolean match(Player player, int checkId, int value);

	/**
	 * 获取子条件的类型
	 * 
	 * @return
	 */
	public int getSubConditionType();
}