package com.rwbase.dao.groupcompetition;

import java.util.Iterator;
import java.util.Map;

import com.playerdata.groupcompetition.util.GCompEventsStatus;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.groupcompetition.pojo.GCompTimeCfg;

public class GCompTimeCfgDAO extends CfgCsvDao<GCompTimeCfg> {

	@Override
	protected Map<String, GCompTimeCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map(GroupCompetitionConfigDir.DIR.getFullPath("GCompTimeCfg.csv"), GCompTimeCfg.class);
		for (Iterator<String> keyItr = cfgCacheMap.keySet().iterator(); keyItr.hasNext();) {
			GCompEventsStatus.setTimeInfo(cfgCacheMap.get(keyItr.next()));
			break;
		}
		return cfgCacheMap;
	}

}
