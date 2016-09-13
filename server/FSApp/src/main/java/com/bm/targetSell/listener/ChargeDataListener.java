package com.bm.targetSell.listener;

import com.playerdata.charge.dao.ChargeInfo;
import com.rw.fsutil.dao.cache.trace.SignleChangedEvent;
import com.rw.fsutil.dao.cache.trace.SingleChangedListener;

/**
 * 充值数据监听器
 * @author Alex
 * 2016年9月13日 下午4:14:32
 */
public class ChargeDataListener implements SingleChangedListener<ChargeInfo>{

	@Override
	public void notifyDataChanged(SignleChangedEvent<ChargeInfo> event) {
		ChargeInfo currentRecord = event.getCurrentRecord();
		ChargeInfo oldRecord = event.getOldRecord();
		if(currentRecord.getTotalChargeGold() != oldRecord.getTotalChargeGold()){
			System.out.println("gold change-------------------------");
		}
		
		if(currentRecord.getTotalChargeMoney() != oldRecord.getTotalChargeMoney()){
			System.out.println("money change =======================================");
		}
		
	}

}
