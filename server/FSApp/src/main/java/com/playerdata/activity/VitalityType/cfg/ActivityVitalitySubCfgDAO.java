package com.playerdata.activity.VitalityType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;


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
		List<ActivityVitalityCfg> cfgList = ActivityVitalityCfgDAO.getInstance().getAllCfg();
		ActivityVitalityCfg cfg = null;
		for(ActivityVitalityCfg cfgtmp : cfgList){
			if(StringUtils.equals(eNum.getCfgId(),cfgtmp.getId() )){
				cfg = cfgtmp;
				break;						
			}
		}
		if(cfg == null){
			return null;
		}
		if (!ActivityVitalityTypeMgr.getInstance().isOpen(cfg)) {
			// 活动未开启,不计数
			return null;
		}
		int day = ActivityVitalityCfgDAO.getInstance().getday() ;
		List<ActivityVitalitySubCfg> allSubCfgList = getCfgListByEnum(eNum);
		for(ActivityVitalitySubCfg subCfg : allSubCfgList){
			if(StringUtils.equals(subCfg.getType(), subId)){
				if(eNum == ActivityVitalityTypeEnum.VitalityTwo&&subCfg.getDay() == -1){
					target = subCfg;
					break;
				}else if(eNum == ActivityVitalityTypeEnum.Vitality&&subCfg.getDay() == day){
					target = subCfg;
					break;
				}
			}
		}	
		return target;		
	}
	
	//根据传入的id来获得子活动
	public ActivityVitalitySubCfg getById(String subId){
		ActivityVitalitySubCfg target = new ActivityVitalitySubCfg();
		List<ActivityVitalitySubCfg> allCfg = getAllCfg();
		for (ActivityVitalitySubCfg cfg : allCfg) {
			if(StringUtils.equals(cfg.getId(), subId)){
				target = cfg;
				break;
			}
		}		
		return target;
	}


	public List<ActivityVitalitySubCfg> getCfgListByEnum(
			ActivityVitalityTypeEnum eNum) {
		List<ActivityVitalitySubCfg> allSubCfgList = ActivityVitalitySubCfgDAO.getInstance().getAllCfg();
		List<ActivityVitalitySubCfg> subEnumCfgList  = new ArrayList<ActivityVitalitySubCfg>();
		for(ActivityVitalitySubCfg subCfg : allSubCfgList){
			if(!StringUtils.equals(eNum.getCfgId(), subCfg.getActiveType()+"")){
				continue;
			}
				subEnumCfgList.add(subCfg);
		}		
		return subEnumCfgList;
	}
}