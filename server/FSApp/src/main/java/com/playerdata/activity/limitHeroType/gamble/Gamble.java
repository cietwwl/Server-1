package com.playerdata.activity.limitHeroType.gamble;

import com.playerdata.Player;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitGamblePlanCfg;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItemHolder;

/**
 * 
 * @author Administrator  由type进入实例抽卡；由次数激活保底抽卡；
 *
 */
public interface Gamble {
	public String gamble(Player player ,ActivityLimitHeroTypeItemHolder holder, ActivityLimitGamblePlanCfg cfg,int guaranteeTimes);
}
