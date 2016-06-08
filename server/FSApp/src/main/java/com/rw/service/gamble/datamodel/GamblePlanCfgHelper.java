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
			}else{
				GamblePlanCfg other = lst.get(0);
				if (other.getMoneyNum() != cfg.getMoneyNum() || other.getMoneyType() != cfg.getMoneyType()){
					throw new RuntimeException("钓鱼台配置有错，相同类型的抽卡类型配置的货币类型或者金额必须一致！"+"关键字:"+other.getKey()+",另一个关键字"+cfg.getKey());
				}
			}
			lst.add(cfg);
		}
		
		//TODO maxCheckCount,GamblePlanCfg 考虑同类型的配置
		//TODO 检查配置：唯一性的存在可能的判断
		//TODO 跨表检查物品/英雄是否存在，然后寻找合适的默认保底容错英雄或物品

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