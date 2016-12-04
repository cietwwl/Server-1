package com.playerdata.charge;

import com.playerdata.Player;
import com.playerdata.charge.cfg.ChargeCfg;
import com.playerdata.charge.data.ChargeParam;

public interface IChargeAction {

	public boolean doCharge(Player player, ChargeCfg cfg, ChargeParam param);
}
