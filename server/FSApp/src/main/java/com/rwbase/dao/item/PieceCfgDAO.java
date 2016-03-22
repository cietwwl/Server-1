package com.rwbase.dao.item;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.item.pojo.PieceCfg;

public class PieceCfgDAO extends CfgCsvDao<PieceCfg>{
	public static PieceCfgDAO getInstance() {
		return SpringContextUtil.getBean(PieceCfgDAO.class);
	}

	@Override
	public Map<String, PieceCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("item/Piece.csv",PieceCfg.class);
		return cfgCacheMap;
	}

}
