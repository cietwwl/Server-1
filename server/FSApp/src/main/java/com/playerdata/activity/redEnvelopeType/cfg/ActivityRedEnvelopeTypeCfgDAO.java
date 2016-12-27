package com.playerdata.activity.redEnvelopeType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;













import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.redEnvelopeType.ActivityRedEnvelopeTypeEnum;
import com.playerdata.activity.redEnvelopeType.ActivityRedEnvelopeTypeMgr;
import com.playerdata.activity.redEnvelopeType.data.ActivityRedEnvelopeTypeItem;
import com.playerdata.activity.redEnvelopeType.data.ActivityRedEnvelopeTypeSubItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityRedEnvelopeTypeCfgDAO extends CfgCsvDao<ActivityRedEnvelopeTypeCfg> {


	
	public static ActivityRedEnvelopeTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityRedEnvelopeTypeCfgDAO.class);
	}
	
	
	
	
	@Override
	public Map<String, ActivityRedEnvelopeTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityRedEnvelopeTypeCfg.csv", ActivityRedEnvelopeTypeCfg.class);
		for (ActivityRedEnvelopeTypeCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
			
			
		}
		
		return cfgCacheMap;
	}
	
	private void parseTime(ActivityRedEnvelopeTypeCfg cfg){
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getStartTimeStr());
		cfg.setStartTime(startTime);
		
		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getEndTimeStr());
		cfg.setEndTime(endTime);
		
		long getRewardsTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getGetRewardsTimeStr());
		cfg.setGetRewardsTime(getRewardsTime);
		
	}
	
	
	
	public ActivityRedEnvelopeTypeCfg getConfig(String id){
		ActivityRedEnvelopeTypeCfg cfg = getCfgById(id);
		return cfg;
	}
	
	public ActivityRedEnvelopeTypeItem newItem(Player player, ActivityRedEnvelopeTypeCfg cfgById){
		if(cfgById!=null){
			ActivityRedEnvelopeTypeItem item = new ActivityRedEnvelopeTypeItem();
			item.setId(player.getUserId());
			item.setUserId(player.getUserId());
			item.setCfgId(cfgById.getId());
			item.setVersion(cfgById.getVersion());
			item.setLastTime(System.currentTimeMillis());
			int day = ActivityTypeHelper.getDayBy5Am(cfgById.getStartTime());
			item.setDay(day);
			item.setSubItemList(ActivityRedEnvelopeTypeCfgDAO.getInstance().getSubList(cfgById));
			return item;
		}else{
			return null;
		}
	}

	public List<ActivityRedEnvelopeTypeSubItem> getSubList(ActivityRedEnvelopeTypeCfg cfg) {
		List<ActivityRedEnvelopeTypeSubItem> subItemList = new ArrayList<ActivityRedEnvelopeTypeSubItem>();
		for(ActivityRedEnvelopeTypeSubCfg subCfg : ActivityRedEnvelopeTypeSubCfgDAO.getInstance().getAllCfg()){
			if(!StringUtils.equals(cfg.getId(), subCfg.getParantid())){
				continue;
			}
			ActivityRedEnvelopeTypeSubItem subItem = new ActivityRedEnvelopeTypeSubItem();
			subItem.setCfgId(subCfg.getId());
			subItem.setDay(subCfg.getDay());	
			subItemList.add(subItem);
			subItem.setDiscount(subCfg.getDiscount());
		}
		
		return subItemList;
	}

	public ActivityRedEnvelopeTypeCfg getCfgByItemOfVersion(ActivityRedEnvelopeTypeItem item){
		List<ActivityRedEnvelopeTypeCfg> allCfg = getAllCfg();
		List<ActivityRedEnvelopeTypeCfg> cfgOfOpen = new ArrayList<ActivityRedEnvelopeTypeCfg>();
		for(ActivityRedEnvelopeTypeCfg cfg : allCfg){
			if(!StringUtils.equals(item.getCfgId(), cfg.getId())&&ActivityRedEnvelopeTypeMgr.getInstance().isOpen(cfg)){
				cfgOfOpen.add(cfg);
			}
		}
		if(cfgOfOpen.size() > 1){
			GameLog.error(LogModule.ComActivityRedEnvelope, null, "多个同时激活的cfg",null);
			return null;
		}else if(cfgOfOpen.size() == 1){
			return cfgOfOpen.get(0);		
		}
		
		return null;
	}

}