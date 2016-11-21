package com.bm.targetSell.param.attrs;

import java.util.Map;

import com.bm.targetSell.param.TargetSellRoleChange;
import com.playerdata.Player;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.user.User;

public interface AbsAchieveAttrValue {
	
	/**
	 * 主角的modelID,因为主角可以转职，所以这里特殊值表示主角
	 */
	public static int MainRoleModelID = 1;
	
	/**
	 * 检查条件
	 * @param player
	 * @param user
	 * @param cfg  属性类型配置
	 * @param AttrMap
	 */
	public void achieveAttrValue(Player player, User user, BenefitAttrCfg cfg, Map<String, Object> AttrMap);

	
	/**
	 * 添加英雄改变的属性条件
	 * @param userID
	 * @param heroID
	 * @param achieveType
	 * @param value
	 */
	public void addHeroAttrs(String userID, String heroID, EAchieveType achieveType, TargetSellRoleChange value);
}
