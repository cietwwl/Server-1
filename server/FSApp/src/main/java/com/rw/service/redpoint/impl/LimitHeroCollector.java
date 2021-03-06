package com.rw.service.redpoint.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.playerdata.Player;
import com.playerdata.activity.limitHeroType.ActivityLimitHeroEnum;
import com.playerdata.activity.limitHeroType.ActivityLimitHeroTypeMgr;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitGamblePlanCfg;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitGamblePlanCfgDAO;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroCfg;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroCfgDAO;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItem;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItemHolder;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeSubItem;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwproto.ActivityLimitHeroTypeProto.GambleType;

public class LimitHeroCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		ArrayList<String> activityList = new ArrayList<String>();
		ActivityLimitHeroTypeItemHolder limitHeroHolder = ActivityLimitHeroTypeItemHolder.getInstance();
		List<ActivityLimitHeroTypeItem> limitHeroItemList = null;
		List<ActivityLimitHeroCfg> cfgList = ActivityLimitHeroCfgDAO.getInstance().getAllCfg();
		for(ActivityLimitHeroCfg cfg: cfgList){
			if(!ActivityLimitHeroTypeMgr.getInstance().isOpen(cfg)){
				continue;
			}
			if(limitHeroItemList == null){
				limitHeroItemList = limitHeroHolder.getItemList(player.getUserId());
			}
			
			ActivityLimitHeroTypeItem item = null;
			for(ActivityLimitHeroTypeItem temp : limitHeroItemList){
				if(StringUtils.equals(temp.getId()+"", ActivityLimitHeroEnum.LimitHero.getCfgId())){
					item = temp;
					break;
				}
			}
			if(item == null ){
				continue;
			}
			if (!item.isTouchRedPoint()) {
				activityList.add(item.getCfgId());
				continue;
			}
			ActivityLimitGamblePlanCfg planCfg = ActivityLimitGamblePlanCfgDAO.getInstance().getCfgByType(GambleType.SINGLE.getNumber(), level);
			if (planCfg == null) {
				continue;
			}
			long now = System.currentTimeMillis();
			long lastTime = item.getLastSingleTime();
			if ((now - lastTime) > planCfg.getRecoverTime() * 1000) {
				activityList.add(item.getCfgId());
				continue;
			}
			List<ActivityLimitHeroTypeSubItem> subList = item.getSubList();
			for (ActivityLimitHeroTypeSubItem subItem : subList) {// 配置表里的每种奖励
				if (item.getIntegral() >= subItem.getIntegral() && !subItem.isTanken()) {
					activityList.add(item.getCfgId());
					break;
				}
			}
		}

		if (!activityList.isEmpty()) {
			map.put(RedPointType.LIMIT_HERO, activityList);
		}
	}

	@Override
	public eOpenLevelType getOpenType() {
		return null;
	}

}
