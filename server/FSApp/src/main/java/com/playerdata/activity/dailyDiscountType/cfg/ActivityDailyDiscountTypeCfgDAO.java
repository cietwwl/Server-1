package com.playerdata.activity.dailyDiscountType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeEnum;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalitySubCfg;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeSubItem;
import com.playerdata.activity.countType.ActivityCountTypeHelper;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeEnum;
import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeHelper;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItem;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeSubItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;


public final class ActivityDailyDiscountTypeCfgDAO extends
		CfgCsvDao<ActivityDailyDiscountTypeCfg> {

	public static ActivityDailyDiscountTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityDailyDiscountTypeCfgDAO.class);
	}

	@Override
	public Map<String, ActivityDailyDiscountTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map(
				"Activity/ActivityDailyDiscountTypeCfg.csv",
				ActivityDailyDiscountTypeCfg.class);
		for (ActivityDailyDiscountTypeCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}
		return cfgCacheMap;
	}

	private void parseTime(ActivityDailyDiscountTypeCfg cfgItem) {
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getStartTimeStr());
		cfgItem.setStartTime(startTime);
		
		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getEndTimeStr());
		cfgItem.setEndTime(endTime);	
	}

	public ActivityDailyDiscountTypeItem newItem(Player player, ActivityDailyDiscountTypeEnum countTypeEnum) {
		String cfgId = countTypeEnum.getCfgId();
		ActivityDailyDiscountTypeCfg cfgById = getCfgById(cfgId);
		if(cfgById!=null){
			ActivityDailyDiscountTypeItem item = new ActivityDailyDiscountTypeItem();
			String itemId = ActivityDailyDiscountTypeHelper.getItemId(player.getUserId(), countTypeEnum);
			item.setId(itemId);
			item.setUserId(player.getUserId());
			item.setCfgId(cfgById.getId());
			item.setVersion(cfgById.getVersion());
			item.setLastTime(System.currentTimeMillis());
			item.setSubItemList(newSubItemList(countTypeEnum));
			return item;
		}else{
			return null;
		}	
	}
	
	public List<ActivityDailyDiscountTypeSubItem> newSubItemList(ActivityDailyDiscountTypeEnum countTypeEnum) {
		int day ;
		ActivityDailyDiscountTypeCfg cfgByEnumId = getCfgById(countTypeEnum.getCfgId());
		if(cfgByEnumId == null){
			day = 0;
		}else{
			day = ActivityTypeHelper.getDayBy5Am(cfgByEnumId.getStartTime());
		}
		
		
		List<ActivityDailyDiscountTypeSubItem> subItemList = new ArrayList<ActivityDailyDiscountTypeSubItem>();
		List<ActivityDailyDiscountTypeSubCfg> subCfgList = ActivityDailyDiscountTypeSubCfgDAO.getInstance().getCfgListByParentId(countTypeEnum);
		for(ActivityDailyDiscountTypeSubCfg activityVitalitySubCfg : subCfgList){
			if(activityVitalitySubCfg.getAfterSomeDays() != day){
				continue;
			}
			
			for(Integer itemId:activityVitalitySubCfg.getItemList()){
				ActivityDailyDiscountItemCfg itemCfg = ActivityDailyDiscountItemCfgDao.getInstance().getCfgById(itemId+"");
				if(itemCfg == null){
					continue;
				}
				ActivityDailyDiscountTypeSubItem subitem = new ActivityDailyDiscountTypeSubItem();
				subitem.setCfgId(itemCfg.getId());
				subitem.setItemId(itemCfg.getItemId());
				subitem.setCount(0);
				subItemList.add(subitem);
			}
			break;//按理只有一个子类
			
		}	
		return subItemList;		
	}
	


	public ActivityDailyDiscountTypeCfg getConfig(String cfgId) {
		List<ActivityDailyDiscountTypeCfg> cfglist = getAllCfg();
		for(ActivityDailyDiscountTypeCfg cfg : cfglist){
			if(StringUtils.equals(cfg.getId(),cfgId)){
				return cfg;
			}			
		}		
		return null;
	}
	
	
	
	
	
	
	
}