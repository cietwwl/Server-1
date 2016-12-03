package com.playerdata.charge.eventlistener;

import com.playerdata.Player;
import com.playerdata.activity.evilBaoArrive.EvilBaoArriveMgr;
import com.playerdata.charge.IChargeEventListener;
import com.playerdata.charge.cfg.ChargeCfg;

public class EvilBaoArriveListenerOfCharge implements IChargeEventListener {

	@Override
	public void notifyCharge(Player player, ChargeCfg target, int preVipLv) {
		EvilBaoArriveMgr.getInstance().addFinishCount(player, target.getMoneyYuan());		
	}

}
