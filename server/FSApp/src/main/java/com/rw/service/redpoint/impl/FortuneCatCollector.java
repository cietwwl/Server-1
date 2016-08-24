package com.rw.service.redpoint.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.activity.fortuneCatType.ActivityFortuneCatTypeMgr;
import com.playerdata.activity.fortuneCatType.cfg.ActivityFortuneCatTypeCfg;
import com.playerdata.activity.fortuneCatType.cfg.ActivityFortuneCatTypeCfgDAO;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItem;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItemHolder;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeSubItem;
import com.rw.service.redpoint.RedPointType;

public class FortuneCatCollector implements RedPointCollector{

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map) {
		ArrayList<String> activityList = new ArrayList<String>();
		ActivityFortuneCatTypeItemHolder fortuneCatHolder = ActivityFortuneCatTypeItemHolder.getInstance();
		List<ActivityFortuneCatTypeItem> fortuneCatItemList = fortuneCatHolder.getItemList(player.getUserId());
		for(ActivityFortuneCatTypeItem item : fortuneCatItemList){
			ActivityFortuneCatTypeCfg cfg = ActivityFortuneCatTypeCfgDAO.getInstance().getCfgById(item.getCfgId());
			if(cfg == null){
				continue;
			}
			if(!ActivityFortuneCatTypeMgr.getInstance().isOpen(cfg)){
				continue;
			}
			if(!item.isTouchRedPoint()){
				activityList.add(item.getCfgId());
				continue;
			}
			int times = item.getTimes();
			List<ActivityFortuneCatTypeSubItem> subItemList = item.getSubItemList();
			ActivityFortuneCatTypeSubItem sub = null;
			for(ActivityFortuneCatTypeSubItem subItem : subItemList){
				if(times == subItem.getNum()&&subItem.getGetGold() == 0){
					sub = subItem;
					break;
				}
			}
			if(sub != null&&player.getUserGameDataMgr().getGold() >= Integer.parseInt(sub.getCost())){
				activityList.add(item.getCfgId());
				break;
			}			
		}
		
		
		
		map.put(RedPointType.FORTUNE_CAT, activityList);
	}

	
	
}
