package com.playerdata.activity.rankType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;


public final class ActivityRankTypeSubCfgDAO extends CfgCsvDao<ActivityRankTypeSubCfg> {


	public static ActivityRankTypeSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityRankTypeSubCfgDAO.class);
	}

	
	@Override
	public Map<String, ActivityRankTypeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityRankTypeSubCfg.csv", ActivityRankTypeSubCfg.class);
		for (ActivityRankTypeSubCfg cfgTmp : cfgCacheMap.values()) {
			parSetRankRange(cfgTmp);
		}
		
		return cfgCacheMap;
	}
	
	private void parSetRankRange(ActivityRankTypeSubCfg cfg){
		String[] strs = cfg.getRankRange().split("_");
		cfg.getRankRanges()[0] = Integer.parseInt(strs[0]);
		if(strs.length == 2){
			cfg.getRankRanges()[1] = Integer.parseInt(strs[1]);
		}else if(strs.length == 1){
			cfg.getRankRanges()[1] = Integer.parseInt(strs[0]);
		}else{
			GameLog.error(LogModule.ComActivityRank, null, "范围rankrange格式错误", null);
		}
	}

	public List<ActivityRankTypeSubCfg> getByParentCfgId(String parentCfgId){
		List<ActivityRankTypeSubCfg> targetList = new ArrayList<ActivityRankTypeSubCfg>();
		List<ActivityRankTypeSubCfg> allCfg = getAllCfg();
		for (ActivityRankTypeSubCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getParentCfgId(), parentCfgId)){
				targetList.add(tmpItem);
			}
		}
		return targetList;
		
	}
	


}