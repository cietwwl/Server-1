package com.playerdata.activity.timeCardType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.activity.timeCardType.ActivityTimeCardTypeEnum;
import com.playerdata.activity.timeCardType.ActivityTimeCardTypeHelper;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItem;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeSubItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityTimeCardTypeCfgDAO extends CfgCsvDao<ActivityTimeCardTypeCfg> {


	public static ActivityTimeCardTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityTimeCardTypeCfgDAO.class);
	}

	
	@Override
	public Map<String, ActivityTimeCardTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityTimeCardTypeCfg.csv", ActivityTimeCardTypeCfg.class);
		return cfgCacheMap;
	}
	
	public ActivityTimeCardTypeCfg getConfig(String id){
		ActivityTimeCardTypeCfg cfg = getCfgById(id);
		return cfg;
	}
	
	public ActivityTimeCardTypeItem newItem(Player player){
		
		String cfgId = ActivityTimeCardTypeEnum.Month.getCfgId();
		ActivityTimeCardTypeCfg cfgById = getCfgById("1" );
		if(cfgById!=null){			
			ActivityTimeCardTypeItem item = new ActivityTimeCardTypeItem();
			String itemId = ActivityTimeCardTypeHelper.getItemId(player.getUserId(), ActivityTimeCardTypeEnum.Month);
			item.setId(itemId);
			item.setUserId(player.getUserId());
			item.setCfgId(cfgId);
			newAndAddSubItemList(item);
			
			return item;
		}else{
			return null;
		}
		
	}


	private void newAndAddSubItemList(ActivityTimeCardTypeItem item) {
		List<ActivityTimeCardTypeSubItem> subItemList = new ArrayList<ActivityTimeCardTypeSubItem>();
		List<ActivityTimeCardTypeSubCfg> subItemCfgList = ActivityTimeCardTypeSubCfgDAO.getInstance().getByParentCfgId(item.getCfgId());
		for (ActivityTimeCardTypeSubCfg tmpSubCfg : subItemCfgList) {
			subItemList.add(ActivityTimeCardTypeSubItem.newItem(tmpSubCfg));
		}
		item.setSubItemList(subItemList);
	}


}