package com.playerdata.activity.VitalityType.cfg;

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
		if(eNum == ActivityVitalityTypeEnum.Vitality){
			target=getVitalityOne(subId);
		}
		if(eNum == ActivityVitalityTypeEnum.VitalityTwo){
			target=getViatlityTwo(subId);
		}
		return target;		
	}
	
	private ActivityVitalitySubCfg getViatlityTwo(String subId) {
		List<ActivityVitalityCfg> cfgList = ActivityVitalityCfgDAO.getInstance().getAllCfg();
		ActivityVitalityCfg cfg = null;
		for(ActivityVitalityCfg cfgtmp : cfgList){
			if(StringUtils.equals(ActivityVitalityTypeEnum.VitalityTwo.getCfgId(),cfgtmp.getId() )){
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
		ActivityVitalitySubCfg target = null;
		List<ActivityVitalitySubCfg> allCfg = getAllCfg();
		for (ActivityVitalitySubCfg subcfg : allCfg) {
			if (StringUtils.equals(subcfg.getType(), subId)&&subcfg.getDay() == -1) {
				target = subcfg;
				break;
			}
		}		
		return target;
	}
	
	
	
	private ActivityVitalitySubCfg getVitalityOne(String subId) {
		List<ActivityVitalityCfg> cfgList = ActivityVitalityCfgDAO.getInstance().getAllCfg();
		ActivityVitalityCfg cfg = null;
		for(ActivityVitalityCfg cfgtmp : cfgList){
			if(StringUtils.equals(ActivityVitalityTypeEnum.Vitality.getCfgId(),cfgtmp.getId() )){
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
		int day = ActivityVitalityCfgDAO.getInstance().getday();// getday方法必须在活动开启时才可有效传入参数,故需先用isopen来判断
		ActivityVitalitySubCfg target = new ActivityVitalitySubCfg();
		List<ActivityVitalitySubCfg> allCfg = getAllCfg();
		for (ActivityVitalitySubCfg subcfg : allCfg) {
			if (StringUtils.equals(subcfg.getType(), subId) && subcfg.getDay() == day) {
				target = subcfg;
				break;
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
}