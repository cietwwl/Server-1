package com.rwbase.dao.copy.cfg;

import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class CopyCfgDAO extends CfgCsvDao<CopyCfg> {

	public static CopyCfgDAO getInstance() {
		return SpringContextUtil.getBean(CopyCfgDAO.class);
	}

	@Override
	public Map<String, CopyCfg> initJsonCfg() {
		Map<String, CopyCfg> readCsv2Map = CfgCsvHelper.readCsv2Map("copy/copy.csv", CopyCfg.class);
		if (readCsv2Map != null && !readCsv2Map.isEmpty()) {
			for (Entry<String, CopyCfg> e : readCsv2Map.entrySet()) {
				e.getValue().initCfg();
			}

			cfgCacheMap = readCsv2Map;
		}

		return cfgCacheMap;
	}

	public CopyCfg getCfg(int id) {
		return (CopyCfg) getCfgById(String.valueOf(id));
	}
}