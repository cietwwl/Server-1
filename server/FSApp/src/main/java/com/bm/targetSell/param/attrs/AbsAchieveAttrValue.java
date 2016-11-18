package com.bm.targetSell.param.attrs;

import java.util.Map;

import com.playerdata.Player;
import com.playerdata.eRoleType;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.user.User;

public abstract class AbsAchieveAttrValue {
	
	
	/**
	 * 检查条件
	 * @param player
	 * @param user
	 * @param cfg  属性类型配置
	 * @param AttrMap
	 */
	public abstract void achieveAttrValue(Player player, User user, BenefitAttrCfg cfg, Map<String, Object> AttrMap);
}
