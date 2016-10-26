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

public class UserFeatruesBuyPowerFive implements IUserFeatruesHandler{

	@Override
	public RewardBackTodaySubItem doEvent(Player player) {
		RewardBackTodaySubItem subItem = new RewardBackTodaySubItem();
		subItem.setId(UserFeaturesEnum.buyPowerFive.getId());
		subItem.setCount(0);
		subItem.setMaxCount(3);
		return subItem;
	}

	@Override
	public RewardBackSubItem doFresh(RewardBackTodaySubItem todaySubItem, Player player, CfgOpenLevelLimitDAO dao) {
		int level = player.getLevel();
		if(level >= dao.checkIsOpen(eOpenLevelType.MAIN_CITY, player)){
			int time = player.getPrivilegeMgr().getIntPrivilege(LoginPrivilegeNames.buyPowerCount);
			int cutTime = time >= UserFeatruesMgr.buyPowerFive?time-UserFeatruesMgr.buyPowerLength*4:0;
			cutTime = cutTime > UserFeatruesMgr.buyPowerLength?UserFeatruesMgr.buyPowerLength : cutTime;
			todaySubItem.setMaxCount(cutTime);	
		}else{
			todaySubItem.setMaxCount(0);
		}
		return null;
	}

	@Override
	public String getNorReward(NormalRewardsCfg cfg,RewardBackSubItem subItem) {
		int times = subItem.getMaxCount() - subItem.getCount();
		if(times <= 0||times >UserFeatruesMgr.buyPowerLength){
			return null;
		}
		String tmp = "3:" + 60*times;
		return tmp;
	}

	@Override
	public String getPerReward(PerfectRewardsCfg cfg,RewardBackSubItem subItem) {
		int times = subItem.getMaxCount() - subItem.getCount();
		if(times <= 0||times >UserFeatruesMgr.buyPowerLength){
			return null;
		}
		String tmp = "3:" + 120*times;
		return tmp;
	}

	@Override
	public int getNorCost(NormalRewardsCfg cfg,RewardBackSubItem subItem,RewardBackCfg mainCfg) {
		// TODO Auto-generated method stub
		return ActivityRetrieveTypeHelper.getCostByCountWithCostOrderList(mainCfg.getNormalCostList(), subItem.getMaxCount() - subItem.getCount());
		
	}

	@Override
	public int getPerCost(PerfectRewardsCfg cfg,RewardBackSubItem subItem,RewardBackCfg mainCfg) {
		// TODO Auto-generated method stub
		return ActivityRetrieveTypeHelper.getCostByCountWithCostOrderList(mainCfg.getPerfectCostList(), subItem.getMaxCount() - subItem.getCount());
		
	}



}
