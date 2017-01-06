package com.rw.service.dropitem;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;

public class DropGuaranteeCreator implements DataExtensionCreator<DropGuaranteeData> {

	@Override
	public DropGuaranteeData create(String key) {
		DropGuaranteeData data = new DropGuaranteeData();
		data.initStore(key);
		return data;
	}

}
