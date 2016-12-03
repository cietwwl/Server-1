package com.playerdata.charge.eventlistener;

import com.playerdata.Player;
import com.playerdata.activity.dailyCharge.ActivityDailyRechargeTypeMgr;
import com.playerdata.charge.IChargeEventListener;
import com.playerdata.charge.cfg.ChargeCfg;

public class ActivityDailyRechargeListenerOfCharge implements IChargeEventListener {

	@Override
	public void notifyCharge(Player player, ChargeCfg target, int preVipLv) {
		ActivityDailyRechargeTypeMgr.getInstance().addFinishCount(player, target.getMoneyYuan());		
	}
	
}
