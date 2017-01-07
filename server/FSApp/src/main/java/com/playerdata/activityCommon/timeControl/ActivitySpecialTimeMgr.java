package com.playerdata.activityCommon.timeControl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.log.GameLog;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.playerdata.activityCommon.activityType.ActivityExtendTimeIF;
import com.playerdata.activityCommon.activityType.ActivityRangeTimeIF;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.playerdata.activityCommon.modifiedActivity.ActivityModifyMgr;
import com.rw.fsutil.cacheDao.CfgCsvDao;


public class ActivitySpecialTimeMgr {
	
	public static AtomicInteger VERSION = new AtomicInteger(-1);
	
	private static ActivitySpecialTimeMgr instance = new ActivitySpecialTimeMgr();
	
	public static ActivitySpecialTimeMgr getInstance() {
		return instance;
	}
	
	/**
	 * 登录服传输活动配置过来
	 * @param request
	 * @param ctx
	 */
	public void decodeActivityTimeInfo(ActCfgInfo actTimeInfo) {
		try {
			if(null != actTimeInfo.getActList()){
				reloadSpecialActivity(actTimeInfo);
			}
			int oriVersion = VERSION.get();
			if(actTimeInfo.getPlatformVersion() == 0 || actTimeInfo.getPlatformVersion() > oriVersion){
				VERSION.set(actTimeInfo.getPlatformVersion());
			}
			if(oriVersion != actTimeInfo.getPlatformVersion()){
				GameLog.info("Activity", "activity", String.format("Get activity[%s] from Login server, last version[%s]...", actTimeInfo.getPlatformVersion(), oriVersion));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void reloadSpecialActivity(ActCfgInfo actTimeInfo){
		List<Integer> modifiedCfgs = new ArrayList<Integer>();
		for(SingleActTime specialTimeInfo : actTimeInfo.getActList()){
			if(dispatchTimeToActivity(specialTimeInfo)){
				modifiedCfgs.add(specialTimeInfo.getCfgId());
			}
		}
		if(!modifiedCfgs.isEmpty()){
			ActivityModifyMgr.getInstance().synModifiedActivityToAll(modifiedCfgs);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean dispatchTimeToActivity(SingleActTime specialTimeInfo){
		ActivityType actType = ActivityTypeFactory.getByCfgId(specialTimeInfo.getCfgId());
		if(null == actType){
			GameLog.error("ActivitySpecialTimeMgr-DispatchTimeToActivity", "", "id[" + specialTimeInfo.getCfgId() + "]暂时不支持动态更换时间");
			return false;
		}
		CfgCsvDao<? extends ActivityCfgIF> cfgDao = actType.getActivityDao();
		if(null != cfgDao){
			ActivityCfgIF actCfg = cfgDao.getCfgById(String.valueOf(specialTimeInfo.getCfgId()));
			if(null != actCfg){
				modifyActivityCfg(actCfg, specialTimeInfo);
				return true;
			}
		}
		return false;
	}
	
	private void modifyActivityCfg(ActivityCfgIF actCfg, SingleActTime specialTimeInfo){
		if(actCfg instanceof ActivityRangeTimeIF){
			((ActivityRangeTimeIF) actCfg).setRangeTime(specialTimeInfo.getRangeTime());
		}
		if(actCfg instanceof ActivityExtendTimeIF){
			((ActivityExtendTimeIF) actCfg).setViceStartAndEndTime(specialTimeInfo.getStartViceTime(), specialTimeInfo.getEndViceTime());
		}
		actCfg.setStartAndEndTime(specialTimeInfo.getStartTime(), specialTimeInfo.getEndTime());
		actCfg.setActDesc(specialTimeInfo.getActDesc());
	}
}
