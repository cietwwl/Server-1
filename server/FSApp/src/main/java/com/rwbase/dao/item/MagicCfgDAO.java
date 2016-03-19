package com.rwbase.dao.item;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.item.pojo.MagicCfg;

public class MagicCfgDAO extends CfgCsvDao<MagicCfg> {
	private static MagicCfgDAO instance = new MagicCfgDAO();
	private MagicCfgDAO(){}
	public static MagicCfgDAO getInstance(){
		return instance;
	}

	@Override
	public Map<String, MagicCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("item/Magic.csv",MagicCfg.class);
		Collection<MagicCfg> vals = cfgCacheMap.values();
		for (MagicCfg magicCfg : vals) {
			magicCfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
	// TODO 所有表加载完成后，应该检查decomposeGoodList里面的模板ID是否存在于Consume表
}
