package com.bm.targetSell.param.attrs;

import java.util.Map;

import com.bm.targetSell.param.TargetSellRoleChange;
import com.playerdata.Player;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.user.User;

public class AchieveLastLoginTime implements AbsAchieveAttrValue{
	@Override
	public void achieveAttrValue(Player player, User user, BenefitAttrCfg cfg, Map<String, Object> AttrMap) {
		String loginTimeValue = DateUtils.getDateTimeFormatString(user.getLastLoginTime(), "yyyyMMdd");
		AttrMap.put(cfg.getAttrName(), loginTimeValue);
	}

	@Override
	public void addHeroAttrs(String userID, String heroID,
			EAchieveType change, TargetSellRoleChange value) {
		// TODO Auto-generated method stub
		
	}
}
