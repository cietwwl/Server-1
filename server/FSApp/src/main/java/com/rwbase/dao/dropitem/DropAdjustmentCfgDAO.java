package com.rwbase.dao.dropitem;

import java.util.HashMap;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.copypve.CopyLevelCfgDAO;

public class DropAdjustmentCfgDAO extends CfgCsvDao<DropAdjustmentCfg>{
	
	public static DropAdjustmentCfgDAO getInstance() {
		return SpringContextUtil.getBean(DropAdjustmentCfgDAO.class);
	}
	
	private HashMap<Integer, DropAdjustmentCfg> map;
	
	@Override
	public Map<String, DropAdjustmentCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("copy/DropAdjustment.csv",DropAdjustmentCfg.class);
		HashMap<Integer, DropAdjustmentCfg> map = new HashMap<Integer, DropAdjustmentCfg>();
		for(Object value:cfgCacheMap.values()){
			DropAdjustmentCfg cfg = (DropAdjustmentCfg)value;
			map.put(cfg.getDropRecordId(), cfg);
		}
		this.map = map;
		return cfgCacheMap;
	}

	public DropAdjustmentCfg getDropAdjustment(int dropRecordId){
		if(map == null){
			initJsonCfg();
		}
		//这里不判空
		return map.get(dropRecordId);
	}
}
