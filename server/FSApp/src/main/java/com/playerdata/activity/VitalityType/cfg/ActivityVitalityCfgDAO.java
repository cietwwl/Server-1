package com.playerdata.activity.VitalityType.cfg;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeEnum;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeMgr;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeItem;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeSubBoxItem;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeSubItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityVitalityCfgDAO extends CfgCsvDao<ActivityVitalityCfg> {
	public static ActivityVitalityCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityVitalityCfgDAO.class);
	}
	
	private HashMap<String, List<ActivityVitalityCfg>> cfgListMap;
	
	@Override
	public Map<String, ActivityVitalityCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityVitalityTypeCfg.csv", ActivityVitalityCfg.class);
		HashMap<String, List<ActivityVitalityCfg>> cfgListMapTmp = new HashMap<String, List<ActivityVitalityCfg>>();
		for(ActivityVitalityCfg cfg : cfgCacheMap.values()){
			cfg.ExtraInitAfterLoad();
			ActivityTypeHelper.add(cfg, String.valueOf(cfg.getEnumID()), cfgListMapTmp);			
		}
		this.cfgListMap = cfgListMapTmp;
		return cfgCacheMap;
	}
	
	/**
	 * 
	 * @param player
	 * @param countTypeEnum
	 * @param subdaysNum  无数据记录的玩家根据第几天开始参与活跃之王来生成数据
	 * @return
	 */
	public ActivityVitalityTypeItem newItem(String userId,ActivityVitalityCfg cfgById){
		if(cfgById!=null){
			int day = ActivityVitalityCfgDAO.getInstance().getday(cfgById) ;
			ActivityVitalityTypeItem item = new ActivityVitalityTypeItem();	
//			String itemId = ActivityVitalityTypeHelper.getItemId(userId, ActivityVitalityTypeEnum.getById(cfgById.getEnumID()));
			int id = cfgById.getEnumID();
			item.setId(id);
			item.setEnumId(String.valueOf(cfgById.getEnumID()));
			item.setCfgId(String.valueOf(cfgById.getId()));
			item.setUserId(userId);
			item.setVersion(String.valueOf(cfgById.getVersion()));
			item.setActiveCount(0);
			item.setSubItemList(newItemList(day,cfgById));
			List<ActivityVitalityTypeSubBoxItem> boxlist = newBoxItemList(day,cfgById);
			if(boxlist != null&&!boxlist.isEmpty()){
				item.setSubBoxItemList(boxlist);
			}
			item.setLastTime(System.currentTimeMillis());
			item.setCanGetReward(cfgById.isCanGetReward());
			return item;
		}else{
			return null;
		}		
	}
	
	/**防止策划把活跃之王的配置表删除，导致报空*/
	public int getday(ActivityVitalityCfg tmpCfg){
		if(tmpCfg == null){
			return 0;
		}		
		return ActivityTypeHelper.getDayBy5Am(tmpCfg.getStartTime());
	
	}

	public List<ActivityVitalityTypeSubItem> newItemList(int day,ActivityVitalityCfg cfg) {
		List<ActivityVitalityTypeSubItem> subItemList = null;
		List<ActivityVitalitySubCfg> allsubCfgList = ActivityVitalitySubCfgDAO.getInstance().getAllCfg();
		if(ActivityVitalityTypeEnum.getById(String.valueOf(cfg.getEnumID())) == ActivityVitalityTypeEnum.Vitality){
			subItemList = newItemListOne(day,cfg,allsubCfgList);	
		}			
		if(ActivityVitalityTypeEnum.getById(String.valueOf(cfg.getEnumID())) == ActivityVitalityTypeEnum.VitalityTwo){
			subItemList = newItemListTwo(day,cfg,allsubCfgList);
		}
		return subItemList;
	}

	private List<ActivityVitalityTypeSubItem> newItemListOne(int day,
			ActivityVitalityCfg cfg,
			List<ActivityVitalitySubCfg> allsubCfgList) {
		List<ActivityVitalityTypeSubItem> subItemList = new ArrayList<ActivityVitalityTypeSubItem>();
		for(ActivityVitalitySubCfg activityVitalitySubCfg : allsubCfgList){
			if(activityVitalitySubCfg.getDay() != day||(activityVitalitySubCfg.getActiveType() != cfg.getId())){
				continue;
			}			
			ActivityVitalityTypeSubItem subitem = new ActivityVitalityTypeSubItem();
			subitem.setCfgId(activityVitalitySubCfg.getId());
			subitem.setCount(0);
			subitem.setTaken(false);
			subitem.setGiftId(activityVitalitySubCfg.getGiftId());
			subitem.setType(activityVitalitySubCfg.getType());
			subItemList.add(subitem);
		}	
		return subItemList;
	}
	
	private List<ActivityVitalityTypeSubItem> newItemListTwo(int day,
			ActivityVitalityCfg cfg,
			List<ActivityVitalitySubCfg> allsubCfgList) {
		List<ActivityVitalityTypeSubItem> subItemList = new ArrayList<ActivityVitalityTypeSubItem>();
		for(ActivityVitalitySubCfg activityVitalitySubCfg : allsubCfgList){
			if(cfg.getId() !=  activityVitalitySubCfg.getActiveType()){
				continue;
			}			
			ActivityVitalityTypeSubItem subitem = new ActivityVitalityTypeSubItem();
			subitem.setCfgId(activityVitalitySubCfg.getId());
			subitem.setCount(0);
			subitem.setTaken(false);
			subitem.setGiftId(activityVitalitySubCfg.getGiftId());
			subitem.setType(activityVitalitySubCfg.getType());
			subItemList.add(subitem);
		}	
		return subItemList;
	}
	
	public List<ActivityVitalityTypeSubBoxItem> newBoxItemList(int day,ActivityVitalityCfg cfg) {
		List<ActivityVitalityTypeSubBoxItem> subItemList = null;
		List<ActivityVitalityRewardCfg> allsubCfgList = ActivityVitalityRewardCfgDAO.getInstance().getAllCfg();
		if(ActivityVitalityTypeEnum.getById(String.valueOf(cfg.getEnumID()))  == ActivityVitalityTypeEnum.Vitality){
			subItemList = newBoxItemListOne(day,cfg,allsubCfgList);	
		}			
		if(ActivityVitalityTypeEnum.getById(String.valueOf(cfg.getEnumID()))  == ActivityVitalityTypeEnum.VitalityTwo){
			subItemList = newBoxItemListTwo(day,cfg,allsubCfgList);
		}
		return subItemList;
	}	

	private List<ActivityVitalityTypeSubBoxItem> newBoxItemListOne(int day,
			ActivityVitalityCfg cfg,
			List<ActivityVitalityRewardCfg> allsubCfgList) {
		List<ActivityVitalityTypeSubBoxItem> subItemList = new ArrayList<ActivityVitalityTypeSubBoxItem>();
		if(allsubCfgList == null){
			return subItemList;
		}
		
		for(ActivityVitalityRewardCfg activityVitalityRewardCfg : allsubCfgList){
			if(activityVitalityRewardCfg.getDay() != day||(activityVitalityRewardCfg.getActiveType() != cfg.getId())){
				continue;
			}			
			ActivityVitalityTypeSubBoxItem subitem = new ActivityVitalityTypeSubBoxItem();
			subitem.setCfgId(activityVitalityRewardCfg.getId());
			subitem.setCount(activityVitalityRewardCfg.getActivecount());
			subitem.setTaken(false);
			subitem.setGiftId(activityVitalityRewardCfg.getGiftId());
			subItemList.add(subitem);
		}
		return subItemList;
	}
	
	private List<ActivityVitalityTypeSubBoxItem> newBoxItemListTwo(int day,
			ActivityVitalityCfg cfg,
			List<ActivityVitalityRewardCfg> allsubCfgList) {
		List<ActivityVitalityTypeSubBoxItem> subItemList = new ArrayList<ActivityVitalityTypeSubBoxItem>();
		if(allsubCfgList == null){
			return subItemList;
		}
		for(ActivityVitalityRewardCfg activityVitalitySubCfg : allsubCfgList){
			if(cfg.getId() != activityVitalitySubCfg.getActiveType()){
				continue;
			}			
			ActivityVitalityTypeSubBoxItem subitem = new ActivityVitalityTypeSubBoxItem();
			subitem.setCfgId(activityVitalitySubCfg.getId());
			subitem.setCount(activityVitalitySubCfg.getActivecount());
			subitem.setTaken(false);
			subitem.setGiftId(activityVitalitySubCfg.getGiftId());
			subItemList.add(subitem);
		}	
		return subItemList;
	}

	public ActivityVitalityCfg getCfgByItemOfVersion(ActivityVitalityTypeItem item) {
		List<ActivityVitalityCfg> openCfgList = new ArrayList<ActivityVitalityCfg>();
		ActivityVitalityTypeMgr activityVitalityTypeMgr = ActivityVitalityTypeMgr.getInstance();
		List<ActivityVitalityCfg> listByEnumId = cfgListMap.get(item.getEnumId());
		if(listByEnumId == null){
			return null;
		}		
		for(ActivityVitalityCfg cfg : listByEnumId){
			if (!StringUtils.equals(item.getCfgId(), String.valueOf(cfg.getId()))
					&& activityVitalityTypeMgr.isOpen(cfg)) {
				openCfgList.add(cfg);
			}
			
		}
		if(openCfgList.size() > 1){
			GameLog.error(LogModule.ComActivityVitality, null, "单个类型出现多个同时激活的cfg", null);
			return null;
		}else if(openCfgList.size() == 1){
			
			return openCfgList.get(0);
		}
		return null;
	}
	
	public List<ActivityVitalityCfg> getCfgListByEnumId(String enumId){
		List<ActivityVitalityCfg> cfgList = cfgListMap.get(enumId);
		if(cfgList == null){
			cfgList = new ArrayList<ActivityVitalityCfg>();
		}
		return cfgList;		
	}
}