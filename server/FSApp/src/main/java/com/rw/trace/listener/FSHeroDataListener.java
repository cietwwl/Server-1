package com.rw.trace.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bm.targetSell.TargetSellManager;
import com.bm.targetSell.param.ERoleAttrs;
import com.bm.targetSell.param.attrs.EAchieveType;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.hero.core.FSHero;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.trace.MapItemChangedEvent;
import com.rw.fsutil.dao.cache.trace.MapItemChangedListener;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.targetSell.BenefitAttrCfgDAO;

public class FSHeroDataListener  implements MapItemChangedListener<FSHero>{

	@Override
	public void notifyDataChanged(MapItemChangedEvent<FSHero> event) {
		List<Pair<String,FSHero>> addList = event.getAddList();
		
		
		
		Map<String, Pair<FSHero, FSHero>> changedMap = event.getChangedMap();
		
		for (Pair<FSHero, FSHero> pair : changedMap.values()) {
			FSHero oldItem = pair.getT1();
			FSHero newItem = pair.getT2();
			
			Player player = PlayerMgr.getInstance().find(newItem.getOwnerUserId());
			
			
			if(!oldItem.getQualityId().equals(newItem.getQualityId())){
				List<ERoleAttrs> roleAttrsList = new ArrayList<ERoleAttrs>();
				roleAttrsList.add(ERoleAttrs.r_EmbattleQuality);
				TargetSellManager.getInstance().notifyRoleAttrsChange(player, roleAttrsList);
			}
			
			if (oldItem.getStarLevel() != newItem.getStarLevel()) {
				BenefitAttrCfg cfg = BenefitAttrCfgDAO.getInstance().getCfgByHeroModelIdAndProcessType(newItem.getModeId(), EAchieveType.AcheiveStar.getId());
				if (cfg != null) {
					int id = cfg.getId();
					ERoleAttrs roleAttrs = ERoleAttrs.getRoleAttrs(id);
					if (roleAttrs != null) {
						List<ERoleAttrs> roleAttrsList = new ArrayList<ERoleAttrs>();
						roleAttrsList.add(roleAttrs);
						TargetSellManager.getInstance().notifyRoleAttrsChange(player, roleAttrsList);
					}
				}
			}
			
			if(oldItem.getCareerType() != newItem.getCareerType()){
				List<ERoleAttrs> roleAttrsList = new ArrayList<ERoleAttrs>();
				roleAttrsList.add(ERoleAttrs.r_EmbattleCarrer);
				TargetSellManager.getInstance().notifyRoleAttrsChange(player, roleAttrsList);
			}
		}
	}

}
