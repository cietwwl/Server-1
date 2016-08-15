package com.playerdata.activity.limitHeroType;

import java.util.ArrayList;
import java.util.List;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.exChangeType.ActivityExChangeTypeEnum;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfgDAO;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItem;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItemHolder;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroCfg;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroCfgDAO;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItem;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItemHolder;

public class ActivityLimitHeroTypeMgr {
	private static ActivityLimitHeroTypeMgr instance = new ActivityLimitHeroTypeMgr();
	public static ActivityLimitHeroTypeMgr getInstance (){
		return instance;
	}
	
	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
//		checkCfgVersion(player);
//		checkClose(player);
	}
	
	private void checkNewOpen(Player player) {
		ActivityLimitHeroTypeItemHolder dataHolder = ActivityLimitHeroTypeItemHolder.getInstance();
		List<ActivityLimitHeroCfg> allCfgList = ActivityLimitHeroCfgDAO.getInstance().getAllCfg();
		for (ActivityLimitHeroCfg cfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (!isOpen(cfg)) {
				// 活动未开启
				continue;
			}			
			ActivityLimitHeroTypeItem targetItem = dataHolder.getItem(player.getUserId());// 已在之前生成数据的活动
			if (targetItem != null) {					
				continue;
			}
			targetItem = ActivityLimitHeroCfgDAO.getInstance().newItem(player, cfg);// 生成新开启活动的数据
			if(targetItem == null){
				continue;
			}
			dataHolder.updateItem(player, targetItem);
		}
	}

	private boolean isOpen(ActivityLimitHeroCfg cfg) {
		if (cfg != null) {
			long startTime = cfg.getStartTime();
			long endTime = cfg.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}
	
	
	
	
}
