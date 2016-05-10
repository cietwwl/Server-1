package com.rw.service.TaoistMagic.datamodel;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attrdata.AttrDataIF;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.service.TaoistMagic.datamodel.TaoistMagicCfgHelper"  init-method="init" />

public class TaoistMagicCfgHelper extends CfgCsvDao<TaoistMagicCfg> {
	public static TaoistMagicCfgHelper getInstance() {
		return SpringContextUtil.getBean(TaoistMagicCfgHelper.class);
	}


	public AttrDataIF getEffect(int skillId,int level){
		TaoistMagicCfg cfg = cfgCacheMap.get(String.valueOf(skillId));
		AttrData attr = new AttrData();
		Field field = attrMap.get(cfg.getAttribute());
		try {
			field.set(attr, cfg.getMagicValue(level));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return attr;
	}
	private Map<String, Field> attrMap;
	
	@Override
	public Map<String, TaoistMagicCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("TaoistMagic/TaoistMagicCfg.csv", TaoistMagicCfg.class);
		attrMap = CfgCsvHelper.getFieldMap(AttrData.class);
		Collection<TaoistMagicCfg> vals = cfgCacheMap.values();
		HashMap<Integer, Integer> tagMap = new HashMap<Integer, Integer>();
		HashMap<Integer, HashSet<Integer>> orderMap = new HashMap<Integer, HashSet<Integer>>();

		for (TaoistMagicCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
			// 检查属性是否存在, 每个分页的开放等级必须一样,序号应该连续且没有重复
			String attribute = cfg.getAttribute();
			if (!attrMap.containsKey(attribute)){
				throw new RuntimeException("无效属性名:"+attribute);
			}
			int tagNum = cfg.getTagNum();
			int openLevel = cfg.getOpenLevel();
			Integer oldTagCfg = tagMap.get(tagNum);
			if (oldTagCfg == null) {
				tagMap.put(tagNum, openLevel);
			} else {
				if (oldTagCfg != openLevel) {
					throw new RuntimeException("同一个分页的道术技能应该是同一个开放等级！" + "key=" + cfg.getKey());
				}
			}

			HashSet<Integer> orderSet = orderMap.get(tagNum);
			if (orderSet == null) {
				orderSet = new HashSet<>();
				orderMap.put(tagNum, orderSet);
			}
			if (!orderSet.add(cfg.getOrder())) {
				throw new RuntimeException("重复的道术排列序号" + "key=" + cfg.getKey());
			}
		}

		Set<Entry<Integer, HashSet<Integer>>> orderEntrySet = orderMap.entrySet();
		for (Entry<Integer, HashSet<Integer>> entry : orderEntrySet) {
			HashSet<Integer> orderSet = entry.getValue();
			int size = orderSet.size();
			for (int i = 1; i <= size; i++) {
				if (!orderSet.contains(i)) {
					throw new RuntimeException("分页:"+entry.getKey()+",缺少道术排列序号" + i);
				}
			}
		}
		return cfgCacheMap;
	}

	@Override
	public void CheckConfig() {
		// 跨表检查，consumeId是否在TaoistConsumeCfg有定义
		TaoistConsumeCfgHelper helper = TaoistConsumeCfgHelper.getInstance();
		Collection<TaoistMagicCfg> vals = cfgCacheMap.values();
		for (TaoistMagicCfg cfg : vals) {
			int consumeId = cfg.getConsumeId();
			TaoistConsumeCfg consumeCfg = helper.getCfgById(String.valueOf(consumeId));
			if (consumeCfg == null) {
				throw new RuntimeException("无效技能消耗ID=" + consumeId);
			}
		}
	}
}
