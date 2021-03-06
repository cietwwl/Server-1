package com.playerdata.activity.timeCardType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.activity.timeCardType.ActivityTimeCardTypeEnum;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItem;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeSubItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 月卡
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
		//在不改配置文件的情况下，只修改itemid方便插入数据库；
		ActivityTimeCardTypeCfg cfgById = getCfgById(ActivityTimeCardTypeEnum.Month.getCfgId());
		if(cfgById!=null){			
			ActivityTimeCardTypeItem item = new ActivityTimeCardTypeItem();
			int id = Integer.parseInt(ActivityTimeCardTypeEnum.Month.getCfgId());
			item.setId(id);
			item.setUserId(player.getUserId());
			item.setCfgId(cfgById.getId());
			newAndAddSubItemList(item);	
			return item;
		}else{
			return null;
		}
	}


	private void newAndAddSubItemList(ActivityTimeCardTypeItem item) {
		List<ActivityTimeCardTypeSubItem> subItemList = new ArrayList<ActivityTimeCardTypeSubItem>();
		List<ActivityTimeCardTypeSubCfg> subItemCfgList = ActivityTimeCardTypeSubCfgDAO.getInstance().getByParentCfgId(item.getCfgId());
		if(subItemCfgList == null){
			subItemCfgList = new ArrayList<ActivityTimeCardTypeSubCfg>();
		}
		for (ActivityTimeCardTypeSubCfg tmpSubCfg : subItemCfgList) {
			subItemList.add(ActivityTimeCardTypeSubItem.newItem(tmpSubCfg));
		}
		item.setSubItemList(subItemList);
	}
}