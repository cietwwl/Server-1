package com.playerdata.activity.redEnvelopeType.cfg;


import java.util.HashMap;
import java.util.List;
import java.util.Map;





import com.playerdata.activity.ActivityTypeHelper;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;


public final class ActivityRedEnvelopeTypeSubCfgDAO extends CfgCsvDao<ActivityRedEnvelopeTypeSubCfg> {


	public static ActivityRedEnvelopeTypeSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityRedEnvelopeTypeSubCfgDAO.class);
	}

	private HashMap<String,List<ActivityRedEnvelopeTypeSubCfg>> subCfgListMap;
	
	@Override
	public Map<String, ActivityRedEnvelopeTypeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityRedEnvelopeTypeSubCfg.csv", ActivityRedEnvelopeTypeSubCfg.class);
		HashMap<String,List<ActivityRedEnvelopeTypeSubCfg>> subCfgListMapTmp = new HashMap<String, List<ActivityRedEnvelopeTypeSubCfg>>();
		for(ActivityRedEnvelopeTypeSubCfg subCfg : cfgCacheMap.values()){
			ActivityTypeHelper.add(subCfg, subCfg.getParantid(), subCfgListMapTmp);
			
		}
		this.subCfgListMap = subCfgListMapTmp;
		return cfgCacheMap;
	}
	
	public List<ActivityRedEnvelopeTypeSubCfg> getSubCfgListByParentID(String parentId){
		return subCfgListMap.get(parentId);
	}


}