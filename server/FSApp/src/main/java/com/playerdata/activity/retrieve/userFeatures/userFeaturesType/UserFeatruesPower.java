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
import com.rwbase.dao.copypve.CopyInfoCfgDAO;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.power.RoleUpgradeCfgDAO;
import com.rwbase.dao.power.pojo.RoleUpgradeCfg;

public class UserFeatruesPower implements IUserFeatruesHandler{

	@Override
	public RewardBackTodaySubItem doEvent() {
		RewardBackTodaySubItem subItem = new RewardBackTodaySubItem();
		subItem.setId(UserFeaturesEnum.power.getId());
		subItem.setCount(0);		
		RoleUpgradeCfg cfg = (RoleUpgradeCfg) RoleUpgradeCfgDAO.getInstance().getCfgById(String.valueOf(1));
		subItem.setMaxCount(cfg.getMostPower());
		return subItem;
	}
	
	@Override
	public RewardBackSubItem doFresh(RewardBackTodaySubItem todaySubItem, Player player, CfgOpenLevelLimitDAO dao) {
		RoleUpgradeCfg cfg = (RoleUpgradeCfg) RoleUpgradeCfgDAO.getInstance().getCfgById(String.valueOf(player.getLevel()));
		int maxCount = todaySubItem.getCount() <= 0?0:cfg.getMostPower();
		todaySubItem.setMaxCount(maxCount);
		return null;
	}

	@Override
	public String getNorReward(NormalRewardsCfg cfg,RewardBackSubItem subItem) {
		String tmp = "3:"+subItem.getCount()/2;
		return tmp;
	}

	@Override
	public String getPerReward(PerfectRewardsCfg cfg,RewardBackSubItem subItem) {
		// TODO Auto-generated method stub
		String tmp = "3:"+subItem.getCount();
		return tmp;
	}

	@Override
	public int getNorCost(NormalRewardsCfg cfg,RewardBackSubItem subItem,RewardBackCfg mainCfg) {
		
		
		return ActivityRetrieveTypeHelper.getInstance().getCostByCountWithCostOrderList(mainCfg.getNormalCostList(), subItem.getCount());
	}

	@Override
	public int getPerCost(PerfectRewardsCfg cfg,RewardBackSubItem subItem,RewardBackCfg mainCfg) {
		// TODO Auto-generated method stub
		return ActivityRetrieveTypeHelper.getInstance().getCostByCountWithCostOrderList(mainCfg.getPerfectCostList(), subItem.getCount());
		
	}

}
