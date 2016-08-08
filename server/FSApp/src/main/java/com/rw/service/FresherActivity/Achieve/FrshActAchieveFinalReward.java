package com.rw.service.FresherActivity.Achieve;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Player;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.template.BIActivityCode;
import com.rw.service.log.template.BILogTemplateHelper;
import com.rw.service.log.template.BilogItemInfo;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.fresherActivity.FresherActivityFinalRewardCfgDao;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityFinalRewardCfg;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItem;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemHolder;

public class FrshActAchieveFinalReward implements IFrshActAchieveRewardHandler {

	@Override
	public String achieveFresherActivityReward(Player player, int cfgId, FresherActivityItemHolder holder) {
		// TODO Auto-generated method stub
		List<FresherActivityItem> list = holder.getFresherActivityItemList();
		int finishCount = 0;
		int totalCount = 0;
		for (FresherActivityItem data : list) {
			if (data.isFinish()) {
				finishCount++;
			}
			
			// 不能算最终奖励的个数
			if (data.getType() != eActivityType.A_Final) {
				totalCount++;
			}
		}
		double result = (double) finishCount / totalCount * 100;
		int maxprogress = -1;
		String reward = "";
		holder.achieveFresherActivityReward(player, cfgId);
		List<FresherActivityFinalRewardCfg> allCfg = FresherActivityFinalRewardCfgDao.getInstance().getAllCfg();
		for (FresherActivityFinalRewardCfg fresherActivityFinalRewardCfg : allCfg) {

			if (fresherActivityFinalRewardCfg.getProgress() <= result) {
				if (fresherActivityFinalRewardCfg.getProgress() > maxprogress) {
					maxprogress = fresherActivityFinalRewardCfg.getProgress();
					reward = fresherActivityFinalRewardCfg.getReward();
				}
			}
		}

		// 发送奖励
		String[] split = reward.split(";");
		List<ItemInfo> itemList = new ArrayList<ItemInfo>(split.length);
		for (String value : split) {
			String[] split2 = value.split(":");
			if (split2.length < 2) {
				continue;
			}
//			player.getItemBagMgr().addItem(Integer.parseInt(split2[0]), Integer.parseInt(split2[1]));
			itemList.add(new ItemInfo(Integer.parseInt(split2[0]), Integer.parseInt(split2[1])));
		}
		player.getItemBagMgr().addItem(itemList);
		
		String rewardInfoActivity="";
		List<BilogItemInfo> rewardslist = BilogItemInfo.fromStrArr(split);
		rewardInfoActivity = BILogTemplateHelper.getString(rewardslist);		
		BILogMgr.getInstance().logActivityEnd(player, null, BIActivityCode.SEVER_BEGIN_ACTIVITY_ONE, 0, true, 0, rewardInfoActivity,cfgId);
		

		return null;
	}

}
