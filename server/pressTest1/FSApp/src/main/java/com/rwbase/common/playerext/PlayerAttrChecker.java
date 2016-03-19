package com.rwbase.common.playerext;

import com.playerdata.Player;
import com.playerdata.RankingMgr;
import com.rwbase.common.PlayerTaskListener;

public class PlayerAttrChecker implements PlayerTaskListener {

	@Override
	public void notifyTaskCompleted(Player player) {
		RankingMgr rankingMgr = RankingMgr.getInstance();
		PlayerTempAttribute tempAttr = player.getTempAttribute();
		if (tempAttr.checkAndResetExpChanged() || tempAttr.checkAndResetFightingChanged()) {
			rankingMgr.onLevelOrExpChanged(player);
		}
		if (tempAttr.checkAndResetFightingChanged()) {
			rankingMgr.onHeroFightingChanged(player);
		}
	}

}
