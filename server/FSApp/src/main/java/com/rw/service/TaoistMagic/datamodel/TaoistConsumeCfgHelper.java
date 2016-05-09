package com.rw.service.TaoistMagic.datamodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.service.TaoistMagic.datamodel.TaoistConsumeCfgHelper"  init-method="init" />

public class TaoistConsumeCfgHelper extends CfgCsvDao<TaoistConsumeCfg> {
	public static TaoistConsumeCfgHelper getInstance() {
		return SpringContextUtil.getBean(TaoistConsumeCfgHelper.class);
	}

	private HashMap<Pair<Integer,Integer>,TaoistConsumeCfg> consumePlanMap;
	private HashMap<Integer,Integer> consumeMaxLevelMap;
	
	@Override
	public Map<String, TaoistConsumeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("TaoistMagic/TaoistConsumeCfg.csv",TaoistConsumeCfg.class);
		Collection<TaoistConsumeCfg> vals = cfgCacheMap.values();
		consumeMaxLevelMap = new HashMap<>();
		consumePlanMap = new HashMap<>(vals.size());
		for (TaoistConsumeCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
			int consumePlanId = cfg.getConsumeId();
			int level = cfg.getSkillLevel();
			Pair<Integer,Integer> pair = Pair.Create(consumePlanId, level);
			TaoistConsumeCfg oldPair = consumePlanMap.put(pair, cfg);
			if (oldPair != null){
				throw new RuntimeException("重复的技能消耗ID＋技能等级组合:"+"id="+consumePlanId+",level="+level);
			}
			Integer maxLvl = consumeMaxLevelMap.get(consumePlanId);
			if (maxLvl == null || maxLvl < level){
				maxLvl = level;
				consumeMaxLevelMap.put(consumePlanId, maxLvl);
			}
		}
		//检查技能等级是否连续
		Set<Entry<Integer, Integer>> idlvlmap = consumeMaxLevelMap.entrySet();
		for (Entry<Integer, Integer> entry : idlvlmap) {
			int maxLvl = entry.getValue();
			int consumePlanId = entry.getKey();
			for(int i = 1;i<=maxLvl;i++){
				Pair<Integer,Integer> pair = Pair.Create(consumePlanId, i);
				TaoistConsumeCfg cfg = consumePlanMap.get(pair);
				if (cfg == null){
					throw new RuntimeException("技能消耗ID="+consumePlanId+"缺少配置等级:"+i);
				}
			}
		}
		return cfgCacheMap;
	}
	
	@Override
	public void CheckConfig(){
		//跨表检查：暴击组合序列是否存在TaoistCriticalPlanCfg
		TaoistCriticalPlanCfgHelper helper = TaoistCriticalPlanCfgHelper.getInstance();
		Collection<TaoistConsumeCfg> vals = cfgCacheMap.values();
		for (TaoistConsumeCfg cfg : vals) {
			int[] critSeqs = cfg.getSeqList();
			for (int i = 0; i < critSeqs.length; i++) {
				int critPlanId =  critSeqs[i];
				TaoistCriticalPlanCfg planCfg = helper.getCfgById(String.valueOf(critPlanId));
				if (planCfg == null){
					throw new RuntimeException("无效暴击方案Id="+critPlanId);
				}
			}
		}
	}
}
