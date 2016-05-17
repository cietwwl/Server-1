package com.playerdata.activity.timeCountType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.activity.timeCountType.ActivityTimeCountTypeEnum;
import com.playerdata.activity.timeCountType.ActivityTimeCountTypeHelper;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItem;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeSubItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityTimeCountTypeCfgDAO extends CfgCsvDao<ActivityTimeCountTypeCfg> {


	public static ActivityTimeCountTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityTimeCountTypeCfgDAO.class);
	}

	
	@Override
	public Map<String, ActivityTimeCountTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityTimeCountTypeCfg.csv", ActivityTimeCountTypeCfg.class);
		for (ActivityTimeCountTypeCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}
		
		return cfgCacheMap;
	}
	
	
	private void parseTime(ActivityTimeCountTypeCfg cfgItem){
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getStartTimeStr());
		cfgItem.setStartTime(startTime);
		
		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getEndTimeStr());
		cfgItem.setEndTime(endTime);		
//		System.out.println("activitytimecounttypecfgdao.  starttimestr = " + cfgItem.getStartTimeStr() + " starttime=" + startTime + " endtimestr ="+ cfgItem.getEndTimeStr() + " endtime= " + endTime );
	}
		
	

	
	public ActivityTimeCountTypeItem newItem(Player player, ActivityTimeCountTypeEnum countTypeEnum){
		
		String cfgId = countTypeEnum.getCfgId();
		ActivityTimeCountTypeCfg cfgById = getCfgById(cfgId );
		if(cfgById!=null){			
			ActivityTimeCountTypeItem item = new ActivityTimeCountTypeItem();
			String itemId = ActivityTimeCountTypeHelper.getItemId(player.getUserId(), countTypeEnum);
			item.setId(itemId);
			item.setCfgId(cfgId);
			item.setUserId(player.getUserId());
			item.setVersion(cfgById.getVersion());
			List<ActivityTimeCountTypeSubItem> newItemList = newItemList(player, cfgById);
			item.setSubItemList(newItemList);
			item.setCount(1);
			return item;
		}else{
			return null;
		}		
		
	}
	
	
	public List<ActivityTimeCountTypeSubItem> newItemList(Player player, ActivityTimeCountTypeCfg activityCountTypeCfg) {
		List<ActivityTimeCountTypeSubItem> subItemList = new ArrayList<ActivityTimeCountTypeSubItem>();
		List<ActivityTimeCountTypeSubCfg> subItemCfgList = ActivityTimeCountTypeSubCfgDAO.getInstance().getByParentCfgId(activityCountTypeCfg.getId());
		for (ActivityTimeCountTypeSubCfg activityCountTypeSubCfg : subItemCfgList) {
			ActivityTimeCountTypeSubItem subItem = new ActivityTimeCountTypeSubItem();
			subItem.setCfgId(activityCountTypeSubCfg.getId());	
			subItem.setTaken(false);
			subItemList.add(subItem);
		}	
		return subItemList;
	}

	


}