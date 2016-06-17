package com.playerdata.activity.rateType.cfg;

import java.util.Map;

import com.playerdata.Player;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.activity.rateType.ActivityRateTypeEnum;
import com.playerdata.activity.rateType.ActivityRateTypeHelper;
import com.playerdata.activity.rateType.data.ActivityRateTypeItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityRateTypeCfgDAO extends CfgCsvDao<ActivityRateTypeCfg> {


	public static ActivityRateTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityRateTypeCfgDAO.class);
	}

	
	@Override
	public Map<String, ActivityRateTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityRateTypeCfg.csv", ActivityRateTypeCfg.class);
		for (ActivityRateTypeCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}
		return cfgCacheMap;
	}
	
	private void parseTime(ActivityRateTypeCfg cfgItem){
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getStartTimeStr());
		cfgItem.setStartTime(startTime);
		
		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getEndTimeStr());
		cfgItem.setEndTime(endTime);		
	}
	
	public ActivityRateTypeCfg getConfig(String id){
		ActivityRateTypeCfg cfg = getCfgById(id);
		return cfg;
	}
	
	public ActivityRateTypeItem newItem(Player player, ActivityRateTypeEnum typeEnum){
		
		String cfgId = typeEnum.getCfgId();
		ActivityRateTypeCfg cfgById = getCfgById(cfgId );
		if(cfgById!=null){			
			ActivityRateTypeItem item = new ActivityRateTypeItem();
			String itemId = ActivityRateTypeHelper.getItemId(player.getUserId(), typeEnum);
			item.setId(itemId);
			item.setCfgId(cfgId);
			item.setUserId(player.getUserId());	
			item.setVersion(cfgById.getVersion());
			return item;
		}else{
			return null;
		}
		
	}



}