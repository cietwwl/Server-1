package com.rwbase.dao.magicweapon;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.item.SoulStoneCfgDAO;
import com.rwbase.dao.magicweapon.pojo.MagicExpCfg;

public class MagicExpCfgDAO extends CfgCsvDao<MagicExpCfg> {

	public static MagicExpCfgDAO getInstance() {
		return SpringContextUtil.getBean(MagicExpCfgDAO.class);
	}
	@Override
	public Map<String, MagicExpCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("MagicWeapon/MagicExp.csv", MagicExpCfg.class);
		return cfgCacheMap;
	}

}
