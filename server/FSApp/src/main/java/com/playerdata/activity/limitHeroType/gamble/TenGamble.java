package com.playerdata.activity.limitHeroType.gamble;

import java.util.Map;

import com.playerdata.Player;
import com.playerdata.activity.limitHeroType.ActivityLimitHeroTypeMgr;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitGamblePlanCfg;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItemHolder;

public class TenGamble implements Gamble{

	@Override
	public String gamble(Player player,
			ActivityLimitHeroTypeItemHolder holder,
			ActivityLimitGamblePlanCfg cfg, int guaranteeTimes) {
		int gambleTime = cfg.getDropItemCount();
		Map<Integer, Integer> planList = cfg.getOrdinaryPlanMap();
		Map<Integer, Integer> guaranteePlanList = cfg.getGuaranteePlanMap();
		StringBuilder rewardsMap = new StringBuilder();
		ActivityLimitHeroTypeMgr activityLimitHeroTypeMgr = ActivityLimitHeroTypeMgr.getInstance();
		for(int i = 0;i < gambleTime;i++){
			String str = "";
			if(i < guaranteeTimes){
				str = activityLimitHeroTypeMgr.getGambleRewards(player,guaranteePlanList);	
			}else{
				str = activityLimitHeroTypeMgr.getGambleRewards(player,planList);	
			}
			
			if(i == gambleTime - 1){
				rewardsMap.append(str);
			}else{
				rewardsMap.append(str).append(",");
			}
		}		
		return rewardsMap.toString();
	}



}
