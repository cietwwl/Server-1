package com.rw.trace.listener;

import com.bm.targetSell.TargetSellManager;
import com.bm.targetSell.param.attrs.EAchieveType;
import com.rw.fsutil.dao.cache.trace.SignleChangedEvent;
import com.rw.fsutil.dao.cache.trace.SingleChangedListener;
import com.rwbase.dao.inlay.InlayItem;

/**
 * 宝石数据监测
 * @author Alex
 *
 * 2016年11月17日 下午10:30:40
 */
public class InlayDataListener implements SingleChangedListener<InlayItem>{

	@Override
	public void notifyDataChanged(SignleChangedEvent<InlayItem> event) {
		InlayItem oldRecord = event.getOldRecord();
		InlayItem currentRecord = event.getCurrentRecord();
		
		if(oldRecord.getModelId() != currentRecord.getModelId()){
			TargetSellManager.getInstance().notifyHeroAttrsChange(oldRecord.getOwnerId(), EAchieveType.AchieveStoneLevel);
			TargetSellManager.getInstance().notifyHeroAttrsChange(oldRecord.getOwnerId(), EAchieveType.AchieveStoneType);
			
		}
	}

}
