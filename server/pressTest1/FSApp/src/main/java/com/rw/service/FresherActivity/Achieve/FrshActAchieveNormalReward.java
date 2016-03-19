package com.rw.service.FresherActivity.Achieve;

import com.playerdata.Player;
import com.rwbase.dao.fresherActivity.FresherActivityCfgDao;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityCfg;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItem;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemHolder;

public class FrshActAchieveNormalReward implements IFrshActAchieveRewardHandler {

	@Override
	public String achieveFresherActivityReward(Player player, int cfgId, FresherActivityItemHolder holder) {
		// TODO Auto-generated method stub

		FresherActivityItem item = holder.getFresherActivityItemsById(cfgId);
		FresherActivityCfg cfg = (FresherActivityCfg) FresherActivityCfgDao.getInstance().getCfgById(String.valueOf(cfgId));
		holder.achieveFresherActivityReward(player, cfgId);
		String reward = cfg.getReward();
		// 发送奖励
		String[] split = reward.split(";");
		for (String value : split) {
			String[] split2 = value.split(":");
			if (split2.length < 2) {
				continue;
			}
			player.getItemBagMgr().addItem(Integer.parseInt(split2[0]), Integer.parseInt(split2[1]));
		}
		return null;
	}

}
