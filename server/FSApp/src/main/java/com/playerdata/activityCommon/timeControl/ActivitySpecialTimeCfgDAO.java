package com.playerdata.activityCommon.timeControl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.playerdata.activityCommon.activityType.ActivityExtendTimeIF;
import com.playerdata.activityCommon.activityType.ActivityRangeTimeIF;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.playerdata.activityCommon.modifiedActivity.ActivityModifyMgr;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.manager.GameManager;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.playerdata.activityCommon.timeControl.ActivitySpecialTimeCfgDAO"  init-method="init" />

public class ActivitySpecialTimeCfgDAO extends CfgCsvDao<ActivitySpecialTimeCfg> {
	public static ActivitySpecialTimeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivitySpecialTimeCfgDAO.class);
	}

	@Override
	public Map<String, ActivitySpecialTimeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/TimeControl/ActivitySpecialTimeCfg.csv",ActivitySpecialTimeCfg.class);
		return cfgCacheMap;
	}
	
	public void reload(){
		super.reload();
		final int zoneId = GameManager.getZoneId();
		if(zoneId <= 0){
			return;
		}
		List<Integer> modifiedCfgs = new ArrayList<Integer>();
		for(ActivitySpecialTimeCfg cfg : cfgCacheMap.values()){
			if(StringUtils.isBlank(cfg.getZoneId())){
				continue;
			}
			String[] zoneSections = cfg.getZoneId().split(",");
			for(String section : zoneSections){
				int startZoneId = 0;
				int endZoneId = 0;
				String[] startAndEnd = section.split("_");
				if(2 == startAndEnd.length){
					startZoneId = Integer.parseInt(startAndEnd[0]);
					endZoneId = Integer.parseInt(startAndEnd[1]);
				}else if(1 == startAndEnd.length){
					startZoneId = Integer.parseInt(startAndEnd[0]);
					endZoneId = startZoneId;
				}else{
					GameLog.error("ActivitySpecialTimeCfgDAO-reload", "", "id[" + cfg.getId() + "]区号区间填写有误");
				}
				if(zoneId >= startZoneId && zoneId <= endZoneId){
					if(dispatchTimeToActivity(cfg)){
						modifiedCfgs.add(cfg.getCfgId());
					}
					break;
				}
			}
		}
		if(!modifiedCfgs.isEmpty()){
			ActivityModifyMgr.getInstance().synModifiedActivityToAll(modifiedCfgs);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean dispatchTimeToActivity(ActivitySpecialTimeCfg specialTimecfg){
		ActivityType actType = ActivityTypeFactory.getByCfgId(specialTimecfg.getCfgId());
		if(null == actType){
			GameLog.error("ActivitySpecialTimeCfgDAO-DispatchTimeToActivity", "", "id[" + specialTimecfg.getCfgId() + "]暂时不支持动态更换时间");
			return false;
		}
		CfgCsvDao<? extends ActivityCfgIF> cfgDao = actType.getActivityDao();
		if(null != cfgDao){
			ActivityCfgIF actCfg = cfgDao.getCfgById(String.valueOf(specialTimecfg.getCfgId()));
			if(null != actCfg){
				modifyActivityCfg(actCfg, specialTimecfg);
				return true;
			}
		}
		return false;
	}
	
	private void modifyActivityCfg(ActivityCfgIF actCfg, ActivitySpecialTimeCfg specialTimecfg){
		if(actCfg instanceof ActivityRangeTimeIF){
			((ActivityRangeTimeIF) actCfg).setRangeTime(specialTimecfg.getRangeTime());
		}
		if(actCfg instanceof ActivityExtendTimeIF){
			((ActivityExtendTimeIF) actCfg).setViceStartAndEndTime(specialTimecfg.getStartViceTime(), specialTimecfg.getEndViceTime());
		}
		actCfg.setStartAndEndTime(specialTimecfg.getStartTime(), specialTimecfg.getEndTime());
		actCfg.setActDesc(specialTimecfg.getActDesc());
	}
}
