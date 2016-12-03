package com.playerdata.charge.eventlistener;

import com.playerdata.Player;
import com.playerdata.charge.IChargeEventListener;
import com.playerdata.charge.cfg.ChargeCfg;
import com.playerdata.charge.dao.ChargeInfo;
import com.playerdata.charge.dao.ChargeInfoHolder;

public class ChargeInfoUpdateListenerOfCharge implements IChargeEventListener {

	@Override
	public void notifyCharge(Player player, ChargeCfg target, int preVipLv) {
		ChargeInfo chargeInfo = ChargeInfoHolder.getInstance().get(player.getUserId());
		if (!chargeInfo.isContainsId(target.getId())) {
			chargeInfo.addChargeCfgId(target.getId());
			ChargeInfoHolder.getInstance().update(player);
		}		
	}

}
