package com.playerdata.activity.fortuneCatType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeEnum;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalitySubCfg;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeSubItem;
import com.playerdata.activity.countType.ActivityCountTypeHelper;
import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeEnum;
import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeHelper;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItem;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeSubItem;
import com.playerdata.activity.fortuneCatType.ActivityFortuneCatTypeMgr;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItem;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeSubItem;
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

	public ActivityFortuneCatTypeItem newItem(Player player, ActivityFortuneCatTypeCfg cfg) {
		if(cfg!=null){
			ActivityFortuneCatTypeItem item = new ActivityFortuneCatTypeItem();
			item.setId(player.getUserId());
			item.setUserId(player.getUserId());
			item.setCfgId(cfg.getId());
			item.setVersion(cfg.getVersion());
			item.setSubItemList(newSubItemList(cfg));
			item.setTimes(0);
			return item;
		}else{
			return null;
		}	
	}
//	
	public List<ActivityFortuneCatTypeSubItem> newSubItemList(ActivityFortuneCatTypeCfg cfg) {		
		List<ActivityFortuneCatTypeSubItem> subItemList = new ArrayList<ActivityFortuneCatTypeSubItem>();
		List<ActivityFortuneCatTypeSubCfg> subCfgList = ActivityFortuneCatTypeSubCfgDAO.getInstance().getCfgListByParentId(cfg.getId());
		if(subCfgList == null){
			return subItemList;
		}
		for(ActivityFortuneCatTypeSubCfg subCfg : subCfgList){
			ActivityFortuneCatTypeSubItem item = new ActivityFortuneCatTypeSubItem();
			item.setCfgId(subCfg.getId()+"");
			item.setNum(subCfg.getNum());
			item.setCost(subCfg.getCost()+"");
			item.setVip(subCfg.getVip());
			item.setGetGold(0);
			subItemList.add(item);
		}
		return subItemList;		
	}

	public ActivityFortuneCatTypeCfg getCfgListByItem(
			ActivityFortuneCatTypeItem targetItem) {
		ActivityFortuneCatTypeMgr activityFortuneCatTypeMgr = ActivityFortuneCatTypeMgr.getInstance();
		String cfgId = targetItem.getCfgId();
		List<ActivityFortuneCatTypeCfg>  cfgList = getAllCfg();
		List<ActivityFortuneCatTypeCfg>  cfgListByItem = new ArrayList<ActivityFortuneCatTypeCfg>();
		for(ActivityFortuneCatTypeCfg cfg : cfgList){//取出所有符合相同枚举的可选配置
			if(!StringUtils.equals(cfgId, cfg.getId())){
				cfgListByItem.add(cfg);
			}			
		}
		
		List<ActivityFortuneCatTypeCfg>  cfgListIsOpen = new ArrayList<ActivityFortuneCatTypeCfg>();//激活的下一个活动，只有0或1个；
		for(ActivityFortuneCatTypeCfg cfg : cfgListByItem){
			if(activityFortuneCatTypeMgr.isOpen(cfg)){
				cfgListIsOpen.add(cfg);
			}			
		}
		
		if(cfgListIsOpen.size() > 1){
			GameLog.error(LogModule.ComActivityFortuneCat, null, "发现了两个以上开放的活动", null);
			return null;
		}else if(cfgListIsOpen.size() == 1){
			return cfgListIsOpen.get(0);
		}		
		return null;
	}
	

}