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
import com.rwbase.dao.fetters.pojo.cfg.HeroFettersCfg;
import com.rwbase.dao.fetters.pojo.cfg.template.HeroFettersTemplate;

/*
 * @author HC
 * @date 2016年4月27日 下午2:39:29
 * @Description 
 */
public class HeroFettersCfgDAO extends CfgCsvDao<HeroFettersCfg> {

	public static HeroFettersCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(HeroFettersCfgDAO.class);
	}

	private List<HeroFettersTemplate> templateList = new ArrayList<HeroFettersTemplate>();// 模版表集合
	private Map<Integer, HeroFettersTemplate> templateMap = new HashMap<Integer, HeroFettersTemplate>();// 模版表

	@Override
	protected Map<String, HeroFettersCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fetters/HeroFettersCfg.csv", HeroFettersCfg.class);

		if (cfgCacheMap != null && !cfgCacheMap.isEmpty()) {
			Map<Integer, HeroFettersTemplate> templateMap = new HashMap<Integer, HeroFettersTemplate>(cfgCacheMap.size());// 模版表
			List<HeroFettersTemplate> templateList = new ArrayList<HeroFettersTemplate>();// 模版表集合

			for (Entry<String, HeroFettersCfg> e : cfgCacheMap.entrySet()) {
				HeroFettersCfg value = e.getValue();
				if (value == null) {
					continue;
				}

				HeroFettersTemplate tmp = new HeroFettersTemplate(value);
				templateMap.put(value.getHeroModelId(), tmp);

				templateList.add(tmp);
			}

			this.templateMap = templateMap;
			this.templateList = Collections.unmodifiableList(templateList);
		}

		return cfgCacheMap;
	}

	/**
	 * 通过英雄的ModelId获取英雄的羁绊
	 * 
	 * @param heroModelId
	 * @return
	 */
	public HeroFettersTemplate getHeroFettersTemplateByModelId(int heroModelId) {
		return this.templateMap.get(heroModelId);
	}

	/**
	 * 获取只读的模版列表
	 * 
	 * @return
	 */
	public List<HeroFettersTemplate> getHeroFettersTemplateList() {
		return this.templateList;
	}
}