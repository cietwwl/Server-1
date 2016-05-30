package com.playerdata.charge.cfg;

import java.util.List;
import java.util.Map;


import com.playerdata.charge.dao.ChargeInfoSubRecording;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class ChargeCfgDao extends CfgCsvDao<ChargeCfg> {
	public static ChargeCfgDao getInstance() {
		return SpringContextUtil.getBean(ChargeCfgDao.class);
	}
	
	@Override
	public Map<String, ChargeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Charge/ChargeCfg.csv", ChargeCfg.class);

		
		return cfgCacheMap;
	}
	
	public ChargeCfg getConfig(String cfgId){
		ChargeCfg cfg = getCfgById(cfgId);
		return cfg;
	}
	
	
	public ChargeInfoSubRecording newSubItem(String subItemId){		
		ChargeInfoSubRecording target = new ChargeInfoSubRecording();
		target.setId(subItemId);
		target.setCount(0);
		return target;
	}
	

	
	
	
	
}
