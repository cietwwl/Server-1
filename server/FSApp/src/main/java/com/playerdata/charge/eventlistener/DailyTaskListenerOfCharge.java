package com.playerdata.charge.eventlistener;

import com.playerdata.Player;
import com.playerdata.charge.IChargeEventListener;
import com.playerdata.charge.cfg.ChargeCfg;
import com.rwbase.common.enu.eTaskFinishDef;

public class DailyTaskListenerOfCharge implements IChargeEventListener {

	@Override
	public void notifyCharge(Player player, ChargeCfg target, int preVipLv) {
		player.getTaskMgr().AddTaskTimes(eTaskFinishDef.Recharge);
	}

}
