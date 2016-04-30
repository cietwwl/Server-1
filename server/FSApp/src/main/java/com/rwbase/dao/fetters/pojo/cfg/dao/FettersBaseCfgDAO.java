package com.rwbase.dao.fetters.pojo.cfg.dao;

import java.util.HashMap;
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

	/** 羁绊基础的模版Map */
	private Map<Integer, FettersBaseTemplate> fettersBaseTemplateMap = new HashMap<Integer, FettersBaseTemplate>();

	@Override
	protected Map<String, FettersBaseCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fetters/FettersBaseCfg.csv", FettersBaseCfg.class);

		if (cfgCacheMap != null && !cfgCacheMap.isEmpty()) {
			Map<Integer, FettersBaseTemplate> fettersBaseTemplateMap = new HashMap<Integer, FettersBaseTemplate>(cfgCacheMap.size());

			for (Entry<String, FettersBaseCfg> e : cfgCacheMap.entrySet()) {
				FettersBaseCfg cfg = e.getValue();
				if (cfg == null) {
					continue;
				}

				fettersBaseTemplateMap.put(cfg.getFettersId(), new FettersBaseTemplate(cfg));
			}

			this.fettersBaseTemplateMap = fettersBaseTemplateMap;
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
}