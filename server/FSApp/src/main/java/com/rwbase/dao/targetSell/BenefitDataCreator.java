package com.rwbase.dao.targetSell;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;

public class BenefitDataCreator implements DataExtensionCreator<TargetSellRecord>{

	@Override
	public TargetSellRecord create(String key) {
		TargetSellRecord record = new TargetSellRecord();
		record.setUserId(key);
		return record;
	}

}
