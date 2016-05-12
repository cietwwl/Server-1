//package com.rwbase.dao.fetters.pojo.cfg.dao;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import com.rw.fsutil.cacheDao.CfgCsvDao;
//import com.rw.fsutil.util.SpringContextUtil;
//import com.rwbase.common.config.CfgCsvHelper;
//import com.rwbase.dao.fetters.pojo.cfg.FettersSubConditionCfg;
//import com.rwbase.dao.fetters.pojo.cfg.template.FettersSubConditionTemplate;
//
///*
// * @author HC
// * @date 2016年4月27日 下午3:41:14
// * @Description 
// */
//public class FettersSubConditionCfgDAO extends CfgCsvDao<FettersSubConditionCfg> {
//
//	public static FettersSubConditionCfgDAO getCfgDAO() {
//		return SpringContextUtil.getBean(FettersSubConditionCfgDAO.class);
//	}
//
//	/** 羁绊子条件 */
//	private Map<Integer, FettersSubConditionTemplate> fettersSubConditionMap = new HashMap<Integer, FettersSubConditionTemplate>();
//
//	@Override
//	protected Map<String, FettersSubConditionCfg> initJsonCfg() {
//		cfgCacheMap = CfgCsvHelper.readCsv2Map("fetters/FettersSubConditionCfg.csv", FettersSubConditionCfg.class);
//
//		if (cfgCacheMap != null && !cfgCacheMap.isEmpty()) {
//			Map<Integer, FettersSubConditionTemplate> fettersSubConditionMap = new HashMap<Integer, FettersSubConditionTemplate>();
//
//			for (Entry<String, FettersSubConditionCfg> e : cfgCacheMap.entrySet()) {
//				FettersSubConditionCfg cfg = e.getValue();
//				if (cfg == null) {
//					continue;
//				}
//
//				fettersSubConditionMap.put(cfg.getSubConditionId(), new FettersSubConditionTemplate(cfg));
//			}
//
//			this.fettersSubConditionMap = fettersSubConditionMap;
//		}
//
//		return cfgCacheMap;
//	}
//
//	/**
//	 * 通过子条件Id获取到子条件模版
//	 * 
//	 * @param subConditionId
//	 * @return
//	 */
//	public FettersSubConditionTemplate getFettersSubConditionTemplateById(int subConditionId) {
//		return this.fettersSubConditionMap.get(subConditionId);
//	}
// }