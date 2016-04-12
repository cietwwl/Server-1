package com.playerdata.activity.countType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.common.BeanCopyer;
import com.playerdata.Player;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
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
		}
		
		return cfgCacheMap;
	}
	
	public ActivityCountTypeCfg getConfig(String id){
		ActivityCountTypeCfg cfg = getCfgById(id);
		return cfg;
	}
	
	public ActivityCountTypeItem newItem(Player player,String cfgId){
		
		ActivityCountTypeCfg cfgById = getCfgById(cfgId);
		if(cfgById!=null){			
			ActivityCountTypeItem item = new ActivityCountTypeItem();
			item.setId(cfgId);
			item.setUserId(player.getUserId());
			return item;
		}else{
			return null;
		}
		
		
		
	}
	
	
	public ActivityCountTypeSubItem newSubItem(String id, String subItemId){
		ActivityCountTypeCfg cfg = getCfgById(id);
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
	


}