package com.rw.trace.listener;

import java.util.List;

import com.rw.fsutil.dao.cache.trace.DataEventRecorder;
import com.rw.fsutil.dao.cache.trace.SignleChangedEvent;
import com.rw.fsutil.dao.cache.trace.SingleChangedListener;
import com.rw.service.log.BILogMgr;
import com.rwbase.dao.majorDatas.pojo.MajorData;

public class MajorDataListener implements SingleChangedListener<MajorData>{

	@Override
	public void notifyDataChanged(SignleChangedEvent<MajorData> event) {
		// TODO Auto-generated method stub
		MajorData oldRecord = event.getOldRecord();
		MajorData newRecord = event.getCurrentRecord();
		
		List<Object> list = (List<Object>)DataEventRecorder.getParam();
		if(list == null){
			return;
		}
		
		//记录游戏币的变动日志
		long oldCoin = oldRecord.getCoin();
		long newCoin = newRecord.getCoin();
		if(oldCoin != newCoin){
			BILogMgr.getInstance().logCoinChanged(list, (int)(newCoin - oldCoin), newCoin);
		}
		
		//记录赠送充值币的变动日志
		int oldGiftGold = oldRecord.getGiftGold();
		int newGiftGold = newRecord.getGiftGold();
		if(oldGiftGold != newGiftGold){
			BILogMgr.getInstance().logGiftGoldChanged(list, (int)(newGiftGold- oldGiftGold), newGiftGold);
		}
		
		//记录充值币的变动日志
		int oldChargeGold = oldRecord.getChargeGold();
		int newChargeGold = newRecord.getChargeGold();
		if(oldChargeGold != newChargeGold){
			BILogMgr.getInstance().logGoldChanged(list, (int)(newChargeGold- oldChargeGold), newChargeGold);
		}
	}


}
