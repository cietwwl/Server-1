package com.playerdata.mgcsecret.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class MagicChapterCfgDAO extends CfgCsvDao<MagicChapterCfg> {
	public static MagicChapterCfgDAO getInstance(){
		return SpringContextUtil.getBean(MagicChapterCfgDAO.class);
	}
	
	@Override
	public Map<String, MagicChapterCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("magicSecret/magicChapterCfg.csv", MagicChapterCfg.class);
		return cfgCacheMap;
	}
}
