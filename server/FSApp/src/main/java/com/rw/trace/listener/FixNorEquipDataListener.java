package com.rw.trace.listener;

import java.util.List;
import java.util.Map;

import com.bm.targetSell.TargetSellManager;
import com.bm.targetSell.param.attrs.EAchieveType;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.trace.RoleExtChangedEvent;
import com.rw.fsutil.dao.cache.trace.RoleExtChangedListener;
import com.rw.manager.ServerSwitch;

/**
 * 普通神器数据监听器
 * @author Alex
 *
 * 2016年11月17日 下午10:27:15
 */
public class FixNorEquipDataListener implements RoleExtChangedListener<FixNormEquipDataItem> {

	@Override
	public void notifyDataChanged(RoleExtChangedEvent<FixNormEquipDataItem> event) {
		if (!ServerSwitch.isOpenTargetSell()) {
			return;
		}
		List<Pair<Integer, FixNormEquipDataItem>> addList = event.getAddList();
		if (addList != null && !addList.isEmpty()) {
			Pair<Integer, FixNormEquipDataItem> pair = addList.get(0);
			// 有增加神器
			FixNormEquipDataItem item = pair.getT2();
			TargetSellManager.getInstance().notifyHeroAttrsChange(item.getOwnerId(), EAchieveType.AchieveveHeroFixEquipUpgradStar);

		}

		Map<Integer, Pair<FixNormEquipDataItem, FixNormEquipDataItem>> changedMap = event.getChangedMap();
		if (changedMap != null && !changedMap.isEmpty()) {
			for (Pair<FixNormEquipDataItem, FixNormEquipDataItem> pair : changedMap.values()) {
				if (pair == null) {
					continue;
				}
				FixNormEquipDataItem old = pair.getT1();
				FixNormEquipDataItem current = pair.getT2();
				if (current == null) {
					continue;
				}
				if (old == null || old.getStar() != current.getStar()) {
					TargetSellManager.getInstance().notifyHeroAttrsChange(current.getOwnerId(), EAchieveType.AchieveveHeroFixEquipUpgradStar);
				}
			}
		}

	}

}
