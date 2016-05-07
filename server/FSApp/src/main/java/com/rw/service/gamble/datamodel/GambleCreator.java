package com.rw.service.gamble.datamodel;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rw.service.gamble.datamodel.GambleRecord;
import com.rw.service.gamble.datamodel.GambleRecordDAO;
import com.rwbase.dao.gamble.pojo.TableGamble;

public class GambleCreator implements DataExtensionCreator<GambleRecord>{

	@Override
	public GambleRecord create(String userId) {
		GambleRecord tableGamble = new GambleRecord(userId);
		return tableGamble;
	}

}
