package com.rwbase.dao.tower;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class TowerFirstAwardCfgDAO extends CfgCsvDao<TowerFirstAwardCfg> {
	public static TowerFirstAwardCfgDAO getInstance() {
		return SpringContextUtil.getBean(TowerFirstAwardCfgDAO.class);
	}

	@Override
	public Map<String, TowerFirstAwardCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("tower/TowerFirstAward.csv",TowerFirstAwardCfg.class);
		return cfgCacheMap;
	}

	public String GetGooldListStr(String id) {
		TowerFirstAwardCfg goodCfg = (TowerFirstAwardCfg) getCfgById(id);
		return goodCfg.goods;
	}
}
