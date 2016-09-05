package com.rw.service.FresherActivity.Achieve;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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

/**
 * 
 * @author lida
 * 开服活动最终奖励的领取规则
 * 每完成一个进度，则可以领取一个进度的奖励，在活动结束前完成的进度内的奖励都可以领取
 * 我7天的完成度是85%，那么活动结束后，我领完80%的奖励就不能再领取了。
 * 或者我完成度是85%，我已经领取了80%的奖励。那么活动结束的时候，也就直接关闭了
 */
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
		String reward = "";
		
		//处理领取最终奖励
		FresherActivityItem fresherActivityItem = holder.getFresherActivityItemsById(cfgId);
		String currentValue = fresherActivityItem.getCurrentValue();
		int currentAcheiveFinalRewardId = StringUtils.isEmpty(currentValue) ? -1 : Integer.parseInt(currentValue);
		
		
		List<FresherActivityFinalRewardCfg> allCfg = FresherActivityFinalRewardCfgDao.getInstance().getAllCfg();
		Collections.sort(allCfg, new Comparator<FresherActivityFinalRewardCfg>() {

			@Override
			public int compare(FresherActivityFinalRewardCfg o1, FresherActivityFinalRewardCfg o2) {
				// TODO Auto-generated method stub
				return o1.getId() > o2.getId() ? 1 : -1;
			}
		});
		for (FresherActivityFinalRewardCfg fresherActivityFinalRewardCfg : allCfg) {

			if (fresherActivityFinalRewardCfg.getProgress() <= result) {
				
				if(fresherActivityFinalRewardCfg.getId() > currentAcheiveFinalRewardId){
					currentAcheiveFinalRewardId = fresherActivityFinalRewardCfg.getId();
					reward = fresherActivityFinalRewardCfg.getReward();
					break;
				}
			}
		}
		if(StringUtils.isEmpty(reward)){
			return "您的进度不足以领取奖励哦。";
		}
		fresherActivityItem.setCurrentValue(String.valueOf(currentAcheiveFinalRewardId));
		holder.achieveFresherActivityReward(player, fresherActivityItem);

		List<ItemInfo> itemInfoList = new ArrayList<ItemInfo>();
		// 发送奖励
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
		player.getItemBagMgr().addItem(itemInfoList);
		
		String rewardInfoActivity="";
		List<BilogItemInfo> rewardslist = BilogItemInfo.fromStrArr(split);
		rewardInfoActivity = BILogTemplateHelper.getString(rewardslist);		
		BILogMgr.getInstance().logActivityEnd(player, null, BIActivityCode.SEVER_BEGIN_ACTIVITY_ONE, 0, true, 0, rewardInfoActivity,cfgId);
		

		return null;
	}

}
