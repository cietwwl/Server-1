package com.rwbase.dao.tower;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class TowerFightLimitDAO extends CfgCsvDao<TowerFightLimitCfg> {
	public static TowerFightLimitDAO getInstance() {
		return SpringContextUtil.getBean(TowerFightLimitDAO.class);
	}
	@Override
	public Map<String, TowerFightLimitCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("tower/TowerFightLimit.csv",TowerFightLimitCfg.class);
		return cfgCacheMap;
	}
	public TowerFightLimitCfg getCfgByTowerId(int towerId){
		TowerFightLimitCfg limitCfg = (TowerFightLimitCfg)super.getCfgById(String.valueOf((towerId+1)));
        return limitCfg;
	}
}
