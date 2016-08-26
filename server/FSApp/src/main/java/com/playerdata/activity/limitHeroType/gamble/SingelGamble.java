package com.playerdata.activity.limitHeroType.gamble;

import java.util.HashMap;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.activity.limitHeroType.ActivityLimitHeroTypeMgr;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitGamblePlanCfg;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItemHolder;

public class SingelGamble implements Gamble{

	@Override
	public String gamble(Player player,
			ActivityLimitHeroTypeItemHolder holder,
			ActivityLimitGamblePlanCfg cfg, int guaranteeTimes) {
		Map<Integer, Integer> planList = cfg.getOrdinaryPlanMap();
		if(guaranteeTimes != 0){
			planList = cfg.getGuaranteePlanMap();
		}
		return ActivityLimitHeroTypeMgr.getInstance().getGambleRewards(planList);
	}



}
