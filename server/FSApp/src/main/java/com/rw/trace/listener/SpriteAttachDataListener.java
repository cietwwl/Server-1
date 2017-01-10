package com.rw.trace.listener;

import java.util.List;
import java.util.Map;

import com.bm.targetSell.TargetSellManager;
import com.bm.targetSell.param.attrs.EAchieveType;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.trace.RoleExtChangedEvent;
import com.rw.fsutil.dao.cache.trace.RoleExtChangedListener;
import com.rw.manager.ServerSwitch;
import com.rwbase.dao.spriteattach.SpriteAttachSyn;

/**
 * 英雄附灵数据监听
 * @author Alex
 * 2017年1月9日 上午11:01:31
 */
public class SpriteAttachDataListener implements RoleExtChangedListener<SpriteAttachSyn>{

	@Override
	public void notifyDataChanged(RoleExtChangedEvent<SpriteAttachSyn> event) {
		if(!ServerSwitch.isOpenTargetSell()){
			return;
		}
		
		List<Pair<Integer,SpriteAttachSyn>> addList = event.getAddList();
		if(addList != null && !addList.isEmpty()){
			Pair<Integer, SpriteAttachSyn> pair = addList.get(0);
			//有增加
			SpriteAttachSyn item = pair.getT2();
			TargetSellManager.getInstance().notifyHeroAttrsChange(item.getOwnerId(), EAchieveType.AchieveSpriteAttach);
			
		}
		
		Map<Integer, Pair<SpriteAttachSyn, SpriteAttachSyn>> changedMap = event.getChangedMap();
		if(changedMap != null && !changedMap.isEmpty()){
			for (Pair<SpriteAttachSyn, SpriteAttachSyn> pair : changedMap.values()) {
				if(pair != null){
					SpriteAttachSyn item = pair.getT2();
					TargetSellManager.getInstance().notifyHeroAttrsChange(item.getOwnerId(), EAchieveType.AchieveSpriteAttach);
					break;
				}
			}
		}
	}

}
