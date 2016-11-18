package com.bm.targetSell.param.attrs;

import java.util.Map;

import com.playerdata.Player;
import com.playerdata.eRoleType;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.user.User;

public class AchieveAllPower extends AbsAchieveAttrValue{

	@Override
	public void achieveAttrValue(Player player, User user, BenefitAttrCfg cfg, Map<String, Object> AttrMap) {
		int fightingAll = player.getHeroMgr().getFightingAll(player);
		
		AttrMap.put(cfg.getAttrName(), fightingAll);
		
	}

}
