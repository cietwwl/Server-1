package com.playerdata.activity.exChangeType.cfg;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfg;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.ActivityCountTypeHelper;
import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.activity.countType.cfg.ActivityCountTypeSubCfg;
import com.playerdata.activity.countType.cfg.ActivityCountTypeSubCfgDAO;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;
import com.playerdata.activity.exChangeType.ActivityExChangeTypeEnum;
import com.playerdata.activity.exChangeType.ActivityExChangeTypeHelper;
import com.playerdata.activity.exChangeType.ActivityExchangeTypeMgr;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItem;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeSubItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;


public final class ActivityExchangeTypeCfgDAO extends CfgCsvDao<ActivityExchangeTypeCfg> {
	public static ActivityExchangeTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityExchangeTypeCfgDAO.class);
	}
	
	private HashMap<String, List<ActivityExchangeTypeCfg>> cfgListMap;
	
	@Override
	public Map<String, ActivityExchangeTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityExchangeTypeCfg.csv", ActivityExchangeTypeCfg.class);
		for (ActivityExchangeTypeCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}
		
		HashMap<String, List<ActivityExchangeTypeCfg>> cfgListMapTmp = new HashMap<String, List<ActivityExchangeTypeCfg>>();
		for(ActivityExchangeTypeCfg cfg : cfgCacheMap.values()){
			String enumId = cfg.getEnumId();
			List<ActivityExchangeTypeCfg> list = cfgListMapTmp.get(enumId);
			if(list == null){
				list = new ArrayList<ActivityExchangeTypeCfg>();
				cfgListMapTmp.put(enumId, list);
			}
			list.add(cfg);
		}
		this.cfgListMap = cfgListMapTmp;		
		return cfgCacheMap;
	}


	public void parseTime(ActivityExchangeTypeCfg cfg){
		long dropStartTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getDropStartTimeStr());
		cfg.setDropStartTime(dropStartTime);
		long dropEndTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getDropEndTimeStr());
		cfg.setDropEndTime(dropEndTime);		
		long changeStartTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getChangeStartTimeStr());
		cfg.setChangeStartTime(changeStartTime);
		long changeEndTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getChangeEndTimeStr());
		cfg.setChangeEndTime(changeEndTime);		
	}		
	
	public ActivityExchangeTypeCfg getCfgListByItem(ActivityExchangeTypeItem item){
		String id = item.getCfgId();
		String enumId  = item.getEnumId();
		List<ActivityExchangeTypeCfg> cfgListByItem = new ArrayList<ActivityExchangeTypeCfg>();		
		ActivityExchangeTypeMgr activityExchangeTypeMgr = ActivityExchangeTypeMgr.getInstance();
		List<ActivityExchangeTypeCfg> cfgList = cfgListMap.get(enumId);
		if(cfgList == null){
			return null;
		}
		for (ActivityExchangeTypeCfg cfg : cfgList) {
			if (!StringUtils.equals(id, cfg.getId())
					&& activityExchangeTypeMgr.isOpen(cfg)) {
				cfgListByItem.add(cfg);
			}
		}
		if(cfgListByItem.size() > 1){
			GameLog.error(LogModule.ComActivityExchange, null, "发现了两个以上开放的活动,活动枚举为="+ enumId, null);
			return null;
		}
		if(cfgListByItem.size() == 1){
			return cfgListByItem.get(0);
		}
		
		return null;
	}
	
	
	public boolean isCfgByEnumIdEmpty(String enumId){
		return cfgListMap.get(enumId)== null ||cfgListMap.get(enumId).isEmpty();
		
	}
	
	
	
	public ActivityExchangeTypeItem newItem(Player player, ActivityExchangeTypeCfg cfgById){
		if(cfgById!=null){			
			ActivityExchangeTypeItem item = new ActivityExchangeTypeItem();
			String itemId = ActivityExChangeTypeHelper.getItemId(player.getUserId(), ActivityExChangeTypeEnum.getById(cfgById.getEnumId()));
			item.setId(itemId);
			item.setEnumId(cfgById.getEnumId());
			item.setCfgId(cfgById.getId());
			item.setUserId(player.getUserId());
			item.setVersion(cfgById.getVersion());
			item.setSubItemList(newItemList(player, cfgById));
			item.setLasttime(System.currentTimeMillis());
			return item;
		}else{
			return null;
		}		
		
	}
	public List<ActivityExchangeTypeSubItem> newItemList(Player player, ActivityExchangeTypeCfg cfgById) {
		List<ActivityExchangeTypeSubItem> subItemList = new ArrayList<ActivityExchangeTypeSubItem>();
		List<ActivityExchangeTypeSubCfg> subItemCfgList = ActivityExchangeTypeSubCfgDAO.getInstance().getByParentCfgId(cfgById.getId());
		if(subItemCfgList == null){
			return subItemList;
		}
		for (ActivityExchangeTypeSubCfg activityExchangeTypeSubCfg : subItemCfgList) {
			ActivityExchangeTypeSubItem subItem = new ActivityExchangeTypeSubItem();
			subItem.setCfgId(activityExchangeTypeSubCfg.getId());	
			subItem.setTime(0);
			subItem.setIsrefresh(activityExchangeTypeSubCfg.isIsrefresh());
			subItemList.add(subItem);
			
		}	
		return subItemList;
	}
	
	
}