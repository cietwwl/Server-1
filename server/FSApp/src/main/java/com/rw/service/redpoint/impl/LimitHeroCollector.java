package com.rw.service.redpoint.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.limitHeroType.ActivityLimitHeroTypeMgr;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroCfg;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroCfgDAO;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItem;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItemHolder;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeSubItem;
import com.rw.service.redpoint.RedPointType;

public class LimitHeroCollector implements RedPointCollector{

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map) {
		ArrayList<String> activityList = new ArrayList<String>();
		ActivityLimitHeroTypeItemHolder limitHeroHolder = ActivityLimitHeroTypeItemHolder.getInstance();
		List<ActivityLimitHeroTypeItem> limitHeroItemList = limitHeroHolder.getItemList(player.getUserId());
		for(ActivityLimitHeroTypeItem item : limitHeroItemList){
			ActivityLimitHeroCfg cfg = ActivityLimitHeroCfgDAO.getInstance().getCfgById(item.getCfgId());
			if(cfg == null){
				continue;
			}
			if(!ActivityLimitHeroTypeMgr.getInstance().isOpen(cfg)){
				continue;
			}
			if(!item.isTouchRedPoint()){
				activityList.add(item.getCfgId());
				continue;
			}
			long now = System.currentTimeMillis();
			long lastTime = item.getLastSingleTime();
			if((now - lastTime)> cfg.getFreecd() * 1000){
				activityList.add(item.getCfgId());
				continue;
			}
			List<ActivityLimitHeroTypeSubItem> subList = item.getSubList();
			for (ActivityLimitHeroTypeSubItem subItem : subList) {// 配置表里的每种奖励				
				if (item.getIntegral() >= subItem.getIntegral()
						&& !subItem.isTanken()) {
					activityList.add(item.getCfgId());	
					break;
				}
			}
		}
		
		
		if (!activityList.isEmpty()) {
		map.put(RedPointType.LIMIT_HERO, activityList);
		}
	}

	
	
}
