package com.rwbase.dao.openLevelTiggerService;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.openLevelLimit.pojo.CfgOpenLevelLimit;
import com.rwbase.dao.openLevelTiggerService.pojo.CfgOpenLevelTiggerService;
import com.rwproto.MsgDef.Command;

public class CfgOpenLevelTiggerServiceDAO extends CfgCsvDao<CfgOpenLevelTiggerService>{
//			 CfgOpenLevelTiggerServiceDAO
	public static CfgOpenLevelTiggerServiceDAO getInstance(){
		return SpringContextUtil.getBean(CfgOpenLevelTiggerServiceDAO.class);
	}
	
	private Map<Integer, List<CfgOpenLevelTiggerService>> mapOfType ;//= new Map<Integer, CfgOpenLevelTiggerService>();
	@Override
	protected Map<String, CfgOpenLevelTiggerService> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("openLevelLimit/triggerServiceByLevel.csv", CfgOpenLevelTiggerService.class);
		Map<Integer, List<CfgOpenLevelTiggerService>> mapTmp = new HashMap<Integer, List<CfgOpenLevelTiggerService>>();
		Set<Entry<String, CfgOpenLevelTiggerService>> entryLst = cfgCacheMap.entrySet();
		for (Entry<String, CfgOpenLevelTiggerService> entry : entryLst) {
			CfgOpenLevelTiggerService cfg = entry.getValue();
			int type = cfg.getType();
			List<CfgOpenLevelTiggerService> old = mapTmp.get(type);
			if (old == null){
				old = new ArrayList<CfgOpenLevelTiggerService>();
				mapTmp.put(type, old);
			}
			old.add(cfg);
		}
		mapOfType = mapTmp;		
		return cfgCacheMap;
	}
	
	public List<CfgOpenLevelTiggerService> getListByType(Integer type){
		return mapOfType.get(type);
	}
	
}
