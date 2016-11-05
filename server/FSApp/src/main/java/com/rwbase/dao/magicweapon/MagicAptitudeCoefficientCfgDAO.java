package com.rwbase.dao.magicweapon;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.magicweapon.pojo.MagicAptitudeCoefficientCfg;

public class MagicAptitudeCoefficientCfgDAO extends CfgCsvDao<MagicAptitudeCoefficientCfg> {

	public static final MagicAptitudeCoefficientCfgDAO getInstance() {
		return SpringContextUtil.getBean(MagicAptitudeCoefficientCfgDAO.class);
	}
	
	@Override
	protected Map<String, MagicAptitudeCoefficientCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map("MagicWeapon/MagicAptitudeCoefficient.csv", MagicAptitudeCoefficientCfg.class);
		return cfgCacheMap;
	}

}
