package com.playerdata.groupFightOnline.cfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.service.GroupFightOnline.datamodel.GFightOnlineDefeatRankHelper"  init-method="init" />

public class GFightOnlineDefeatRankDAO extends CfgCsvDao<GFightOnlineDefeatRankCfg> {
	public static GFightOnlineDefeatRankDAO getInstance() {
		return SpringContextUtil.getBean(GFightOnlineDefeatRankDAO.class);
	}

	@Override
	public Map<String, GFightOnlineDefeatRankCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("GroupFightOnline/GFightOnlineDefeatRank.csv",GFightOnlineDefeatRankCfg.class);
		Collection<GFightOnlineDefeatRankCfg> vals = cfgCacheMap.values();
		for (GFightOnlineDefeatRankCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
