package com.rwbase.dao.spriteattach;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachRoleCfg;

public class SpriteAttachRoleCfgDAO extends CfgCsvDao<SpriteAttachRoleCfg>{


	public static SpriteAttachRoleCfgDAO getInstance() {
		return SpringContextUtil.getBean(SpriteAttachRoleCfgDAO.class);
	}
	
	@Override
	protected Map<String, SpriteAttachRoleCfg> initJsonCfg() {
		// TODO Auto-generated method stub
		cfgCacheMap = CfgCsvHelper.readCsv2Map("SpriteAttach/SpriteAttachRoleCfg.csv", SpriteAttachRoleCfg.class);
		for (Iterator<Entry<String, SpriteAttachRoleCfg>> iterator = cfgCacheMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, SpriteAttachRoleCfg> entry = iterator.next();
			SpriteAttachRoleCfg cfg = entry.getValue();
			cfg.addIndexMap(cfg.getSpriteItem1(), 1);
			cfg.addIndexMap(cfg.getSpriteItem2(), 2);
			cfg.addIndexMap(cfg.getSpriteItem3(), 3);
			cfg.addIndexMap(cfg.getSpriteItem4(), 4);
			cfg.addIndexMap(cfg.getSpriteItem5(), 5);
			cfg.addIndexMap(cfg.getSpriteItem6(), 6);
			
		}
		return cfgCacheMap;
	}

}
