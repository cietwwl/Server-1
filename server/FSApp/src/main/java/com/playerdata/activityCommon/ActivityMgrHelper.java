package com.playerdata.activityCommon;

import java.util.ArrayList;
import java.util.List;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.playerdata.activityCommon.modifiedActivity.ActivityModifyItem;
import com.playerdata.activityCommon.modifiedActivity.ActivityModifyMgr;
import com.rw.fsutil.cacheDao.CfgCsvDao;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ActivityMgrHelper {
	
	private static ActivityMgrHelper instance = new ActivityMgrHelper();

	public static ActivityMgrHelper getInstance() {
		return instance;
	}
	
	/**
	 * 初始化活动数据的时间（配置表加载的时候会初始化一次，但那次可能没有开服时间，所以需要再初始化一次）
	 * @param player
	 */
	public void initActivityTime(){
		for(ActivityType type : ActivityTypeFactory.getAllTypes()){
			CfgCsvDao<? extends ActivityCfgIF> cfgDao = type.getActivityDao();
			if(null != cfgDao){
				List<? extends ActivityCfgIF> cfgList = cfgDao.getAllCfg();
				for(ActivityCfgIF cfg : cfgList){
					cfg.ExtraInitAfterLoad();
				}
			}
		}
	}
	
	/**
	 * 同步活动数据
	 * @param player
	 */
	public void synActivityData(Player player, List<ActivityModifyItem> modifiedList){
		ActivityModifyMgr.getInstance().synModifiedActivity(player, modifiedList);
		for(ActivityType type : ActivityTypeFactory.getAllTypes()){
			if(null != type.getActivityMgr()){
				type.getActivityMgr().synDataWithoutEmpty(player);
			}
		}
	}
	
	/**
	 * 检查活动是否开启
	 * @param player
	 */
	public void checkActivity(Player player){
		for(ActivityType type : ActivityTypeFactory.getAllTypes()){	
			if(null != type.getActivityMgr()){
				type.getActivityMgr().checkActivityOpen(player);
			}
		}
	}
	
	/**
	 * 红点判断
	 * @param player
	 * @return
	 */
	public List<String> haveRedPoint(Player player){
		List<String> redPointList = new ArrayList<String>();
		for(ActivityType type : ActivityTypeFactory.getAllTypes()){
			if(null != type.getActivityMgr()){
				redPointList.addAll(type.getActivityMgr().haveRedPoint(player));
			}
		}
		return redPointList;
	}
	
	/**
	 * 每日刷新
	 * @param player
	 */
	public void dailyRefreshNewDaySubActivity(Player player) {
		for(ActivityType type : ActivityTypeFactory.getAllTypes()){
			if(null != type.getActivityMgr()){
				try{
					type.getActivityMgr().dailyRefreshNewDaySubActivity(player);
				}catch(Exception ex){
					GameLog.error(LogModule.ComActivity, "ActivityMgrHelper-dailyRefreshNewDaySubActivity", "活动每日刷新的时候，出现异常", ex);
				}
			}
		}
	}
	
	/**
	 * 设置活动是否被查看的红点
	 */
	public void updateRedPoint(Player player, String eNum) {
		for(ActivityType type : ActivityTypeFactory.getAllTypes()){
			if(null != type.getActivityMgr()){
				type.getActivityMgr().updateRedPoint(player, eNum);
			}
		}
	}
}
