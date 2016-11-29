package com.rw.trace.listener;

import java.util.List;
import java.util.Map;

import com.bm.targetSell.TargetSellManager;
import com.bm.targetSell.param.ERoleAttrs;
import com.bm.targetSell.param.attrs.AbsAchieveAttrValue;
import com.bm.targetSell.param.attrs.EAchieveType;
import com.playerdata.hero.core.FSHero;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.trace.MapItemChangedEvent;
import com.rw.fsutil.dao.cache.trace.MapItemChangedListener;
import com.rw.manager.ServerSwitch;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.targetSell.BenefitAttrCfgDAO;


/**
 * 这里监听的都不是主角的英雄数据
 * @author Alex
 *
 * 2016年11月29日 下午5:04:20
 */
public class FSHeroDataListener implements MapItemChangedListener<FSHero> {

	@Override
	public void notifyDataChanged(MapItemChangedEvent<FSHero> event) {
		if (!ServerSwitch.isOpenTargetSell()) {
			return;
		}
		List<Pair<String, FSHero>> addList = event.getAddList();
		if (addList != null) {
			for (int i = 0, size = addList.size(); i < size; i++) {
				Pair<String, FSHero> pair = addList.get(i);
				FSHero hero = pair.getT2();
				if (hero == null) {
					continue;
				}
				if (hero.isMainRole()) {
					continue;
				}
				TargetSellManager.getInstance().notifyHeroAttrsChange(hero.getUUId(), EAchieveType.AcheiveStar);
			}
		}

		Map<String, Pair<FSHero, FSHero>> changedMap = event.getChangedMap();
		for (Pair<FSHero, FSHero> pair : changedMap.values()) {
			FSHero oldItem = pair.getT1();
			FSHero newItem = pair.getT2();
			if (!oldItem.getQualityId().equals(newItem.getQualityId())) {
				TargetSellManager.getInstance().notifyHeroAttrsChange(newItem.getOwnerUserId(), EAchieveType.AcheiveQuality);
			}

			if (oldItem.getStarLevel() != newItem.getStarLevel()) {
				int modelID = newItem.isMainRole() ? AbsAchieveAttrValue.MainRoleModelID : newItem.getModeId();// 如果是主角，则默认为1，因为主角可以转职，可能有很多个英雄
				BenefitAttrCfg cfg = BenefitAttrCfgDAO.getInstance().getCfgByHeroModelIdAndProcessType(modelID, EAchieveType.AcheiveStar.getId());
				if (cfg != null) {
					TargetSellManager.getInstance().notifyHeroAttrsChange(newItem.getOwnerUserId(), EAchieveType.AcheiveStar);
				}
			}

			if (oldItem.getCareerType() != newItem.getCareerType()) {
				TargetSellManager.getInstance().notifyRoleAttrsChange(newItem.getOwnerUserId(), ERoleAttrs.r_EmbattleCarrer.getId());
			}
		}
	}

}
