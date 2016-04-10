package com.rwbase.common.playerext;

import com.playerdata.Player;
import com.playerdata.RankingMgr;
import com.rwbase.common.PlayerTaskListener;

public class PlayerAttrChecker implements PlayerTaskListener {

	@Override
	public void notifyTaskCompleted(Player player) {
		RankingMgr rankingMgr = RankingMgr.getInstance();
		PlayerTempAttribute tempAttr = player.getTempAttribute();
		boolean levelChanged = tempAttr.checkAndResetLevelChanged();
		boolean expChanged = tempAttr.checkAndResetExpChanged();
		boolean fightingChanged = tempAttr.checkAndResetFightingChanged();
		if (levelChanged || expChanged || fightingChanged) {
			rankingMgr.onLevelOrExpChanged(player);
		}
		if (fightingChanged) {
			rankingMgr.onHeroFightingChanged(player);
		}
	}

}
