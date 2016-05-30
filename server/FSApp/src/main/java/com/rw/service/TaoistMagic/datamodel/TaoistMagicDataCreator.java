package com.rw.service.TaoistMagic.datamodel;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;

public class TaoistMagicDataCreator implements DataExtensionCreator<TaoistMagicRecord> {

	@Override
	public TaoistMagicRecord create(String uid) {
		TaoistMagicRecord t = new TaoistMagicRecord(uid);
		return t;
	}

}
