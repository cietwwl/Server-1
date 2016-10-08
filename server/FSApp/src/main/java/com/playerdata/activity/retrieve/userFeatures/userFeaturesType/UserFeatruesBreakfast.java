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
import com.playerdata.activity.retrieve.userFeatures.UserFeaturesEnum;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwproto.PrivilegeProtos.PvePrivilegeNames;

public class UserFeatruesBreakfast implements IUserFeatruesHandler{	
	
	//吃早饭这么天经地义的事，肯定是各种写死啦啦啦；写的挫了点，有时间4抽1
	@Override
	public RewardBackTodaySubItem doEvent() {
		RewardBackTodaySubItem subItem = new RewardBackTodaySubItem();
		subItem = ActivityRetrieveTypeHelper.getInstance().doEatEvent(UserFeaturesEnum.breakfast);
		
		return subItem;
	}

	
	@Override
	public RewardBackSubItem doFresh(RewardBackTodaySubItem todaySubItem, Player player, CfgOpenLevelLimitDAO dao) {
		ActivityRetrieveTypeHelper.getInstance().doEatFresh(todaySubItem, player, dao);
		return null;
	}


	@Override
	public String getNorReward(NormalRewardsCfg cfg,RewardBackSubItem subItem) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getPerReward(PerfectRewardsCfg cfg,RewardBackSubItem subItem) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int getNorCost(NormalRewardsCfg cfg,RewardBackSubItem subItem,RewardBackCfg mainCfg) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getPerCost(PerfectRewardsCfg cfg,RewardBackSubItem subItem,RewardBackCfg mainCfg) {
		// TODO Auto-generated method stub
		return 0;
	}


}
