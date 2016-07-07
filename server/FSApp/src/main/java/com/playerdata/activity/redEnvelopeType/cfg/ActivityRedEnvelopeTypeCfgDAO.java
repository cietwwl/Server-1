package com.playerdata.activity.redEnvelopeType.cfg;

import java.util.Map;


import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityRedEnvelopeTypeCfgDAO extends CfgCsvDao<ActivityRedEnvelopeTypeCfg> {


	
	public static ActivityRedEnvelopeTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityRedEnvelopeTypeCfgDAO.class);
	}
	
	
	
	
	@Override
	public Map<String, ActivityRedEnvelopeTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityRedEnvelopeTypeCfg.csv", ActivityRedEnvelopeTypeCfg.class);
		for (ActivityRedEnvelopeTypeCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
			
			
		}
		
		return cfgCacheMap;
	}
	
	private void parseTime(ActivityRedEnvelopeTypeCfg cfg){
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getStartTimeStr());
		cfg.setStartTime(startTime);
		
		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getEndTimeStr());
		cfg.setEndTime(endTime);
		
		long getRewardsTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getGetRewardsTimeStr());
		cfg.setGetRewardsTime(getRewardsTime);
		
	}
	
	
	
//	public ActivityRankTypeCfg getConfig(String id){
//		ActivityRankTypeCfg cfg = getCfgById(id);
//		return cfg;
//	}
	
//	public ActivityRankTypeItem newItem(Player player, ActivityRankTypeEnum typeEnum){
//		
//		String cfgId = typeEnum.getCfgId();
//		ActivityRedEnvelopeTypeCfg cfgById = getCfgById(cfgId );
//		if(cfgById!=null){			
//			ActivityRankTypeItem item = new ActivityRankTypeItem();
//			String itemId = ActivityRankTypeHelper.getItemId(player.getUserId(), typeEnum);
//			item.setId(itemId);
//			item.setUserId(player.getUserId());
//			item.setCfgId(cfgId);
//			item.setVersion(cfgById.getVersion());
//			return item;
//		}else{
//			return null;
//		}		
//	}


}