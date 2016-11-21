package com.rw.trace.listener;

import java.util.List;
import java.util.Map;

import com.bm.targetSell.TargetSellManager;
import com.bm.targetSell.param.attrs.EAchieveType;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.trace.RoleExtChangedEvent;
import com.rw.fsutil.dao.cache.trace.RoleExtChangedListener;


/**
 * 经验神器数据监听器
 * @author Alex
 *
 * 2016年11月17日 下午10:09:37
 */
public class FixExpEquipDataListener implements RoleExtChangedListener<FixExpEquipDataItem>{



	@Override
	public void notifyDataChanged(RoleExtChangedEvent<FixExpEquipDataItem> event) {
		
		List<Pair<Integer,FixExpEquipDataItem>> addList = event.getAddList();
		if(addList != null && !addList.isEmpty()){
			Pair<Integer, FixExpEquipDataItem> pair = addList.get(0);
			//有增加神器
			FixExpEquipDataItem item = pair.getT2();
			TargetSellManager.getInstance().notifyHeroAttrsChange(item.getOwnerId(), EAchieveType.AchieveveHeroFixEquipUpgradStar);
			
		}
		
		
		Map<Integer, Pair<FixExpEquipDataItem, FixExpEquipDataItem>> changedMap = event.getChangedMap();
		if(changedMap != null && !changedMap.isEmpty()){
			for (Pair<FixExpEquipDataItem, FixExpEquipDataItem> pair : changedMap.values()) {
				if(pair != null){
					FixExpEquipDataItem item = pair.getT2();
					TargetSellManager.getInstance().notifyHeroAttrsChange(item.getOwnerId(), EAchieveType.AchieveveHeroFixEquipUpgradStar);
					break;
				}
			}
		}
		
	}

}
