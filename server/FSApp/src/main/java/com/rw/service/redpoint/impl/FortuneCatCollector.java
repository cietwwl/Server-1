package com.rw.service.redpoint.impl;

import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.activity.evilBaoArrive.EvilBaoArriveMgr;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class FortuneCatCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		List<String> evilBaoArriveList = EvilBaoArriveMgr.getInstance().getRedPoint(player);
		if(!evilBaoArriveList.isEmpty()){
			map.put(RedPointType.FORTUNE_CAT, EvilBaoArriveMgr.getInstance().getRedPoint(player));
		}
		
//		ArrayList<String> activityList = new ArrayList<String>();
//		ActivityFortuneCatTypeItemHolder fortuneCatHolder = ActivityFortuneCatTypeItemHolder.getInstance();
//		List<ActivityFortuneCatTypeItem> fortuneCatItemList = null;
//		List<ActivityFortuneCatTypeCfg> cfgList = ActivityFortuneCatTypeCfgDAO.getInstance().getAllCfg();
//		for(ActivityFortuneCatTypeCfg cfg : cfgList){
//			if (!ActivityFortuneCatTypeMgr.getInstance().isOpen(cfg)) {
//				continue;
//			}
//			if(fortuneCatItemList == null){
//				fortuneCatItemList = fortuneCatHolder.getItemList(player.getUserId());
//			}
//			ActivityFortuneCatTypeItem item = null;
//			for(ActivityFortuneCatTypeItem temp : fortuneCatItemList){
//				if(StringUtils.equals(temp.getId()+"", ActivityFortuneTypeEnum.FortuneCat.getCfgId())){
//					item = temp;
//					break;
//				}
//			}
//			if(item == null){
//				continue;
//			}
//			if (!item.isTouchRedPoint()) {
//				activityList.add(item.getCfgId());
//				continue;
//			}
//			int times = item.getTimes();
//			times++;
//			List<ActivityFortuneCatTypeSubItem> subItemList = item.getSubItemList();
//			ActivityFortuneCatTypeSubItem sub = null;
//			for (ActivityFortuneCatTypeSubItem subItem : subItemList) {
//				if (times == subItem.getNum() && subItem.getGetGold() == 0) {
//					sub = subItem;
//					break;
//				}
//			}
//			if (sub != null && player.getUserGameDataMgr().getGold() >= Integer.parseInt(sub.getCost()) && player.getVip() >= sub.getVip()) {
//				activityList.add(item.getCfgId());
//				break;
//			}			
//		}
//		
//		if (!activityList.isEmpty()) {
//			map.put(RedPointType.FORTUNE_CAT, activityList);
//		}
	}

	@Override
	public eOpenLevelType getOpenType() {
		return null;
	}
}
