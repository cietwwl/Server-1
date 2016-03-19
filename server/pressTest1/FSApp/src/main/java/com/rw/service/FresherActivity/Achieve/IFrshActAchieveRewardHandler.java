package com.rw.service.FresherActivity.Achieve;

import com.playerdata.Player;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemHolder;

public interface IFrshActAchieveRewardHandler {
	public String achieveFresherActivityReward(Player player, int cfgId, FresherActivityItemHolder holder);
}
