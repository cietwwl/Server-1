package com.playerdata.activity.rankType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.rankType.ActivityRankTypeEnum;
import com.playerdata.activity.rankType.ActivityRankTypeHelper;
import com.playerdata.activity.rankType.ActivityRankTypeMgr;
import com.playerdata.activity.rankType.data.ActivityRankTypeItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityRankTypeCfgDAO extends CfgCsvDao<ActivityRankTypeCfg> {


	
	public static ActivityRankTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityRankTypeCfgDAO.class);
	}
	
	
	
	
	@Override
	public Map<String, ActivityRankTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityRankTypeCfg.csv", ActivityRankTypeCfg.class);
		for (ActivityRankTypeCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
			
			
		}
		
		return cfgCacheMap;
	}
	
	private void parseTime(ActivityRankTypeCfg cfg){
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getStartTimeStr());
		cfg.setStartTime(startTime);
		
		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getEndTimeStr());
		cfg.setEndTime(endTime);		
	}
	
	
	
//	public ActivityRankTypeCfg getConfig(String id){
//		ActivityRankTypeCfg cfg = getCfgById(id);
//		return cfg;
//	}
//	
	public ActivityRankTypeItem newItem(Player player, ActivityRankTypeCfg cfgById){
		if(cfgById!=null){			
			ActivityRankTypeItem item = new ActivityRankTypeItem();
			String itemId = ActivityRankTypeHelper.getItemId(player.getUserId(), ActivityRankTypeEnum.getById(cfgById.getEnumId()));
			item.setId(itemId);
			item.setUserId(player.getUserId());
			item.setCfgId(cfgById.getId());
			item.setEnumId(cfgById.getEnumId());
			item.setVersion(cfgById.getVersion());
			return item;
		}else{
			return null;
		}
		
	}




	public ActivityRankTypeCfg getCfgById(ActivityRankTypeItem targetItem) {
		String id = targetItem.getCfgId();
		String enumId = targetItem.getEnumId();
		List<ActivityRankTypeCfg> cfgListByItem = new ArrayList<ActivityRankTypeCfg>();
		List<ActivityRankTypeCfg> cfgList = getAllCfg();
		for(ActivityRankTypeCfg cfg : cfgList){
			if(!StringUtils.equals(id, cfg.getId())&&StringUtils.equals(enumId, cfg.getEnumId())&&ActivityRankTypeMgr.getInstance().isOpen(cfg)){
				cfgListByItem.add(cfg);
			}			
		}
		if(cfgListByItem.size() > 1){
			GameLog.error(LogModule.ComActivityRank, null, "发现了两个以上开放的活动,活动枚举为="+ enumId, null);
			return null;
		}
		if(cfgListByItem.size() == 1){
			return cfgListByItem.get(0);
		}
		
		return null;
	}



	/**根据传来的enumId,找出同类活动里处于派发状态的cfg*/
	public ActivityRankTypeCfg getCfgByModleCfgEnumId(String enumId) {
		ActivityRankTypeCfg cfgIsOpen = null;
		List<ActivityRankTypeCfg> allCfgList = getAllCfg();
		List<ActivityRankTypeCfg> cfgListByEnumId = new ArrayList<ActivityRankTypeCfg>();
		for(ActivityRankTypeCfg cfg : allCfgList){
			if(StringUtils.equals(cfg.getEnumId(), enumId)){
				cfgListByEnumId.add(cfg);
			}
		}
		long now = System.currentTimeMillis();
		for(ActivityRankTypeCfg cfg : cfgListByEnumId){//有活动处于激活状态
			if(cfg.getStartTime() < now && cfg.getEndTime() > now){
				cfgIsOpen = cfg;
				return cfgIsOpen;
			}			
		}
		for(ActivityRankTypeCfg cfg : cfgListByEnumId){//所有已关闭的
			if(cfg.getEndTime() < now ){
				if(cfgIsOpen == null){
					cfgIsOpen = cfg;
					continue;					
				}
				if(cfgIsOpen.getEndTime() < cfg.getEndTime()){
					cfgIsOpen = cfg;
				}
			}			
		}		
		return cfgIsOpen;
	}


}