package com.playerdata.activity.fortuneCatType.cfg;

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


public final class ActivityFortuneCatTypeCfgDAO extends
		CfgCsvDao<ActivityFortuneCatTypeCfg> {

	public static ActivityFortuneCatTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityFortuneCatTypeCfgDAO.class);
	}

	@Override
	public Map<String, ActivityFortuneCatTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map(
				"Activity/ActivityFortunCatTypeCfg.csv",
				ActivityFortuneCatTypeCfg.class);
		for (ActivityFortuneCatTypeCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}
		return cfgCacheMap;
	}

	private void parseTime(ActivityFortuneCatTypeCfg cfgItem) {
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getStartTimeStr());
		cfgItem.setStartTime(startTime);
		
		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getEndTimeStr());
		cfgItem.setEndTime(endTime);	
	}

//	public ActivityDailyDiscountTypeItem newItem(Player player, ActivityDailyDiscountTypeEnum countTypeEnum) {
//		String cfgId = countTypeEnum.getCfgId();
//		ActivityFortuneCatTypeCfg cfgById = getCfgById(cfgId);
//		if(cfgById!=null){
//			ActivityDailyDiscountTypeItem item = new ActivityDailyDiscountTypeItem();
//			String itemId = ActivityDailyDiscountTypeHelper.getItemId(player.getUserId(), countTypeEnum);
//			item.setId(itemId);
//			item.setUserId(player.getUserId());
//			item.setCfgId(cfgById.getId());
//			item.setVersion(cfgById.getVersion());
//			item.setLastTime(System.currentTimeMillis());
//			item.setSubItemList(newSubItemList(countTypeEnum));
//			return item;
//		}else{
//			return null;
//		}	
//	}
//	
//	public List<ActivityDailyDiscountTypeSubItem> newSubItemList(ActivityDailyDiscountTypeEnum countTypeEnum) {
//		int day ;
//		ActivityFortuneCatTypeCfg cfgByEnumId = getCfgById(countTypeEnum.getCfgId());
//		if(cfgByEnumId == null){
//			day = 0;
//		}else{
//			day = ActivityTypeHelper.getDayBy5Am(cfgByEnumId.getStartTime());
//		}
//		
//		
//		List<ActivityDailyDiscountTypeSubItem> subItemList = new ArrayList<ActivityDailyDiscountTypeSubItem>();
//		List<ActivityFortuneCatTypeSubCfg> subCfgList = ActivityFortuneCatTypeSubCfgDAO.getInstance().getCfgListByParentId(countTypeEnum);
//		for(ActivityFortuneCatTypeSubCfg activityVitalitySubCfg : subCfgList){
//			if(activityVitalitySubCfg.getAfterSomeDays() != day){
//				continue;
//			}
//			
//			for(Integer itemId:activityVitalitySubCfg.getItemList()){
//				ActivityDailyDiscountItemCfg itemCfg = ActivityDailyDiscountItemCfgDao.getInstance().getCfgById(itemId+"");
//				if(itemCfg == null){
//					continue;
//				}
//				ActivityDailyDiscountTypeSubItem subitem = new ActivityDailyDiscountTypeSubItem();
//				subitem.setCfgId(itemCfg.getId());
//				subitem.setItemId(itemCfg.getItemId());
//				subitem.setItemNum(itemCfg.getItemNum());
//				subitem.setCount(0);
//				subItemList.add(subitem);
//			}
//			break;//按理只有一个子类
//			
//		}	
//		return subItemList;		
//	}
	

}