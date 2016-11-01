package com.playerdata.activity.growthFund;

import com.playerdata.Player;
import com.playerdata.activity.growthFund.cfg.GrowthFundRewardAbsCfg;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;

public class GrowthFundRewardConditionCheckStrage implements IConditionCheckStrage {

	@Override
	public IReadOnlyPair<Boolean, String> checkConditionReached(GrowthFundRewardAbsCfg cfg, Player player) {
		GrowthFundGlobalData globalData = ActivityGrowthFundMgr.getInstance().getGlobalData();
		
		boolean pass = true;
		String tips = null;
		if (globalData.getAlreadyBoughtCount() < cfg.getRequiredCondition()) {
			pass = false;
			tips = GrowthFundTips.getTipsAlreadyBoughtCountNotReached(cfg.getRequiredCondition());
		}
		return Pair.Create(pass, tips);
	}

}
