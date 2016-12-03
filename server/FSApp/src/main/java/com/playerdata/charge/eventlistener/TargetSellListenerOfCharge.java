package com.playerdata.charge.eventlistener;

import com.bm.targetSell.TargetSellManager;
import com.playerdata.Player;
import com.playerdata.charge.IChargeEventListener;
import com.playerdata.charge.cfg.ChargeCfg;

public class TargetSellListenerOfCharge implements IChargeEventListener {

	@Override
	public void notifyCharge(Player player, ChargeCfg target, int preVipLv) {
		TargetSellManager.getInstance().playerCharge(player, target.getMoneyYuan());		
	}

}
