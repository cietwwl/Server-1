package com.rwbase.dao.targetSell;

import java.util.HashMap;

import com.bm.targetSell.TargetSellManager;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;

public class BenefitDataCreator implements DataExtensionCreator<TargetSellRecord>{

	@Override
	public TargetSellRecord create(String key) {
		TargetSellRecord record = new TargetSellRecord();
		record.setUserId(key);
		record.setBenefitScore(0);
		record.setNextClearScoreTime(TargetSellManager.getInstance().getNextRefreshTimeMils());
		record.setItemMap(new HashMap<Integer, BenefitItems>());
		record.setRecieveMap(new HashMap<Integer, Integer>());
		return record;
	}

}
