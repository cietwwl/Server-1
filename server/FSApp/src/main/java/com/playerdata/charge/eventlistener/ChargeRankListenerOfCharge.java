package com.playerdata.charge.eventlistener;

import com.playerdata.Player;
import com.playerdata.activity.chargeRank.ActivityChargeRankMgr;
import com.playerdata.charge.IChargeEventListener;
import com.playerdata.charge.cfg.ChargeCfg;

public class ChargeRankListenerOfCharge implements IChargeEventListener {

	@Override
	public void notifyCharge(Player player, ChargeCfg target, int preVipLv) {
		ActivityChargeRankMgr.getInstance().addFinishCount(player, target.getMoneyYuan());		
	}

}
