package com.rwbase.dao.fetters.pojo.cfg.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.fetters.pojo.cfg.FettersBaseCfg;
import com.rwbase.dao.fetters.pojo.cfg.template.FettersBaseTemplate;

/*
 * @author HC
 * @date 2016年4月27日 下午3:15:19
 * @Description 
 */
public class FettersBaseCfgDAO extends CfgCsvDao<FettersBaseCfg> {

	public static FettersBaseCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(FettersBaseCfgDAO.class);
	}

	/** 羁绊基础摸吧的List */
	private List<FettersBaseTemplate> fettersBaseTemplateList = new ArrayList<FettersBaseTemplate>();
	/** 羁绊基础的模版Map */
	private Map<Integer, FettersBaseTemplate> fettersBaseTemplateMap = new HashMap<Integer, FettersBaseTemplate>();
	/** 英雄ModelId对应的羁绊Id列表 */
	private Map<Integer, List<Integer>> hero4FettersIdMap = new HashMap<Integer, List<Integer>>();

	@Override
	protected Map<String, FettersBaseCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("HeroFetters/FettersBaseCfg.csv", FettersBaseCfg.class);

		if (cfgCacheMap != null && !cfgCacheMap.isEmpty()) {
			int size = cfgCacheMap.size();
			Map<Integer, FettersBaseTemplate> fettersBaseTemplateMap = new HashMap<Integer, FettersBaseTemplate>(size);
			Map<Integer, List<Integer>> hero4FettersIdMap = new HashMap<Integer, List<Integer>>(size);

			for (Entry<String, FettersBaseCfg> e : cfgCacheMap.entrySet()) {
				FettersBaseCfg cfg = e.getValue();
				if (cfg == null) {
					continue;
				}

				int fettersId = cfg.getFettersId();
				fettersBaseTemplateMap.put(fettersId, new FettersBaseTemplate(cfg));

				int heroModelId = cfg.getHeroModelId();
				List<Integer> list = hero4FettersIdMap.get(heroModelId);
				if (list == null) {
					list = new ArrayList<Integer>();
					hero4FettersIdMap.put(heroModelId, list);
				}

				list.add(fettersId);
			}

			this.fettersBaseTemplateMap = fettersBaseTemplateMap;
			this.fettersBaseTemplateList = Collections.unmodifiableList(new ArrayList<FettersBaseTemplate>(fettersBaseTemplateMap.values()));

			Map<Integer, List<Integer>> unmodifyHero4FettersIdMap = new HashMap<Integer, List<Integer>>(size);
			for (Entry<Integer, List<Integer>> e : hero4FettersIdMap.entrySet()) {
				unmodifyHero4FettersIdMap.put(e.getKey(), new ArrayList<Integer>(e.getValue()));
			}
			this.hero4FettersIdMap = unmodifyHero4FettersIdMap;
		}

		return cfgCacheMap;
	}

	/**
	 * 获取羁绊的配置
	 * 
	 * @param fettersId
	 * @return
	 */
	public FettersBaseTemplate getFettersBaseTemplateById(int fettersId) {
		return fettersBaseTemplateMap.get(fettersId);
	}

	/**
	 * 获取羁绊的List
	 * 
	 * @return
	 */
	public List<FettersBaseTemplate> getFettersBaseTemplateList() {
		return fettersBaseTemplateList;
	}

	/**
	 * 通过heroModelId获取羁绊的列表
	 * 
	 * @param heroModelId
	 * @return
	 */
	public List<FettersBaseTemplate> getFettersBaseTemplateListByHeroModelId(int heroModelId) {
		List<Integer> list = this.hero4FettersIdMap.get(heroModelId);
		if (list == null || list.isEmpty()) {
			return Collections.emptyList();
		}

		int size = list.size();
		List<FettersBaseTemplate> baseTmpList = new ArrayList<FettersBaseTemplate>(size);

		for (int i = 0; i < size; i++) {
			FettersBaseTemplate baseTmp = this.fettersBaseTemplateMap.get(list.get(i));
			if (baseTmp == null) {
				continue;
			}

			baseTmpList.add(baseTmp);
		}

		return baseTmpList;
	}
}