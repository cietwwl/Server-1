package com.bm.targetSell.param.attrs;

import java.util.Map;

import com.bm.targetSell.param.TargetSellRoleChange;
import com.playerdata.Player;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.user.User;

public class AchievePower implements AbsAchieveAttrValue{

	@Override
	public void achieveAttrValue(Player player, User user, BenefitAttrCfg cfg, Map<String, Object> AttrMap) {
		int power = player.getUserGameDataMgr().getPower();
		AttrMap.put(cfg.getAttrName(), power);
	}

	@Override
	public void addHeroAttrs(String userID, String heroID,
			EAchieveType change, TargetSellRoleChange value) {
		// TODO Auto-generated method stub
		
	}

}
