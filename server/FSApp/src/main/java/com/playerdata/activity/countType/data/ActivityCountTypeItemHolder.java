package com.playerdata.activity.countType.data;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityCountTypeItemHolder extends UserActivityChecker<ActivityCountTypeItem>{
	
	private static ActivityCountTypeItemHolder instance = new ActivityCountTypeItemHolder();

	public static ActivityCountTypeItemHolder getInstance() {
		return instance;
	}

	final private eSynType synType = eSynType.ActivityCountType;

	public void updateItem(Player player, ActivityCountTypeItem item) {
		getItemStore(player.getUserId()).update(item.getId());
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}

	public ActivityCountTypeItem getItem(String userId, ActivityCountTypeEnum countTypeEnum){		
		int id = Integer.parseInt(countTypeEnum.getCfgId());
		return getItemStore(userId).get(id);
	}

	public void synAllData(Player player) {
		List<ActivityCountTypeItem> itemList = getItemList(player.getUserId());
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ActivityType getActivityType() {
		return ActivityTypeFactory.CountType;
	}

	@Override
	public PlayerExtPropertyType getExtPropertyType() {
		return PlayerExtPropertyType.ACTIVITY_COUNTTYPE;
	}
}
