package com.bm.targetSell.listener;

import com.bm.targetSell.TargetSellManager;
import com.bm.targetSell.param.ERoleAttrs;
import com.playerdata.charge.dao.ChargeInfo;
import com.rw.fsutil.dao.cache.trace.SignleChangedEvent;
import com.rw.fsutil.dao.cache.trace.SingleChangedListener;
import com.rw.manager.ServerSwitch;

/**
 * 充值数据监听器
 * @author Alex
 * 2016年9月13日 下午4:14:32
 */
public class ChargeDataListener implements SingleChangedListener<ChargeInfo>{

	@Override
	public void notifyDataChanged(SignleChangedEvent<ChargeInfo> event) {
		if(!ServerSwitch.isOpenTargetSell()){
			return;
		}
		ChargeInfo currentRecord = event.getCurrentRecord();
		ChargeInfo oldRecord = event.getOldRecord();
		if(currentRecord.getTotalChargeGold() != oldRecord.getTotalChargeGold()){
//			System.err.println("gold change-------------------------old charge:" + oldRecord.getTotalChargeGold()
//					+ ",cur charge:" + currentRecord.getTotalChargeGold());
		}
		
		if(currentRecord.getTotalChargeMoney() > oldRecord.getTotalChargeMoney()){
//			System.err.println("money change =======================================" + oldRecord.getTotalChargeMoney()
//					+ ", cur money:" + currentRecord.getTotalChargeMoney());
//			TargetSellManager.getInstance().increaseChargeMoney(currentRecord.getUserId(), currentRecord.getTotalChargeMoney());
			TargetSellManager.getInstance().notifyRoleAttrsChange(oldRecord.getUserId(), ERoleAttrs.r_Charge.getId());
		}
		
	}

}
