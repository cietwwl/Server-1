package com.rwbase.dao.magicweapon;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.magicweapon.pojo.MagicAptitudeCfg;

public class MagicAptitudeCfgDAO extends CfgCsvDao<MagicAptitudeCfg>{

	public static MagicAptitudeCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(MagicAptitudeCfgDAO.class);
	}
	
	@Override
	protected Map<String, MagicAptitudeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("MagicWeapon/MagicAptitude.csv", MagicAptitudeCfg.class);
		for (Iterator<Entry<String, MagicAptitudeCfg> > iterator = cfgCacheMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, MagicAptitudeCfg> entry = iterator.next();
			MagicAptitudeCfg cfg = entry.getValue();
			cfg.initData();
		}
		return cfgCacheMap;
	}

}
