package com.rw.trace.listener;

import com.bm.targetSell.TargetSellManager;
import com.bm.targetSell.param.ERoleAttrs;
import com.bm.targetSell.param.attrs.AbsAchieveAttrValue;
import com.bm.targetSell.param.attrs.EAchieveType;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.playerdata.hero.core.FSHero;
import com.playerdata.hero.core.FSHeroMgr;
import com.rw.fsutil.dao.cache.trace.SignleChangedEvent;
import com.rw.fsutil.dao.cache.trace.SingleChangedListener;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.targetSell.BenefitAttrCfgDAO;


/**
 * 经验神器数据监听器
 * @author Alex
 *
 * 2016年11月17日 下午10:09:37
 */
public class FixExpEquipDataListener implements SingleChangedListener<FixExpEquipDataItem>{

	@Override
	public void notifyDataChanged(SignleChangedEvent<FixExpEquipDataItem> event) {
		FixExpEquipDataItem oldRecord = event.getOldRecord();
		FixExpEquipDataItem currentRecord = event.getCurrentRecord();
		
		
		if(oldRecord.getStar() != currentRecord.getStar()){
			//保存到缓存，具体逻辑在发送的时候再进行检查
			TargetSellManager.getInstance().notifyHeroAttrsChange(oldRecord.getOwnerId(), EAchieveType.AchieveveHeroFixEquipUpgradStar);
		}
	}

}
