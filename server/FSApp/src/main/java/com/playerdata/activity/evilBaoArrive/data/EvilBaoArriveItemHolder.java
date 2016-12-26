package com.playerdata.activity.evilBaoArrive.data;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;


public class EvilBaoArriveItemHolder extends UserActivityChecker<EvilBaoArriveItem>{
	
	protected EvilBaoArriveItemHolder(){
		super(EvilBaoArriveItem.class);
	}
	
	private static EvilBaoArriveItemHolder instance = new EvilBaoArriveItemHolder();
	
	public static EvilBaoArriveItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityEvilBaoArrive;
	
	public void updateItem(Player player, EvilBaoArriveItem item){
		getItemStore(player.getUserId()).update(item.getId());
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}

	public void synAllData(Player player){
		List<EvilBaoArriveItem> itemList = getItemList(player.getUserId());
		if(null != itemList && !itemList.isEmpty()){
			ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ActivityType getActivityType() {
		return ActivityTypeFactory.EvilBaoArrive;
	}

	@Override
	public PlayerExtPropertyType getExtPropertyType() {
		return PlayerExtPropertyType.ACTIVITY_EVILBAOARRIVE;
	}
}
