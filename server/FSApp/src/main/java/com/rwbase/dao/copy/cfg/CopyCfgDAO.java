package com.rwbase.dao.copy.cfg;

import java.util.HashMap;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.JsonCfgTransfer;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class CopyCfgDAO extends CfgCsvDao<CopyCfg>{

	public static CopyCfgDAO getInstance() {
		return SpringContextUtil.getBean(CopyCfgDAO.class);
	}
	
	private HashMap<Integer, CopyCfg> map;
	
	@Override
	public Map<String, CopyCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("copy/copy.csv",CopyCfg.class);
		HashMap<Integer, CopyCfg> map = new HashMap<Integer, CopyCfg>();
		for(Map.Entry<String, CopyCfg> entry:cfgCacheMap.entrySet()){
			map.put(Integer.valueOf(entry.getKey()), entry.getValue());
		}
		this.map = map;
		return cfgCacheMap;
	}
	public CopyCfg getCfg(int id){
//		return (CopyCfg)getCfgById(String.valueOf(id));
		return this.map.get(id);
	}
}
