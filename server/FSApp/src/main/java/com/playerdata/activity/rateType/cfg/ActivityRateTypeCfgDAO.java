package com.playerdata.activity.rateType.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.activity.rateType.data.ActivityRateTypeItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityRateTypeCfgDAO extends
		CfgCsvDao<ActivityRateTypeCfg> {

	public static ActivityRateTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityRateTypeCfgDAO.class);
	}
	
	private HashMap<String, List<ActivityRateTypeCfg>> cfgMapByEnumid ;
	
	@Override
	public Map<String, ActivityRateTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map(
				"Activity/ActivityRateTypeCfg.csv", ActivityRateTypeCfg.class);
		for (ActivityRateTypeCfg cfgTmp : cfgCacheMap.values()) {
			cfgTmp.ExtraInitAfterLoad();
			parseTimeByHour(cfgTmp);
			parseCopyTypeAndespecialEnum(cfgTmp);
		}
		
		HashMap<String, List<ActivityRateTypeCfg>> cfgMapByEnumidTemp = new HashMap<String, List<ActivityRateTypeCfg>>();
		for(ActivityRateTypeCfg cfg : cfgCacheMap.values()){
			ActivityTypeHelper.add(cfg, String.valueOf(cfg.getEnumId()), cfgMapByEnumidTemp);
		}
		this.cfgMapByEnumid = cfgMapByEnumidTemp;		
		return cfgCacheMap;
	}
	
	/**
	 * 
	 * @param cfgTmp copytype_especial#especial,copytype_especial
	 */
	private void parseCopyTypeAndespecialEnum(ActivityRateTypeCfg cfgTmp) {
		Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
		String[] copylist = cfgTmp.getCopytypeAndespecialitemidEnum().split(",");
		for(String copytypeAndEspecial : copylist){
			String[] copytypeOrEspecial = copytypeAndEspecial.split("_");
			String[] especials = copytypeOrEspecial[1].split("#");
			List<Integer> especialList = new ArrayList<Integer>();
			for(String especialItemId : especials){
				especialList.add(Integer.parseInt(especialItemId));
			}
			map.put(Integer.parseInt(copytypeOrEspecial[0]), especialList);			
		}
		cfgTmp.setCopyTypeMap(map);
	}

	private void parseTimeByHour(ActivityRateTypeCfg cfgTmp) {
		try {
			String[] startAndEndgroup = cfgTmp.getTimeStr().split(";");
			List<ActivityRateTypeStartAndEndHourHelper> timeList = cfgTmp.getStartAndEnd();
			for (String subStartAndEnd : startAndEndgroup) {
				String[] substartAndEndlist = subStartAndEnd.split(":");
				ActivityRateTypeStartAndEndHourHelper timebyHour = new ActivityRateTypeStartAndEndHourHelper();
				timebyHour.setStarthour(Integer.parseInt(substartAndEndlist[0]));
				timebyHour.setEndhour(Integer.parseInt(substartAndEndlist[1]));
				timeList.add(timebyHour);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public ActivityRateTypeItem newItem(Player player,
			ActivityRateTypeCfg cfgById) {
		if (cfgById != null) {
			ActivityRateTypeItem item = new ActivityRateTypeItem();
//			String itemId = ActivityRateTypeHelper.getItemId(
//					player.getUserId(), ActivityRateTypeEnum.getById(cfgById.getEnumId()));
			int id = Integer.parseInt(String.valueOf(cfgById.getEnumId()));
			item.setId(id);
			item.setCfgId(String.valueOf(cfgById.getId()));
			item.setEnumId(String.valueOf((cfgById.getEnumId())));
			item.setUserId(player.getUserId());
			item.setVersion(String.valueOf((cfgById.getVersion())));
			item.setMultiple(cfgById.getMultiple());
			return item;
		} else {
			return null;
		}

	}
	
	/**
	 *获取和传入数据同类型的，不同id的，处于激活状态的，单一新活动 
	 */
	public ActivityRateTypeCfg getCfgByEnumId(ActivityRateTypeItem item) {
		List<ActivityRateTypeCfg> cfgList = cfgMapByEnumid.get(item.getEnumId());
		if(cfgList == null || cfgList.isEmpty()){
			return null;
		}
		List<ActivityRateTypeCfg> cfgListByEnumID = new ArrayList<ActivityRateTypeCfg>();
		for(ActivityRateTypeCfg cfg : cfgList){
			if(!StringUtils.equals(item.getCfgId(), String.valueOf(cfg.getId()))){
				cfgListByEnumID.add(cfg);				
			}			
		}
		ActivityRateTypeMgr activityRateTypeMgr = ActivityRateTypeMgr.getInstance();
		List<ActivityRateTypeCfg> cfgListIsOpen = new ArrayList<ActivityRateTypeCfg>();
		for(ActivityRateTypeCfg cfg : cfgListByEnumID){
			if(activityRateTypeMgr.isOpen(cfg)){
				cfgListIsOpen.add(cfg);
			}			
		}
		
		if(cfgListIsOpen.size() > 1){
			GameLog.error(LogModule.ComActivityRate, null, "发现了两个以上开放的活动,活动枚举为="+ item.getCfgId(), null);
			return null;
		}else if(cfgListIsOpen.size() == 1){
			return cfgListIsOpen.get(0);
		}		
		return null;
	}

	public boolean hasCfgByEnumId(String enumId){
		List<ActivityRateTypeCfg> cfgList = cfgMapByEnumid.get(enumId);
		if(cfgList == null || cfgList.isEmpty()){
			return false;
		}
		return true;		
	}
	
}