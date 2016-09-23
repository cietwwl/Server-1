package com.playerdata.activity.retrieve.userFeatures;

import com.playerdata.Player;
import com.playerdata.activity.retrieve.cfg.RewardBackCfgDAO;
import com.playerdata.activity.retrieve.data.RewardBackSubItem;
import com.playerdata.activity.retrieve.data.RewardBackTodaySubItem;


public interface IUserFeatruesHandler {
	
	/**
	 * 
	 * @param userId
	 * @return 负责创建角色或隔天刷新时，将当天的功能对应可完成次数填入todaysubitem
	 */
	public RewardBackTodaySubItem doEvent(Player player);
	
	/**
	 * 
	 * @param todaySubItem
	 * @param userId
	 * @param dao
	 * @return 负责隔天刷新时，将旧的当天功能数据生成对应的新的活动找回数据；
	 * 因为部分奖励和消耗是根据用户等级+vip变化的，需要此时根据自身获得对应的normal-perfect的cfg再获得对应的功能的相关数据；
	 */
	public RewardBackSubItem doFresh(RewardBackTodaySubItem todaySubItem,String userId,RewardBackCfgDAO dao);
	
}
