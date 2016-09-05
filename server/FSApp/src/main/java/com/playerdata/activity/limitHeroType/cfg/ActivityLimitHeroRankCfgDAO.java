package com.playerdata.activity.limitHeroType.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.activity.ActivityTypeHelper;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class ActivityLimitHeroRankCfgDAO extends CfgCsvDao<ActivityLimitHeroRankCfg>{
	
	public static ActivityLimitHeroRankCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityLimitHeroRankCfgDAO.class);
	}
	
	private HashMap<String, List<ActivityLimitHeroRankCfg>> rankCfgListMap ;
	
	@Override
	protected Map<String, ActivityLimitHeroRankCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityLimitHeroRankCfg.csv", ActivityLimitHeroRankCfg.class);
		for (ActivityLimitHeroRankCfg cfgTmp : cfgCacheMap.values()) {
			parSetRankRange(cfgTmp);
		}
		HashMap<String, List<ActivityLimitHeroRankCfg>> rankCfgListMapTmp = new HashMap<String, List<ActivityLimitHeroRankCfg>>();
		for(ActivityLimitHeroRankCfg rankCfg : cfgCacheMap.values()){
			ActivityTypeHelper.add(rankCfg, rankCfg.getParentid(),rankCfgListMapTmp);
		}
		this.rankCfgListMap = rankCfgListMapTmp;
		return cfgCacheMap;
	}
	
	private void parSetRankRange(ActivityLimitHeroRankCfg cfg){
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
	
	public List<ActivityLimitHeroRankCfg> getByParentCfgId(String id) {
		List<ActivityLimitHeroRankCfg> list = rankCfgListMap.get(id);
		if(list == null){
			list = new ArrayList<ActivityLimitHeroRankCfg>();
		}
		return list;
	}
}
