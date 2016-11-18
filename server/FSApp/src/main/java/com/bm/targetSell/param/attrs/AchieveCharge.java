package com.bm.targetSell.param.attrs;

import java.util.Map;

import com.playerdata.Player;
import com.playerdata.charge.dao.ChargeInfo;
import com.playerdata.charge.dao.ChargeInfoHolder;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.user.User;

public class AchieveCharge extends AbsAchieveAttrValue{

	@Override
	public void achieveAttrValue(Player player, User user, BenefitAttrCfg cfg, Map<String, Object> AttrMap) {
		ChargeInfo charge = ChargeInfoHolder.getInstance().get(player.getUserId());
		AttrMap.put(cfg.getAttrName(), charge.getTotalChargeMoney());
	}

}
