package com.rwbase.dao.targetSell;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class BenefitAttrCfgDAO extends CfgCsvDao<BenefitAttrCfg>{

	public static BenefitAttrCfgDAO getInstance(){
		return SpringContextUtil.getBean(BenefitAttrCfgDAO.class);
	}
	
	@Override
	protected Map<String, BenefitAttrCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("BenefitAttrCfg/BenefitAttrCfg.csv",BenefitAttrCfg.class);
		return cfgCacheMap;
	}
	
	public BenefitAttrCfg getCfgByHeroModelIdAndProcessType(int modelId, int processType){
		for (Iterator<Entry<String, BenefitAttrCfg>> iterator = cfgCacheMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, BenefitAttrCfg> entry = iterator.next();
			BenefitAttrCfg value = entry.getValue();
			if(value.getHeroModelId() == modelId && value.getProcessType() == processType){
				return value;
			}
		}
		return null;
	}
}
