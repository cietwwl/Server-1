package com.bm.targetSell.param.attrs;

import java.util.Map;

import com.bm.targetSell.param.ERoleAttrs;
import com.playerdata.Player;
import com.playerdata.eRoleType;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.targetSell.BenefitAttrCfgDAO;
import com.rwbase.dao.user.User;

public class AchieveCoin extends AbsAchieveAttrValue{

	@Override
	public void achieveAttrValue(Player player, User user, ERoleAttrs roleType, Object param, Map<String, Object> AttrMap, BenefitAttrCfgDAO benefitAttrCfgDAO) {
		long coin = player.getUserGameDataMgr().getCoin();
		BenefitAttrCfg cfg = benefitAttrCfgDAO.getCfgById(roleType.getIdStr());
		AttrMap.put(cfg.getAttrName(), coin);
	}

}
