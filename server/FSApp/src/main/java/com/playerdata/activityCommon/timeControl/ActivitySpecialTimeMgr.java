package com.playerdata.activityCommon.timeControl;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.playerdata.activityCommon.activityType.ActivityExtendTimeIF;
import com.playerdata.activityCommon.activityType.ActivityRangeTimeIF;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.playerdata.activityCommon.modifiedActivity.ActivityModifyMgr;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwproto.PlatformGSMsg.ActCfgInfo;
import com.rwproto.PlatformGSMsg.ActivityTimeInfo;
import com.rwproto.RequestProtos.Request;


public class ActivitySpecialTimeMgr {
	
	public static AtomicBoolean ISINIT = new AtomicBoolean(false);
	
	private static ActivitySpecialTimeMgr instance = new ActivitySpecialTimeMgr();
	
	public static ActivitySpecialTimeMgr getInstance() {
		return instance;
	}
	
	/**
	 * 登录服传输活动配置过来
	 * @param request
	 * @param ctx
	 */
	public void decodeActivityTimeInfo(Request request, ChannelHandlerContext ctx) {
		try {
			ActivityTimeInfo actCfgs = ActivityTimeInfo.parseFrom(request.getBody().getSerializedContent());
			reloadSpecialActivity(actCfgs);
			if(ISINIT.compareAndSet(false, true)){
				GameLog.info("Activity", "activity", "Get activity from Login server...");
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}
	
	private void reloadSpecialActivity(ActivityTimeInfo specialCfgs){
		List<Integer> modifiedCfgs = new ArrayList<Integer>();
		for(ActCfgInfo specialTimeInfo : specialCfgs.getActInfosList()){
			if(dispatchTimeToActivity(specialTimeInfo)){
				modifiedCfgs.add(specialTimeInfo.getCfgId());
			}
		}
		if(!modifiedCfgs.isEmpty()){
			ActivityModifyMgr.getInstance().synModifiedActivityToAll(modifiedCfgs);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean dispatchTimeToActivity(ActCfgInfo specialTimeInfo){
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
	
	private void modifyActivityCfg(ActivityCfgIF actCfg, ActCfgInfo specialTimecfg){
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
