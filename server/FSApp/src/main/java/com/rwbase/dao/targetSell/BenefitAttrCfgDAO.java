package com.rwbase.dao.targetSell;

import io.netty.util.collection.IntObjectHashMap;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class BenefitAttrCfgDAO extends CfgCsvDao<BenefitAttrCfg> {

	public static BenefitAttrCfgDAO getInstance() {
		return SpringContextUtil.getBean(BenefitAttrCfgDAO.class);
	}

	private IntObjectHashMap<IntObjectHashMap<BenefitAttrCfg>> paramCfgMapping;

	@Override
	protected Map<String, BenefitAttrCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("BenefitAttrCfg/BenefitAttrCfg.csv", BenefitAttrCfg.class);
		IntObjectHashMap<IntObjectHashMap<BenefitAttrCfg>> paramCfgMappingCopy = new IntObjectHashMap<IntObjectHashMap<BenefitAttrCfg>>();
		for (Iterator<Entry<String, BenefitAttrCfg>> iterator = cfgCacheMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, BenefitAttrCfg> entry = iterator.next();
			BenefitAttrCfg value = entry.getValue();
			String param = value.getParam();
			if (param == null || param.isEmpty()) {
				continue;
			}
			int paramValue = Integer.parseInt(param);
			IntObjectHashMap<BenefitAttrCfg> processMap = paramCfgMappingCopy.get(paramValue);
			if (processMap == null) {
				processMap = new IntObjectHashMap<BenefitAttrCfg>();
				paramCfgMappingCopy.put(paramValue, processMap);
			}
			int processType = value.getProcessType();
			BenefitAttrCfg cfg = processMap.get(processType);
			if (cfg != null) {
				throw new ExceptionInInitializerError("duplicate type=" + paramValue + ",processType" + processType);
			}
			processMap.put(processType, value);
		}
		this.paramCfgMapping = paramCfgMappingCopy;
		return cfgCacheMap;
	}

	public BenefitAttrCfg getCfgByHeroModelIdAndProcessType(int param, int processType) {
		// for (Iterator<Entry<String, BenefitAttrCfg>> iterator = cfgCacheMap.entrySet().iterator(); iterator.hasNext();) {
		// Entry<String, BenefitAttrCfg> entry = iterator.next();
		// BenefitAttrCfg value = entry.getValue();
		// int heroModelID = Integer.parseInt(value.getParam());
		// if (heroModelID == param && value.getProcessType() == processType) {
		// return value;
		// }
		// }
		IntObjectHashMap<BenefitAttrCfg> map = paramCfgMapping.get(param);
		if (map == null) {
			return null;
		}
		return map.get(processType);
	}
}
