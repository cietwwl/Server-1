package com.playerdata.activity.countType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.common.BeanCopyer;
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
			parseSubItemList(cfgTmp);
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
	
	
	public void parseSubItemList(ActivityCountTypeCfg cfgItem){
		String subItems = cfgItem.getSubItems();
		List<ActivityCountTypeSubItem>  itemList = new ArrayList<ActivityCountTypeSubItem>();
		
		//id-count-giftId
		String[] itemArray = subItems.split(";");
		for (String itemStr : itemArray) {
			String[] params = itemStr.split(":");
			String id = params[0];
			int count = Integer.valueOf(params[1]);
			String giftId = params[2];			
			ActivityCountTypeSubItem item = new ActivityCountTypeSubItem();
			item.setCount(count);
			item.setId(id);
			item.setGift(giftId);
			itemList.add(item);
		}
		cfgItem.setSubItemList(itemList);		
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
			return item;
		}else{
			return null;
		}
		
		
		
	}
	
	
	public ActivityCountTypeSubItem newSubItem(ActivityCountTypeEnum countTypeEnum, String subItemId){
		ActivityCountTypeCfg cfg = getCfgById(countTypeEnum.getCfgId());
		List<ActivityCountTypeSubItem> subItemList = cfg.getSubItemList();
		
		ActivityCountTypeSubItem source = null;
		for (ActivityCountTypeSubItem subItemTmp : subItemList) {
			if(StringUtils.equals(subItemTmp.getId(), subItemId)){
				source = subItemTmp;
				break;
			}
		}
		
		ActivityCountTypeSubItem target = null;
		if(source!=null){
			target = new ActivityCountTypeSubItem();
			BeanCopyer.copy(source, target);
		}		
		return target;
	}

	


}