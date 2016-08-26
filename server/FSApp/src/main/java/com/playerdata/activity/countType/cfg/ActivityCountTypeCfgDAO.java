package com.playerdata.activity.countType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.ActivityCountTypeHelper;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityCountTypeCfgDAO extends CfgCsvDao<ActivityCountTypeCfg> {


	public static ActivityCountTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityCountTypeCfgDAO.class);
	}

	
	@Override
	public Map<String, ActivityCountTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityCountTypeCfg.csv", ActivityCountTypeCfg.class);
		for (ActivityCountTypeCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}
		
		return cfgCacheMap;
	}
	
	
	public void parseTime(ActivityCountTypeCfg cfgItem){
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getStartTimeStr());
		cfgItem.setStartTime(startTime);
		
		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getEndTimeStr());
		cfgItem.setEndTime(endTime);		
	}
		
	
	public ActivityCountTypeCfg getConfig(String id){
		ActivityCountTypeCfg cfg = getCfgById(id);
		return cfg;
	}
	
	public ActivityCountTypeItem newItem(Player player, ActivityCountTypeEnum countTypeEnum){
		
		String cfgId = countTypeEnum.getCfgId();
		ActivityCountTypeCfg cfgById = getCfgById(cfgId );
		if(cfgById!=null){			
			ActivityCountTypeItem item = new ActivityCountTypeItem();
			String itemId = ActivityCountTypeHelper.getItemId(player.getUserId(), countTypeEnum);
			item.setId(itemId);
			item.setCfgId(cfgId);
			item.setUserId(player.getUserId());
			item.setVersion(cfgById.getVersion());
			item.setSubItemList(newItemList(player, cfgById));
			return item;
		}else{
			return null;
		}		
		
	}
	
	
	public List<ActivityCountTypeSubItem> newItemList(Player player, ActivityCountTypeCfg activityCountTypeCfg) {
		List<ActivityCountTypeSubItem> subItemList = new ArrayList<ActivityCountTypeSubItem>();
		List<ActivityCountTypeSubCfg> subItemCfgList = ActivityCountTypeSubCfgDAO.getInstance().getByParentCfgId(activityCountTypeCfg.getId());
		for (ActivityCountTypeSubCfg activityCountTypeSubCfg : subItemCfgList) {
			ActivityCountTypeSubItem subItem = new ActivityCountTypeSubItem();
			subItem.setCfgId(activityCountTypeSubCfg.getId());	
			subItem.setCount(activityCountTypeSubCfg.getAwardCount());
			subItemList.add(subItem);
		}	
		return subItemList;
	}

	


}