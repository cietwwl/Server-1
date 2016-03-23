package com.rwbase.dao.item;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.item.pojo.MagicCfg;

public class MagicCfgDAO extends CfgCsvDao<MagicCfg> {
	public static MagicCfgDAO getInstance() {
		return SpringContextUtil.getBean(MagicCfgDAO.class);
	}

	@Override
	public Map<String, MagicCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("item/Magic.csv",MagicCfg.class);
		return cfgCacheMap;
	}
}
