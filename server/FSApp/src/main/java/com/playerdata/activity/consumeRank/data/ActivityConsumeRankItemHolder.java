package com.playerdata.activity.consumeRank.data;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;


public class ActivityConsumeRankItemHolder extends UserActivityChecker<ActivityConsumeRankItem>{
	
	private static ActivityConsumeRankItemHolder instance = new ActivityConsumeRankItemHolder();
	
	public static ActivityConsumeRankItemHolder getInstance(){
		return instance;
	}

	//final private eSynType synType = eSynType.ActivityConsumeRank;
	
	public void updateItem(Player player, ActivityConsumeRankItem item){
		getItemStore(player.getUserId()).update(item.getId());
		//ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}

	public void synAllData(Player player){
		List<ActivityConsumeRankItem> itemList = getItemList(player.getUserId());
		if(null != itemList && !itemList.isEmpty()){
			//ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ActivityType getActivityType() {
		return ActivityTypeFactory.ConsumeRank;
	}

	@Override
	public PlayerExtPropertyType getExtPropertyType() {
		return PlayerExtPropertyType.ACTIVITY_CONSUME_RANK;
	}
}
