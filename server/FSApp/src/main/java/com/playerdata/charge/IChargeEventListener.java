package com.playerdata.charge;

import com.playerdata.Player;
import com.playerdata.charge.cfg.ChargeCfg;

public interface IChargeEventListener {

	public void notifyCharge(Player player, ChargeCfg target, int preVipLv);
}
