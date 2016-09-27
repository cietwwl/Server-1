package com.rwbase.dao.groupcompetition;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.groupcompetition.pojo.GCompScoreCfg;

public abstract class GCompScoreCfgBaseDAO extends CfgCsvDao<GCompScoreCfg> {

	protected abstract String getFileName();
	
	@Override
	protected Map<String, GCompScoreCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map(GroupCompetitionConfigDir.DIR.getFullPath(getFileName()), GCompScoreCfg.class);
		return cfgCacheMap;
	}

}
