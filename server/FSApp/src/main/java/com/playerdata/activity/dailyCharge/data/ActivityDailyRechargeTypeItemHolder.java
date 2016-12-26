package com.playerdata.activity.dailyCharge.data;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;


public class ActivityDailyRechargeTypeItemHolder extends UserActivityChecker<ActivityDailyRechargeTypeItem>{
	
	protected ActivityDailyRechargeTypeItemHolder(){
		super(ActivityDailyRechargeTypeItem.class);
	}
	
	private static ActivityDailyRechargeTypeItemHolder instance = new ActivityDailyRechargeTypeItemHolder();
	
	public static ActivityDailyRechargeTypeItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityDailyRechargeType;
	
	public void updateItem(Player player, ActivityDailyRechargeTypeItem item){
		getItemStore(player.getUserId()).update(item.getId());
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}

	public void synAllData(Player player){
		List<ActivityDailyRechargeTypeItem> itemList = getItemList(player.getUserId());
		if(null != itemList && !itemList.isEmpty()){
			ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ActivityType getActivityType() {
		return ActivityTypeFactory.DailyRecharge;
	}

	@Override
	public PlayerExtPropertyType getExtPropertyType() {
		return PlayerExtPropertyType.ACTIVITY_DAILYCHARGE;
	}
}
