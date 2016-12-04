package com.playerdata.charge.action;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.charge.IChargeAction;
import com.playerdata.charge.cfg.ChargeCfg;
import com.playerdata.charge.data.ChargeParam;

public class EmptyChargeAction implements IChargeAction {

	@Override
	public boolean doCharge(Player player, ChargeCfg cfg, ChargeParam param) {
		GameLog.error("doCharge", player.getUserId(), "错误的充值回调，充值参数：" + cfg.getId());
		return false;
	}

}
