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
		
		//TODO 还要检查一下英雄是否上阵，如果上，要获取上阵位置
		
		
		int pos = 0;
		
		 EmbattlePositionInfo positionInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(currentRecord.getOwnerId(), eBattlePositionType.Normal_VALUE, null);
		 if(positionInfo == null){
				return;
			}
			String heroId = "";
			List<EmbattleHeroPosition> p = positionInfo.getPos();
			for (EmbattleHeroPosition embattleHeroPosition : p) {
				if (embattleHeroPosition.getPos() == pos) {
					heroId = embattleHeroPosition.getId();
					break;
				} 
			}
		
		if(oldRecord.getModelId() != currentRecord.getModelId()){
			BenefitAttrCfg cfg = BenefitAttrCfgDAO.getInstance().getCfgByHeroModelIdAndProcessType(pos, 
					EAchieveType.AchieveStoneLevel.getId());// 宝石等级
			if(cfg != null){
				TargetSellManager.getInstance().notifyRoleAttrsChange(oldRecord.getOwnerId(), cfg.getId());
			}
			
			cfg = BenefitAttrCfgDAO.getInstance().getCfgByHeroModelIdAndProcessType(pos, 
					EAchieveType.AchieveStoneType.getId());
					if(cfg!= null){
						TargetSellManager.getInstance().notifyRoleAttrsChange(oldRecord.getOwnerId(), cfg.getId());		
					}
		}
	}

}
