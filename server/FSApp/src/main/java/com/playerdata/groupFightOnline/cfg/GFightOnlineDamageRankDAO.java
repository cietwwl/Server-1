package com.playerdata.groupFightOnline.cfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.service.GroupFightOnline.datamodel.GFightOnlineDamageRankHelper"  init-method="init" />

public class GFightOnlineDamageRankDAO extends CfgCsvDao<GFightOnlineDamageRankCfg> {
	public static GFightOnlineDamageRankDAO getInstance() {
		return SpringContextUtil.getBean(GFightOnlineDamageRankDAO.class);
	}

	@Override
	public Map<String, GFightOnlineDamageRankCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("GroupFightOnline/GFightOnlineDamageRank.csv",GFightOnlineDamageRankCfg.class);
		Collection<GFightOnlineDamageRankCfg> vals = cfgCacheMap.values();
		for (GFightOnlineDamageRankCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
