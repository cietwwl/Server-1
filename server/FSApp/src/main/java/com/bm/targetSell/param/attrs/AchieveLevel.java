package com.bm.targetSell.param.attrs;

import java.util.Map;

import com.playerdata.Player;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.user.User;

public class AchieveLevel extends AbsAchieveAttrValue{

	@Override
	public void achieveAttrValue(Player player, User user, BenefitAttrCfg cfg, Map<String, Object> AttrMap) {
		int level = player.getLevel();
		AttrMap.put(cfg.getAttrName(), level);
	}

}
