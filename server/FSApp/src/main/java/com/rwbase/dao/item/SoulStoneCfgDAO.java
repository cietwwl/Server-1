package com.rwbase.dao.item;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.item.pojo.SoulStoneCfg;

public class SoulStoneCfgDAO  extends CfgCsvDao<SoulStoneCfg>{
	private static SoulStoneCfgDAO instance = new SoulStoneCfgDAO();
	private SoulStoneCfgDAO(){}
	public static SoulStoneCfgDAO getInstance(){
		return instance;
	}

	@Override
	public Map<String, SoulStoneCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("item/SoulStone.csv",SoulStoneCfg.class);
		return cfgCacheMap;
	}
}
