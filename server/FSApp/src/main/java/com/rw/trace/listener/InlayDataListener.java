package com.rw.trace.listener;

import java.util.List;

import com.bm.targetSell.TargetSellManager;
import com.bm.targetSell.param.attrs.EAchieveType;
import com.playerdata.HeroMgr;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.playerdata.hero.core.FSHeroMgr;
import com.rw.fsutil.dao.cache.trace.SignleChangedEvent;
import com.rw.fsutil.dao.cache.trace.SingleChangedListener;
import com.rwbase.dao.inlay.InlayItem;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.targetSell.BenefitAttrCfgDAO;
import com.rwproto.BattleCommon.eBattlePositionType;

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
			BenefitAttrCfg cfg = BenefitAttrCfgDAO.getInstance().getCfgByHeroModelIdAndProcessType(oldRecord.getSlotId(), 
					EAchieveType.AchieveStoneLevel.getId());// 宝石等级
			if(cfg != null){
				TargetSellManager.getInstance().notifyHeroAttrsChange(oldRecord.getOwnerId(), cfg.getId());
			}
			
			cfg = BenefitAttrCfgDAO.getInstance().getCfgByHeroModelIdAndProcessType(oldRecord.getSlotId(), 
					EAchieveType.AchieveStoneType.getId());
			if (cfg != null) {
				TargetSellManager.getInstance().notifyHeroAttrsChange(oldRecord.getOwnerId(), cfg.getId());
			}
		}
	}

}
