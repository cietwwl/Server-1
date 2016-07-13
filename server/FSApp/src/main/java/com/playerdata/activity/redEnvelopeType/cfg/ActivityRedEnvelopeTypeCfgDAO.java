package com.playerdata.activity.redEnvelopeType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;









import com.playerdata.Player;
import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.redEnvelopeType.ActivityRedEnvelopeTypeEnum;
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
	
	public ActivityRedEnvelopeTypeItem newItem(Player player, ActivityRedEnvelopeTypeEnum typeEnum){
		
		String cfgId = typeEnum.getCfgId();
		ActivityRedEnvelopeTypeCfg cfgById = getCfgById(cfgId );
		if(cfgById!=null){
			ActivityRedEnvelopeTypeItem item = new ActivityRedEnvelopeTypeItem();
			item.setId(player.getUserId());
			item.setUserId(player.getUserId());
			item.setCfgId(cfgId);
			item.setVersion(cfgById.getVersion());
			item.setLastTime(System.currentTimeMillis());
			int day = ActivityTypeHelper.getDayBy5Am(cfgById.getStartTime());
			item.setDay(day);
			item.setSubItemList(ActivityRedEnvelopeTypeCfgDAO.getInstance().getSubList());
			return item;
		}else{
			return null;
		}
	}

	public List<ActivityRedEnvelopeTypeSubItem> getSubList() {
		List<ActivityRedEnvelopeTypeSubItem> subItemList = new ArrayList<ActivityRedEnvelopeTypeSubItem>();
		for(ActivityRedEnvelopeTypeSubCfg subCfg : ActivityRedEnvelopeTypeSubCfgDAO.getInstance().getAllCfg()){
			ActivityRedEnvelopeTypeSubItem subItem = new ActivityRedEnvelopeTypeSubItem();
			subItem.setCfgId(subCfg.getId());
			subItem.setDay(subCfg.getDay());	
			subItemList.add(subItem);
		}
		
		return subItemList;
	}

	

}