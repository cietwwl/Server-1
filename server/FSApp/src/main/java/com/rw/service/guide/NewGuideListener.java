package com.rw.service.guide;

import com.playerdata.Player;
import com.rwbase.common.PlayerTaskListener;

public class NewGuideListener implements PlayerTaskListener{

	@Override
	public void notifyTaskCompleted(Player player) {
		NewGuideStateChecker.getInstance().check(player, false);
	}

}
