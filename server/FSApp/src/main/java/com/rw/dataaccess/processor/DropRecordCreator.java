package com.rw.dataaccess.processor;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.dropitem.DropRecord;

public class DropRecordCreator implements DataExtensionCreator<DropRecord> {

	@Override
	public DropRecord create(String key) {
		DropRecord record = new DropRecord(key);
		return record;
	}

}
