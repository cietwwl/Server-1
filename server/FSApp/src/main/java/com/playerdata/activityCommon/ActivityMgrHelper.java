package com.playerdata.activityCommon;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.playerdata.activityCommon.modifiedActivity.ActivityModifyMgr;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ActivityMgrHelper {
	
	private static ActivityMgrHelper instance = new ActivityMgrHelper();

	public static ActivityMgrHelper getInstance() {
		return instance;
	}
	
	/**
	 * 同步活动数据
	 * @param player
	 */
	public void synActivityData(Player player){
		ActivityModifyMgr.getInstance().synModifiedActivity(player);
		for(ActivityType type : ActivityTypeFactory.getAllTypes()){
			type.getActivityMgr().synDataWithoutEmpty(player);
		}
	}
	
	/**
	 * 检查活动是否开启
	 * @param player
	 */
	public void checkActivity(Player player){
		for(ActivityType type : ActivityTypeFactory.getAllTypes()){
			type.getActivityMgr().checkActivityOpen(player);
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
			redPointList.addAll(type.getActivityMgr().haveRedPoint(player));
		}
		return redPointList;
	}
	
	/**
	 * 每日刷新
	 * @param player
	 */
	public void dailyRefreshNewDaySubActivity(Player player) {
		for(ActivityType type : ActivityTypeFactory.getAllTypes()){
			type.getActivityMgr().dailyRefreshNewDaySubActivity(player);
		}
	}
	
	/**
	 * 设置活动是否被查看的红点
	 */
	public void updateRedPoint(Player player, String eNum) {
		for(ActivityType type : ActivityTypeFactory.getAllTypes()){
			type.getActivityMgr().updateRedPoint(player, eNum);
		}
	}
}
