package com.rwbase.dao.item;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.item.pojo.GemCfg;

public class GemCfgDAO extends CfgCsvDao<GemCfg> {
	public static GemCfgDAO getInstance() {
		return SpringContextUtil.getBean(GemCfgDAO.class);
	}
	@Override
	public Map<String, GemCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("item/Gem.csv",GemCfg.class);
		return cfgCacheMap;
	}
}
