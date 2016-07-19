package com.playerdata.activity.rateType.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.activity.rateType.ActivityRateTypeEnum;
import com.playerdata.activity.rateType.ActivityRateTypeHelper;
import com.playerdata.activity.rateType.data.ActivityRateTypeItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
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

	@Override
	public Map<String, ActivityRateTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map(
				"Activity/ActivityRateTypeCfg.csv", ActivityRateTypeCfg.class);
		for (ActivityRateTypeCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
			parseTimeByHour(cfgTmp);
			parseCopyTypeAndespecialEnum(cfgTmp);
		}
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
		
//		for(Map.Entry<Integer, List<Integer>> entry:map.entrySet()){
//			StringBuffer str = new StringBuffer();
//			for(Integer especial : entry.getValue()){
//				str.append(especial).append("#");
//			}
//			System.out.println(entry.getKey() + " value =" + str);
//		}
	}

	private void parseTimeByHour(ActivityRateTypeCfg cfgTmp) {
		try {
			String[] startAndEndgroup = cfgTmp.getTimeStr().split(";");
			List<ActivityRateTypeStartAndEndHourHelper> timeList = cfgTmp
					.getStartAndEnd();
			for (String subStartAndEnd : startAndEndgroup) {
				String[] substartAndEndlist = subStartAndEnd.split(":");
				ActivityRateTypeStartAndEndHourHelper timebyHour = new ActivityRateTypeStartAndEndHourHelper();
				timebyHour
						.setStarthour(Integer.parseInt(substartAndEndlist[0]) / 100);
				timebyHour
						.setEndhour(Integer.parseInt(substartAndEndlist[1]) / 100);
				timeList.add(timebyHour);
				// System.out.println("activityrate.." + cfgTmp.getTitle() +
				// " start=" +timebyHour.getStarthour() + " end=" +
				// timebyHour.getEndhour());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void parseTime(ActivityRateTypeCfg cfgItem) {
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem
				.getStartTimeStr());
		cfgItem.setStartTime(startTime);

		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem
				.getEndTimeStr());
		cfgItem.setEndTime(endTime);
	}



	public ActivityRateTypeItem newItem(Player player,
			ActivityRateTypeEnum typeEnum) {

		String cfgId = typeEnum.getCfgId();
		ActivityRateTypeCfg cfgById = getCfgById(cfgId);
		if (cfgById != null) {
			ActivityRateTypeItem item = new ActivityRateTypeItem();
			String itemId = ActivityRateTypeHelper.getItemId(
					player.getUserId(), typeEnum);
			item.setId(itemId);
			item.setCfgId(cfgId);
			item.setUserId(player.getUserId());
			item.setVersion(cfgById.getVersion());
			item.setMultiple(cfgById.getMultiple());
			return item;
		} else {
			return null;
		}

	}

}