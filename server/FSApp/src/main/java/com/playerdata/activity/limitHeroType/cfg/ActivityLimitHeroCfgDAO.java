package com.playerdata.activity.limitHeroType.cfg;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.limitHeroType.ActivityLimitHeroEnum;
import com.playerdata.activity.limitHeroType.ActivityLimitHeroTypeMgr;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItem;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeSubItem;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityLimitHeroCfgDAO extends CfgCsvDao<ActivityLimitHeroCfg> {
	public static ActivityLimitHeroCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityLimitHeroCfgDAO.class);
	}
	
	@Override
	public Map<String, ActivityLimitHeroCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityLimitHeroCfg.csv", ActivityLimitHeroCfg.class);		
		for(ActivityCfgIF cfg : cfgCacheMap.values()){
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
	
	/**
	 * 
	 * @param player
	 * @param countTypeEnum
	 * @param subdaysNum  无数据记录的玩家根据第几天开始参与活跃之王来生成数据
	 * @return
	 */
	public ActivityLimitHeroTypeItem newItem(Player player,ActivityLimitHeroCfg cfg){
		if(cfg!=null){
			ActivityLimitHeroTypeItem item = new ActivityLimitHeroTypeItem();	
			String userid = player.getUserId();
//			String itemId = ActivityLimitHeroHelper.getItemId(userid, ActivityLimitHeroEnum.LimitHero);
			int id = Integer.parseInt(ActivityLimitHeroEnum.LimitHero.getCfgId());
			item.setId(id);
			item.setCfgId(String.valueOf(cfg.getId()));
			item.setUserId(userid);
			item.setVersion(String.valueOf(cfg.getVersion()));
			item.setLastSingleTime(0);
			item.setSubList(newSubItemList(cfg));
			item.setIntegral(0);
			return item;
		}else{
			return null;
		}		
	}

	public List<ActivityLimitHeroTypeSubItem> newSubItemList(ActivityLimitHeroCfg cfg){
		List<ActivityLimitHeroBoxCfg> boxCfgList = ActivityLimitHeroBoxCfgDAO.getInstance().getCfgListByParentID(String.valueOf(cfg.getId()));
		
		List<ActivityLimitHeroTypeSubItem> subItemList = new ArrayList<ActivityLimitHeroTypeSubItem>();
		if(boxCfgList == null){
			return subItemList;
		}
		for(ActivityLimitHeroBoxCfg boxCfg : boxCfgList){
			ActivityLimitHeroTypeSubItem subItem = new ActivityLimitHeroTypeSubItem();
			subItem.setCfgId(boxCfg.getId());
			subItem.setIntegral(boxCfg.getIntegral());
			subItem.setRewards(boxCfg.getRewards());
			subItemList.add(subItem);		
		}		
		return subItemList;
	}
	

	public ActivityLimitHeroCfg getCfgListByItem(ActivityLimitHeroTypeItem item) {
		List<ActivityLimitHeroCfg> openCfgList = new ArrayList<ActivityLimitHeroCfg>();
		ActivityLimitHeroTypeMgr activityLimitHeroTypeMgr = ActivityLimitHeroTypeMgr.getInstance();
		List<ActivityLimitHeroCfg> allList = getAllCfg();
		for(ActivityLimitHeroCfg cfg : allList){
			if (!StringUtils.equals(item.getCfgId(), String.valueOf(cfg.getId())) && activityLimitHeroTypeMgr.isOpen(cfg)) {
				openCfgList.add(cfg);
			}			
		}
		if(openCfgList.size() > 1){
			GameLog.error(LogModule.ComActivityLimitHero, null, "单个类型出现多个同时激活的cfg", null);
			return null;
		}else if(openCfgList.size() == 1){			
			return openCfgList.get(0);
		}
		return null;
	}
	

}