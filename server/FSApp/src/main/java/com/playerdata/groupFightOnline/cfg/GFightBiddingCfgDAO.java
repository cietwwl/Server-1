package com.playerdata.groupFightOnline.cfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;


public class GFightBiddingCfgDAO extends CfgCsvDao<GFightBiddingCfg> {
	public static GFightBiddingCfgDAO getInstance() {
		return SpringContextUtil.getBean(GFightBiddingCfgDAO.class);
	}

	@Override
	public Map<String, GFightBiddingCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("GroupFightOnline/GFightBidding.csv",GFightBiddingCfg.class);
		Collection<GFightBiddingCfg> vals = cfgCacheMap.values();
		for (GFightBiddingCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
