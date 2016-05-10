package com.rwbase.dao.fetters.pojo.cfg.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.fetters.pojo.cfg.FettersConditionCfg;
import com.rwbase.dao.fetters.pojo.cfg.template.FettersConditionTemplate;

/*
 * @author HC
 * @date 2016年4月27日 下午3:22:24
 * @Description 
 */
public class FettersConditionCfgDAO extends CfgCsvDao<FettersConditionCfg> {
	/** 条件的等级比较器，谁的等级小，谁在前 */
	private static Comparator<FettersConditionTemplate> conditionComparator = new Comparator<FettersConditionTemplate>() {

		@Override
		public int compare(FettersConditionTemplate o1, FettersConditionTemplate o2) {
			return o1.getConditionLevel() - o2.getConditionLevel();
		}
	};

	public static FettersConditionCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(FettersConditionCfgDAO.class);
	}

	private Map<Integer, List<FettersConditionTemplate>> conditionMap = new HashMap<Integer, List<FettersConditionTemplate>>();
	private Map<Integer, FettersConditionTemplate> conditionTmpMap = new HashMap<Integer, FettersConditionTemplate>();

	@Override
	protected Map<String, FettersConditionCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fetters/FettersConditionCfg.csv", FettersConditionCfg.class);

		if (cfgCacheMap != null && !cfgCacheMap.isEmpty()) {
			Map<Integer, List<FettersConditionTemplate>> conditionMap = new HashMap<Integer, List<FettersConditionTemplate>>(cfgCacheMap.size());
			Map<Integer, FettersConditionTemplate> conditionTmpMap = new HashMap<Integer, FettersConditionTemplate>();

			for (Entry<String, FettersConditionCfg> e : cfgCacheMap.entrySet()) {
				FettersConditionCfg cfg = e.getValue();
				if (cfg == null) {
					continue;
				}

				int conditionId = cfg.getConditionId();
				List<FettersConditionTemplate> list = conditionMap.get(conditionId);
				if (list == null) {
					list = new ArrayList<FettersConditionTemplate>();
					conditionMap.put(conditionId, list);
				}

				FettersConditionTemplate tmp = new FettersConditionTemplate(cfg);
				list.add(tmp);

				conditionTmpMap.put(tmp.getUniqueId(), tmp);
			}

			// 然后对列表进行一个排序&设置成只读
			Map<Integer, List<FettersConditionTemplate>> conditionMap1 = new HashMap<Integer, List<FettersConditionTemplate>>(conditionMap.size());
			for (Entry<Integer, List<FettersConditionTemplate>> e : conditionMap.entrySet()) {
				List<FettersConditionTemplate> value = e.getValue();
				if (value == null || value.isEmpty()) {
					continue;
				}

				Collections.sort(value, conditionComparator);

				conditionMap1.put(e.getKey(), Collections.unmodifiableList(value));
			}

			this.conditionMap = conditionMap1;
			this.conditionTmpMap = conditionTmpMap;
		}

		return cfgCacheMap;
	}

	/**
	 * <pre>
	 * 通过条件Id获取羁绊的条件模版
	 * <b>返回的是一个不可修改列表</b>
	 * </pre>
	 * 
	 * @param conditionId
	 * @return
	 */
	public List<FettersConditionTemplate> getFettersConditionListById(int conditionId) {
		return this.conditionMap.get(conditionId);
	}

	/**
	 * 通过唯一Id获取羁绊的条件模版
	 * 
	 * @param uniqueId
	 * @return
	 */
	public FettersConditionTemplate getFettersConditionTemplateByUniqueId(int uniqueId) {
		return this.conditionTmpMap.get(uniqueId);
	}
}