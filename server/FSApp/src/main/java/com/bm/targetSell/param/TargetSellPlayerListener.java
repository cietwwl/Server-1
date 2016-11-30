package com.bm.targetSell.param;

import com.bm.targetSell.TargetSellManager;
import com.playerdata.Player;
import com.rwbase.common.PlayerTaskListener;

public class TargetSellPlayerListener implements PlayerTaskListener {

	@Override
	public void notifyTaskCompleted(Player player) {
		TargetSellManager.getInstance().checkAndPackHeroChanged(player.getUserId(), false);
	}

}
