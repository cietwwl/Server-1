package com.rwbase.dao.mainmsg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.magicweapon.MagicSmeltCfgDAO;


public class CfgPmdDAO extends CfgCsvDao<PmdCfg> {
	public static CfgPmdDAO getInstance() {
		return SpringContextUtil.getBean(CfgPmdDAO.class);
	}
	
	public Map<String, PmdCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("mainmsg/PmdCfg.csv",PmdCfg.class);
		return cfgCacheMap;
	}
	
	/**根据子类ID获取相应数据*/
	public PmdCfg getCfg(int id){
		return (PmdCfg)getCfgById(String.valueOf(id));
	}
}
