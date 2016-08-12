package com.playerdata.activity.VitalityType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeEnum;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeMgr;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityVitalitySubCfgDAO extends CfgCsvDao<ActivityVitalitySubCfg> {


	public static ActivityVitalitySubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityVitalitySubCfgDAO.class);
	}

	
	@Override
	public Map<String, ActivityVitalitySubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityVitalityTypeSubCfg.csv", ActivityVitalitySubCfg.class);			
		return cfgCacheMap;
	}
	


	/**根据传入的活动类型来查找激活的子活动*/
	public ActivityVitalitySubCfg getByTypeAndActiveType(ActivityVitalityTypeEnum eNum,String subId){
		ActivityVitalitySubCfg target = null;
		if(eNum == ActivityVitalityTypeEnum.Vitality){
			target=getVitalityOne(subId);
		}
		if(eNum == ActivityVitalityTypeEnum.VitalityTwo){
			target=getViatlityTwo(subId);
		}
		return target;		
	}
	
	private ActivityVitalitySubCfg getVitalityOne(String subId) {
		List<ActivityVitalityCfg> cfgList = ActivityVitalityCfgDAO.getInstance().getAllCfg();
		List<ActivityVitalityCfg> openCfgList = new ArrayList<ActivityVitalityCfg>();
		for(ActivityVitalityCfg cfg : cfgList){
			if (StringUtils.equals(
					ActivityVitalityTypeEnum.Vitality.getCfgId(),
					cfg.getEnumId())
					&& ActivityVitalityTypeMgr.getInstance().isOpen(cfg)) {
				openCfgList.add(cfg);
			}			
		}
		if(openCfgList.size() > 1){
			GameLog.error(LogModule.ComActivityVitality, null, "单一功能event同时触发了多个cfg,eventenum=" + subId, null);
			return null;
		}
		if(openCfgList.isEmpty()){
			return null;
		}
		ActivityVitalityCfg cfg = openCfgList.get(0);		
		int day = ActivityVitalityCfgDAO.getInstance().getday() ;
		ActivityVitalitySubCfg target = null;
		List<ActivityVitalitySubCfg> allCfg = getAllCfg();
		for (ActivityVitalitySubCfg subcfg : allCfg) {
			if (StringUtils.equals(subcfg.getType(), subId)
					&& subcfg.getDay() == day
					&& StringUtils.equals(cfg.getId(), subcfg.getActiveType()
							+ "")) {
				target = subcfg;
				break;
			}
		}
		return target;
	}
	
	private ActivityVitalitySubCfg getViatlityTwo(String subId) {
		List<ActivityVitalityCfg> cfgList = ActivityVitalityCfgDAO.getInstance().getAllCfg();
		List<ActivityVitalityCfg> openCfgList = new ArrayList<ActivityVitalityCfg>();
		for(ActivityVitalityCfg cfg : cfgList){
			if (StringUtils.equals(
					ActivityVitalityTypeEnum.VitalityTwo.getCfgId(),
					cfg.getEnumId())
					&& ActivityVitalityTypeMgr.getInstance().isOpen(cfg)) {
				openCfgList.add(cfg);
			}			
		}
		if(openCfgList.size() > 1){
			GameLog.error(LogModule.ComActivityVitality, null, "单一功能event同时触发了多个cfg,eventenum=" + subId, null);
			return null;
		}
		if(openCfgList.isEmpty()){
			return null;
		}
		ActivityVitalityCfg cfg = openCfgList.get(0);	
		ActivityVitalitySubCfg target = null;
		List<ActivityVitalitySubCfg> allCfg = getAllCfg();
		for (ActivityVitalitySubCfg subcfg : allCfg) {
			if (StringUtils.equals(subcfg.getType(), subId)
					&& subcfg.getDay() == -1
					&& StringUtils.equals(cfg.getId(), subcfg.getActiveType()
							+ "")) {
				target = subcfg;
				break;
			}
		}		
		return target;
	}
}