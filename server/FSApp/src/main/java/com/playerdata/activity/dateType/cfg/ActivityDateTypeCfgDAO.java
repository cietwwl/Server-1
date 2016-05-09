package com.playerdata.activity.dateType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.activity.dateType.ActivityDateTypeEnum;
import com.playerdata.activity.dateType.ActivityDateTypeHelper;
import com.playerdata.activity.dateType.data.ActivityDateTypeItem;
import com.playerdata.activity.dateType.data.ActivityDateTypeSubItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityDateTypeCfgDAO extends CfgCsvDao<ActivityDateTypeCfg> {


	public static ActivityDateTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityDateTypeCfgDAO.class);
	}

	
	@Override
	public Map<String, ActivityDateTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityDateTypeCfg.csv", ActivityDateTypeCfg.class);
		for (ActivityDateTypeCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}
		return cfgCacheMap;
	}
	
	private void parseTime(ActivityDateTypeCfg cfgItem){
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getStartTimeStr());
		cfgItem.setStartTime(startTime);
		
		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getEndTimeStr());
		cfgItem.setEndTime(endTime);		
	}
	
	public ActivityDateTypeCfg getConfig(String id){
		ActivityDateTypeCfg cfg = getCfgById(id);
		return cfg;
	}
	
	public ActivityDateTypeItem newItem(Player player, ActivityDateTypeEnum typeEnum){
		
		String cfgId = typeEnum.getCfgId();
		ActivityDateTypeCfg cfgById = getCfgById(cfgId );
		if(cfgById!=null){			
			ActivityDateTypeItem item = new ActivityDateTypeItem();
			String itemId = ActivityDateTypeHelper.getItemId(player.getUserId(), typeEnum);
			item.setId(itemId);
			item.setUserId(player.getUserId());
			item.setCfgId(cfgId);
			newAndAddSubItemList(item);
			
			return item;
		}else{
			return null;
		}
		
	}


	private void newAndAddSubItemList(ActivityDateTypeItem item) {
		List<ActivityDateTypeSubItem> subItemList = new ArrayList<ActivityDateTypeSubItem>();
		List<ActivityDateTypeSubCfg> subItemCfgList = ActivityDateTypeSubCfgDAO.getInstance().getByParentCfgId(item.getCfgId());
		for (ActivityDateTypeSubCfg tmpSubCfg : subItemCfgList) {
			subItemList.add(ActivityDateTypeSubItem.newItem(tmpSubCfg));
		}
		item.setSubItemList(subItemList);
	}


}