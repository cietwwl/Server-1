package com.playerdata.activity.retrieve.userFeatures.userFeaturesType;

import com.playerdata.Player;
import com.playerdata.activity.retrieve.ActivityRetrieveTypeHelper;
import com.playerdata.activity.retrieve.cfg.NormalRewardsCfg;
import com.playerdata.activity.retrieve.cfg.NormalRewardsCfgDAO;
import com.playerdata.activity.retrieve.cfg.PerfectRewardsCfg;
import com.playerdata.activity.retrieve.cfg.PerfectRewardsCfgDAO;
import com.playerdata.activity.retrieve.cfg.RewardBackCfg;
import com.playerdata.activity.retrieve.cfg.RewardBackCfgDAO;
import com.playerdata.activity.retrieve.data.RewardBackSubItem;
import com.playerdata.activity.retrieve.data.RewardBackTodaySubItem;
import com.playerdata.activity.retrieve.userFeatures.IUserFeatruesHandler;
import com.playerdata.activity.retrieve.userFeatures.UserFeatruesMgr;
import com.playerdata.activity.retrieve.userFeatures.UserFeaturesEnum;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwproto.PrivilegeProtos.LoginPrivilegeNames;

public class UserFeatruesBuyPowerOne implements IUserFeatruesHandler{
	//写的挫了点，有时间5抽1
	@Override
	public RewardBackTodaySubItem doEvent(Player player) {
		RewardBackTodaySubItem subItem = new RewardBackTodaySubItem();
		subItem = ActivityRetrieveTypeHelper.getInstance().doBuyPowerEvent(UserFeaturesEnum.buyPowerOne);
		return subItem;
	}

	@Override
	public RewardBackSubItem doFresh(RewardBackTodaySubItem todaySubItem, Player player, CfgOpenLevelLimitDAO dao) {
		ActivityRetrieveTypeHelper.getInstance().doBuyPowerFresh(todaySubItem, player, dao, UserFeatruesMgr.buyPowerOne);
		return null;
	}
	/**
	 * 奖励应该有个附表来读取，字段3:1，字段60或120这样，策划没有提供则写死，普通3:60；完美3:120；
	 */
	@Override
	public String getNorReward(NormalRewardsCfg cfg,RewardBackSubItem subItem) {
		return ActivityRetrieveTypeHelper.getInstance().doBuyPowerGetNormalReward(cfg, subItem);
	}

	@Override
	public String getPerReward(PerfectRewardsCfg cfg,RewardBackSubItem subItem) {
		return ActivityRetrieveTypeHelper.getInstance().doBuyPowerGetPerfectReward(cfg, subItem);
	}

	@Override
	public int getNorCost(NormalRewardsCfg cfg,RewardBackSubItem subItem,RewardBackCfg mainCfg) {
		// TODO Auto-generated method stub
		return ActivityRetrieveTypeHelper.getInstance().getCostByCountWithCostOrderList(mainCfg.getNormalCostList(), subItem.getMaxCount() - subItem.getCount());
		
	}

	@Override
	public int getPerCost(PerfectRewardsCfg cfg,RewardBackSubItem subItem,RewardBackCfg mainCfg) {
		// TODO Auto-generated method stub
		return ActivityRetrieveTypeHelper.getInstance().getCostByCountWithCostOrderList(mainCfg.getPerfectCostList(), subItem.getMaxCount() - subItem.getCount());
		
	}


}
