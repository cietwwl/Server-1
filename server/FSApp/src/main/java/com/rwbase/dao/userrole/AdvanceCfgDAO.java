package com.rwbase.dao.userrole;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.user.LevelCfgDAO;
import com.rwbase.dao.userrole.pojo.AdvanceCfg;

public class AdvanceCfgDAO extends CfgCsvDao<AdvanceCfg> {
	public static AdvanceCfgDAO getInstance() {
		return SpringContextUtil.getBean(AdvanceCfgDAO.class);
	}
	@Override
	public Map<String, AdvanceCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("role/AdvanceCfg.csv",AdvanceCfg.class);
		return cfgCacheMap;
	}
	
	public AdvanceCfg getCfg(String playerId){
		return (AdvanceCfg)getCfgById(playerId);
	}
}
