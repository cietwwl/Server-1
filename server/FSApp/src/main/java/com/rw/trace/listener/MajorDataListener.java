package com.rw.trace.listener;

import com.bm.targetSell.TargetSellManager;
import com.bm.targetSell.param.ERoleAttrs;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.dao.cache.trace.DataEventRecorder;
import com.rw.fsutil.dao.cache.trace.SignleChangedEvent;
import com.rw.fsutil.dao.cache.trace.SingleChangedListener;
import com.rw.manager.ServerSwitch;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.behavior.GameBehaviorRecord;
import com.rwbase.dao.majorDatas.pojo.MajorData;

public class MajorDataListener implements SingleChangedListener<MajorData> {

	@Override
	public void notifyDataChanged(SignleChangedEvent<MajorData> event) {
		// TODO Auto-generated method stub
		MajorData oldRecord = event.getOldRecord();
		MajorData newRecord = event.getCurrentRecord();

		GameBehaviorRecord record = (GameBehaviorRecord) DataEventRecorder.getParam();
		if (record == null) {
			return;
		}

		// 记录游戏币的变动日志
		long oldCoin = oldRecord.getCoin();
		long newCoin = newRecord.getCoin();
		if (oldCoin != newCoin) {
			BILogMgr.getInstance().logCoinChanged(record, (int) (newCoin - oldCoin), newCoin);
			if (ServerSwitch.isOpenTargetSell()) {
				TargetSellManager.getInstance().notifyRoleAttrsChange(newRecord.getId(), ERoleAttrs.r_Coin.getId());
			}
		}

		// 记录赠送充值币的变动日志
		int oldGiftGold = oldRecord.getGiftGold();
		int newGiftGold = newRecord.getGiftGold();
		if (oldGiftGold != newGiftGold) {
			BILogMgr.getInstance().logGiftGoldChanged(record, (int) (newGiftGold - oldGiftGold), newGiftGold);
		}

		// 记录充值币的变动日志
		int oldChargeGold = oldRecord.getChargeGold();
		int newChargeGold = newRecord.getChargeGold();
		if (oldChargeGold != newChargeGold) {
			int diffChargeGold = newChargeGold - oldChargeGold;
			Player player = PlayerMgr.getInstance().findPlayerForRead(record.getUserId());
			if (player == null) {
				FSUtilLogger.error("logGoldChanged fail by " + record.getUserId() + ",coinChanged=" + diffChargeGold + ",coinRemain=" + newChargeGold);
			} else {
				BILogMgr biLogMgr = BILogMgr.getInstance();
				biLogMgr.logGoldChanged(record, player, diffChargeGold, newChargeGold);
				biLogMgr.logFinanceMainCoinConsume(record, player, diffChargeGold, newChargeGold);
			}
			if (!ServerSwitch.isOpenTargetSell()) {
				TargetSellManager.getInstance().notifyRoleAttrsChange(newRecord.getId(), ERoleAttrs.r_Charge.getId());
			}
		}
	}

}
