package com.playerdata.activity.growthFund;

import com.playerdata.Player;
import com.playerdata.activity.growthFund.cfg.GrowthFundRewardAbsCfg;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;

public class GrowthFundGiftConditionCheckStrage implements IConditionCheckStrage {

	@Override
	public IReadOnlyPair<Boolean, String> checkConditionReached(GrowthFundRewardAbsCfg cfg, Player player) {
		boolean pass = true;
		String tips = null;
		if (player.getLevel() < cfg.getRequiredCondition()) {
			pass = false;
			tips = GrowthFundTips.getTipsLvNotReachToGet(player.getLevel());
		}
		return Pair.Create(pass, tips);
	}

}
