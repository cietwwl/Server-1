package com.playerdata.charge.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class VipGiftCfgDao extends CfgCsvDao<VipGiftCfg> {
	public static VipGiftCfgDao getInstance() {
		return SpringContextUtil.getBean(VipGiftCfgDao.class);
	}
	
	@Override
	public Map<String, VipGiftCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Charge/VipGiftCfg.csv", VipGiftCfg.class);
		return cfgCacheMap;
	}

	
	public VipGiftCfg getByVip(int vipLevel){
		VipGiftCfg cfg = getCfgById(String.valueOf(vipLevel));
		return cfg;
	}
	
	
}
