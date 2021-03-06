package com.rw.service.FresherActivity.Achieve;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.template.BIActivityCode;
import com.rw.service.log.template.BILogTemplateHelper;
import com.rw.service.log.template.BilogItemInfo;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.fresherActivity.FresherActivityCfgDao;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityCfg;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItem;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemHolder;

public class FrshActAchieveNormalReward implements IFrshActAchieveRewardHandler {

	@Override
	public String achieveFresherActivityReward(Player player, int cfgId, FresherActivityItemHolder holder) {
		// TODO Auto-generated method stub

		// FresherActivityItem item = holder.getFresherActivityItemsById(cfgId);
		FresherActivityCfg cfg = (FresherActivityCfg) FresherActivityCfgDao.getInstance().getCfgById(String.valueOf(cfgId));

		// 普通领奖即立刻标识领取状态
		FresherActivityItem fresherActivityItem = holder.getFresherActivityItemsById(cfgId);
		fresherActivityItem.setGiftTaken(true);
		fresherActivityItem.setClosed(true);
		holder.achieveFresherActivityReward(player, fresherActivityItem);

		String reward = cfg.getReward();
		// 发送奖励
		List<ItemInfo> itemInfoList = new ArrayList<ItemInfo>();
		String[] split = reward.split(";");
		for (String value : split) {
			String[] split2 = value.split(":");
			if (split2.length < 2) {
				continue;
			}
			ItemInfo info = new ItemInfo();
			info.setItemID(Integer.parseInt(split2[0]));
			info.setItemNum(Integer.parseInt(split2[1]));
			itemInfoList.add(info);
		}
		ItemBagMgr.getInstance().addItem(player, itemInfoList);

		String rewardInfoActivity = "";
		List<BilogItemInfo> list = BilogItemInfo.fromStrArr(split);
		rewardInfoActivity = BILogTemplateHelper.getString(list);
		BILogMgr.getInstance().logActivityEnd(player, null, BIActivityCode.SEVER_BEGIN_ACTIVITY_ONE, 0, true, 0, rewardInfoActivity, cfgId);

		return null;
	}

}
