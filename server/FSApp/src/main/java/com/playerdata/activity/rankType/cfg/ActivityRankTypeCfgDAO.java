package com.playerdata.activity.rankType.cfg;

import java.util.Map;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.rankType.ActivityRankTypeEnum;
import com.playerdata.activity.rankType.ActivityRankTypeHelper;
import com.playerdata.activity.rankType.data.ActivityRankTypeItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityRankTypeCfgDAO extends CfgCsvDao<ActivityRankTypeCfg> {


	
	public static ActivityRankTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityRankTypeCfgDAO.class);
	}
	
	
	
	
	@Override
	public Map<String, ActivityRankTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityRankTypeCfg.csv", ActivityRankTypeCfg.class);
		for (ActivityRankTypeCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
			
			
		}
		
		return cfgCacheMap;
	}
	
	private void parseTime(ActivityRankTypeCfg cfg){
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getStartTimeStr());
		cfg.setStartTime(startTime);
		
		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getEndTimeStr());
		cfg.setEndTime(endTime);		
	}
	
	
	
//	public ActivityRankTypeCfg getConfig(String id){
//		ActivityRankTypeCfg cfg = getCfgById(id);
//		return cfg;
//	}
//	
	public ActivityRankTypeItem newItem(Player player, ActivityRankTypeEnum typeEnum){
		
		String cfgId = typeEnum.getCfgId();
		ActivityRankTypeCfg cfgById = getCfgById(cfgId );
		if(cfgById!=null){			
			ActivityRankTypeItem item = new ActivityRankTypeItem();
			String itemId = ActivityRankTypeHelper.getItemId(player.getUserId(), typeEnum);
			item.setId(itemId);
			item.setUserId(player.getUserId());
			item.setCfgId(cfgId);
			item.setVersion(cfgById.getVersion());
			return item;
		}else{
			return null;
		}
		
	}


}