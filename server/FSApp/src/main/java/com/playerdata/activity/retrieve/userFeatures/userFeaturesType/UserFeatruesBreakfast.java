package com.playerdata.activity.retrieve.userFeatures.userFeaturesType;

import com.playerdata.Player;
import com.playerdata.activity.retrieve.cfg.RewardBackCfg;
import com.playerdata.activity.retrieve.cfg.RewardBackCfgDAO;
import com.playerdata.activity.retrieve.data.RewardBackSubItem;
import com.playerdata.activity.retrieve.data.RewardBackTodaySubItem;
import com.playerdata.activity.retrieve.userFeatures.IUserFeatruesHandler;
import com.playerdata.activity.retrieve.userFeatures.UserFeaturesEnum;

public class UserFeatruesBreakfast implements IUserFeatruesHandler{	
	
	//吃早饭这么天经地义的事，肯定是各种写死啦啦啦；写的挫了点，有时间4抽1
	@Override
	public RewardBackTodaySubItem doEvent(Player player) {
		RewardBackTodaySubItem subItem = new RewardBackTodaySubItem();
		subItem.setId(UserFeaturesEnum.breakfast.getId());
		subItem.setCount(0);
		subItem.setMaxCount(1);
		return subItem;
	}

	
	@Override
	public RewardBackSubItem doFresh(RewardBackTodaySubItem todaySubItem,String userId,RewardBackCfgDAO dao) {
		RewardBackCfg cfg = dao.getCfgById(todaySubItem.getId()+"");
		RewardBackSubItem subItem = new RewardBackSubItem();
		subItem.setId(Integer.parseInt(todaySubItem.getId()));
		subItem.setMaxCount(todaySubItem.getMaxCount());
		subItem.setCount(todaySubItem.getCount());
		subItem.setNormalReward(cfg.getNormalRewards());
		subItem.setNormalType(cfg.getNormalCostType());
		subItem.setNormalCost(cfg.getNormalCost());
		subItem.setPerfectReward(cfg.getPerfectRewards());
		subItem.setPerfectType(cfg.getPerfectCostType());
		subItem.setPerfectCost(cfg.getPerfectCost());
		subItem.setIstaken(false);		
		return subItem;
	}	
}
