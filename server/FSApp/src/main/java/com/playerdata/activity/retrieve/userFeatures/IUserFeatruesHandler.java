package com.playerdata.activity.retrieve.userFeatures;

import com.playerdata.activity.retrieve.cfg.RewardBackCfgDAO;
import com.playerdata.activity.retrieve.data.RewardBackSubItem;
import com.playerdata.activity.retrieve.data.RewardBackTodaySubItem;


public interface IUserFeatruesHandler {

	public RewardBackTodaySubItem doEvent(String userId);
	
	public RewardBackSubItem doFresh(RewardBackTodaySubItem todaySubItem,String userId,RewardBackCfgDAO dao);
	
}
