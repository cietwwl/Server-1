package com.playerdata.charge.eventlistener;

import com.playerdata.Player;
import com.playerdata.charge.IChargeEventListener;
import com.playerdata.charge.cfg.ChargeCfg;
import com.rwbase.common.userEvent.UserEventMgr;

public class UserEventMgrListenerOfCharge implements IChargeEventListener {

	@Override
	public void notifyCharge(Player player, ChargeCfg target, int preVipLv) {
		UserEventMgr.getInstance().charge(player, target.getMoneyYuan());
	}

}
