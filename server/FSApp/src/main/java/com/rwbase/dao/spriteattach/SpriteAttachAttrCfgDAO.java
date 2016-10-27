package com.rwbase.dao.spriteattach;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachAttrCfg;

public class SpriteAttachAttrCfgDAO extends CfgCsvDao<SpriteAttachAttrCfg>{

	private static Map<Integer, Map<Integer, SpriteAttachAttrCfg>> SpriteAttachAttrCfgMap;
	
	public static SpriteAttachAttrCfgDAO getInstance() {
		return SpringContextUtil.getBean(SpriteAttachAttrCfgDAO.class);
	}
	
	@Override
	protected Map<String, SpriteAttachAttrCfg> initJsonCfg() {
		// TODO Auto-generated method stub
		cfgCacheMap = CfgCsvHelper.readCsv2Map("SpriteAttach/SpriteAttachAttrCfg.csv",SpriteAttachAttrCfg.class);
		SpriteAttachAttrCfgMap = new HashMap<Integer, Map<Integer,SpriteAttachAttrCfg>>();
		for (Iterator<Entry<String, SpriteAttachAttrCfg>> iterator = cfgCacheMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, SpriteAttachAttrCfg> entry = iterator.next();
			SpriteAttachAttrCfg cfg = entry.getValue();
			cfg.initData();
			if(SpriteAttachAttrCfgMap.containsKey(cfg.getPlanId())){
				Map<Integer, SpriteAttachAttrCfg> map = SpriteAttachAttrCfgMap.get(cfg.getPlanId());
				map.put(cfg.getLevel(), cfg);
			}else{
				Map<Integer, SpriteAttachAttrCfg> map = new HashMap<Integer, SpriteAttachAttrCfg>();
				map.put(cfg.getLevel(), cfg);
				SpriteAttachAttrCfgMap.put(cfg.getPlanId(), map);
			}
			
		}
		
		return cfgCacheMap;
	}

	public SpriteAttachAttrCfg getByPlanIdAndLevel(int planId, int level) {
		Map<Integer, SpriteAttachAttrCfg> map = SpriteAttachAttrCfgMap.get(planId);
		if (map != null) {
			return map.get(level);
		}
		return null;
	}
}
