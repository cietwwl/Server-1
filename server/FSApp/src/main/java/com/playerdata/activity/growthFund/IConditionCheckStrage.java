package com.playerdata.activity.growthFund;

import com.playerdata.Player;
import com.playerdata.activity.growthFund.cfg.GrowthFundRewardAbsCfg;
import com.rw.fsutil.common.IReadOnlyPair;

public interface IConditionCheckStrage {

	public IReadOnlyPair<Boolean, String> checkConditionReached(GrowthFundRewardAbsCfg cfg, Player player);
}
