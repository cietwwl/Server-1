package com.rw.service.gamble.datamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
/*
<bean class="com.rw.service.gamble.datamodel.GamblePlanCfgHelper"  init-method="init" />
*/

public class GamblePlanCfgHelper extends CfgCsvDao<GamblePlanCfg> {
	public static GamblePlanCfgHelper getInstance() {
		return SpringContextUtil.getBean(GamblePlanCfgHelper.class);
	}

	private Map<Integer, List<GamblePlanCfg>> typeLevelMapping;
	
	@Override
	public Map<String, GamblePlanCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("gamble/GamblePlanCfg.csv", GamblePlanCfg.class);
		typeLevelMapping = new HashMap<Integer, List<GamblePlanCfg>>();
		Collection<GamblePlanCfg> vals = cfgCacheMap.values();
		for (GamblePlanCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
			List<GamblePlanCfg> lst = typeLevelMapping.get(cfg.getDropType());
			if (lst == null){
				lst = new ArrayList<GamblePlanCfg>();
				typeLevelMapping.put(cfg.getDropType(), lst);
			}
			lst.add(cfg);
		}
		return cfgCacheMap;
	}
	
	public GamblePlanCfg getConfig(int dropType,int level){
		List<GamblePlanCfg> lst = typeLevelMapping.get(dropType);
		if (lst == null) {
			GameLog.error("钓鱼台", "dropType="+dropType, "找不到抽卡类型");
			return null;
		}
		
		for (GamblePlanCfg gamblePlanCfg : lst) {
			if (gamblePlanCfg.inLevelSegment(level)){
				return gamblePlanCfg;
			}
		}
		GameLog.error("钓鱼台", "dropType="+dropType, "找不到等级段,level="+level);
		return null;
	}
}