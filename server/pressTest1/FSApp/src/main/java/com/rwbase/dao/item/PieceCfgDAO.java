package com.rwbase.dao.item;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.item.pojo.PieceCfg;

public class PieceCfgDAO extends CfgCsvDao<PieceCfg>{
	private static PieceCfgDAO instance = new PieceCfgDAO();
	private PieceCfgDAO(){}
	public static PieceCfgDAO getInstance(){
		return instance;
	}

	@Override
	public Map<String, PieceCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("item/Piece.csv",PieceCfg.class);
		return cfgCacheMap;
	}

}
