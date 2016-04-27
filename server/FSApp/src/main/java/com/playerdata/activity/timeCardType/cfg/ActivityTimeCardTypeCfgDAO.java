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
	
	public ActivityTimeCardTypeItem newItem(Player player, ActivityTimeCardTypeEnum typeEnum){
		
		String cfgId = typeEnum.getCfgId();
		ActivityTimeCardTypeCfg cfgById = getCfgById(cfgId );
		if(cfgById!=null){			
			ActivityTimeCardTypeItem item = new ActivityTimeCardTypeItem();
			String itemId = ActivityTimeCardTypeHelper.getItemId(player.getUserId(), typeEnum);
			item.setId(itemId);
			item.setCfgId(cfgId);
			item.setUserId(player.getUserId());
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