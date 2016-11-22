package com.bm.targetSell.param.attrs;

import java.util.Map;

import com.bm.targetSell.param.TargetSellRoleChange;
import com.playerdata.Player;
import com.playerdata.charge.dao.ChargeInfo;
import com.playerdata.charge.dao.ChargeInfoHolder;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.user.User;

public class AchieveCharge implements AbsAchieveAttrValue{

	@Override
	public void achieveAttrValue(Player player, User user, BenefitAttrCfg cfg, Map<String, Object> AttrMap) {
		ChargeInfo charge = ChargeInfoHolder.getInstance().get(player.getUserId());
		AttrMap.put(cfg.getAttrName(), charge.getTotalChargeMoney());
	}

	@Override
	public void addHeroAttrs(String userID, String heroID,
			EAchieveType change, TargetSellRoleChange value) {
		// TODO Auto-generated method stub
		
	}

}
