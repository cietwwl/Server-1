package com.rw.trace.listener;

import java.util.List;
import java.util.Map;

import com.bm.targetSell.TargetSellManager;
import com.bm.targetSell.param.attrs.EAchieveType;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.trace.RoleExtChangedEvent;
import com.rw.fsutil.dao.cache.trace.RoleExtChangedListener;
import com.rw.manager.ServerSwitch;
import com.rwbase.dao.inlay.InlayItem;

/**
 * 宝石数据监测
 * @author Alex
 *
 * 2016年11月17日 下午10:30:40
 */
public class InlayDataListener implements RoleExtChangedListener<InlayItem>{


	@Override
	public void notifyDataChanged(RoleExtChangedEvent<InlayItem> event) {
		if(!ServerSwitch.isOpenTargetSell()){
			return;
		}
		List<Pair<Integer,InlayItem>> addList = event.getAddList();
		if(addList != null && !addList.isEmpty()){
			Pair<Integer, InlayItem> pair = addList.get(0);
			//有增加
			InlayItem item = pair.getT2();
			TargetSellManager.getInstance().notifyHeroAttrsChange(item.getOwnerId(), EAchieveType.AchieveStoneLevel);
			TargetSellManager.getInstance().notifyHeroAttrsChange(item.getOwnerId(), EAchieveType.AchieveStoneType);
			
		}
		
		
		Map<Integer, Pair<InlayItem, InlayItem>> changedMap = event.getChangedMap();
		if(changedMap != null && !changedMap.isEmpty()){
			for (Pair<InlayItem, InlayItem> pair : changedMap.values()) {
				if(pair != null){
					InlayItem item = pair.getT2();
					TargetSellManager.getInstance().notifyHeroAttrsChange(item.getOwnerId(), EAchieveType.AchieveStoneLevel);
					TargetSellManager.getInstance().notifyHeroAttrsChange(item.getOwnerId(), EAchieveType.AchieveStoneType);
					break;
				}
			}
		}
		
	}

}
