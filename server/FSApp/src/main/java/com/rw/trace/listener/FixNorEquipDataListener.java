package com.rw.trace.listener;

import com.bm.targetSell.TargetSellManager;
import com.bm.targetSell.param.ERoleAttrs;
import com.bm.targetSell.param.attrs.EAchieveType;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.rw.fsutil.dao.cache.trace.SignleChangedEvent;
import com.rw.fsutil.dao.cache.trace.SingleChangedListener;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.targetSell.BenefitAttrCfgDAO;

/**
 * 普通神器数据监听器
 * @author Alex
 *
 * 2016年11月17日 下午10:27:15
 */
public class FixNorEquipDataListener implements SingleChangedListener<FixNormEquipDataItem>{

	@Override
	public void notifyDataChanged(SignleChangedEvent<FixNormEquipDataItem> event) {
		FixNormEquipDataItem oldRecord = event.getOldRecord();
		FixNormEquipDataItem currentRecord = event.getCurrentRecord();
		if(oldRecord.getStar() != currentRecord.getStar()){
			BenefitAttrCfg cfg = BenefitAttrCfgDAO.getInstance().getCfgByHeroModelIdAndProcessType(oldRecord.getSlot(), EAchieveType.AchieveveHeroFixEquipUpgradStar.getId());
			if (cfg != null) {
				TargetSellManager.getInstance().notifyRoleAttrsChange(oldRecord.getOwnerId(), cfg.getId());
			}
		}
	}

}
