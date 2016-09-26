package com.rw.dataaccess.processor;

import com.playerdata.groupcompetition.holder.data.GCompFightRecordData;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;

public class GCompFightRecordCreator implements DataExtensionCreator<GCompFightRecordData> {

	@Override
	public GCompFightRecordData create(String key) {
		GCompFightRecordData data = new GCompFightRecordData();
		data.setMatchId(Integer.parseInt(key));
		return data;
	}

}
