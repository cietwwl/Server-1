package com.rwbase.dao.fetters.pojo;

import java.util.List;

import com.playerdata.Player;
import com.rwbase.dao.fetters.pojo.cfg.template.FettersSubConditionTemplate;

/*
 * @author HC
 * @date 2016年4月27日 上午10:45:20
 * @Description 强制限定
 */
public interface IFettersSubRestrictCondition {
	/**
	 * 检查条件是否达成
	 * 
	 * @param player
	 * @param fettersHeroIdList
	 * @param forceUseHeroIdList
	 * @param subConditionId
	 * @return
	 */
	public boolean match(Player player, List<Integer> fettersHeroIdList, List<Integer> forceUseHeroIdList, FettersSubConditionTemplate fettersSubCondition);

	/**
	 * 获取子条件限定类型
	 * 
	 * @return
	 */
	public int getSubRestrictConditionType();
}