package com.rw.dataaccess.processor;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.vip.pojo.TableVip;

public class VipCreator implements DataExtensionCreator<TableVip>{

	@Override
	public TableVip create(String userId) {
		TableVip tableVip = new TableVip();
		tableVip.setUserId(userId);
		return tableVip;
	}

}
