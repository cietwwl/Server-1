package com.playerdata.activity.evilBaoArrive.cfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.playerdata.activity.evilBaoArrive.cfg.EvilBaoArriveSubCfgDAO"  init-method="init" />

public class EvilBaoArriveSubCfgDAO extends CfgCsvDao<EvilBaoArriveSubCfg> {
	public static EvilBaoArriveSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(EvilBaoArriveSubCfgDAO.class);
	}

	@Override
	public Map<String, EvilBaoArriveSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("EvilBaoArrive/EvilBaoArriveSubCfg.csv",EvilBaoArriveSubCfg.class);
		Collection<EvilBaoArriveSubCfg> vals = cfgCacheMap.values();
		for (EvilBaoArriveSubCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
