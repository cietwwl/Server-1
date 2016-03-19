package com.rwbase.dao.item;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.item.pojo.SpecialItemCfg;

/*
 * @author HC
 * @date 2016年1月20日 下午8:09:58
 * @Description 特殊物品的DAO
 */
public class SpecialItemCfgDAO extends CfgCsvDao<SpecialItemCfg> {
	private static SpecialItemCfgDAO dao = new SpecialItemCfgDAO();

	public static SpecialItemCfgDAO getDAO() {
		return dao;
	}

	@Override
	public Map<String, SpecialItemCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("item/SpecialItem.csv", SpecialItemCfg.class);
		return cfgCacheMap;
	}
}