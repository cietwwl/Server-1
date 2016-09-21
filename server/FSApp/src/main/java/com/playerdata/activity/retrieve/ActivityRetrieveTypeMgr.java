package com.playerdata.activity.retrieve;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.countType.data.ActivityCountTypeItemHolder;
import com.playerdata.activity.retrieve.data.ActivityRetrieveTypeHolder;
import com.playerdata.activity.retrieve.data.RewardBackItem;
import com.playerdata.activity.retrieve.data.RewardBackSubItem;
import com.playerdata.activity.retrieve.data.RewardBackTodaySubItem;
import com.playerdata.activity.retrieve.userFeatures.UserFeatruesMgr;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;

public class ActivityRetrieveTypeMgr {
	
	private static ActivityRetrieveTypeMgr instance = new ActivityRetrieveTypeMgr();
	
	public static ActivityRetrieveTypeMgr getInstance(){
		return instance;
	}
	
	public void synCountTypeData(Player player) {
		ActivityRetrieveTypeHolder.getInstance().synAllData(player);
	}
	
	
	/**
	 * 类月卡和在线礼包模式，登陆生成，每日更新
	 */
	public void checkActivityOpen(Player player){
		checkNewOpen(player);
		checkOtherDay(player);
	}
	
	private void checkNewOpen(Player player) {
		ActivityRetrieveTypeHolder dataHolder = new ActivityRetrieveTypeHolder();
		String userId = player.getUserId();
		List<RewardBackItem> addItemList = creatItems(userId, dataHolder.getItemStore(userId));
		if(addItemList != null){
			dataHolder.addItemList(player, addItemList);
		}			
	}

	public List<RewardBackItem> creatItems(String userId,MapItemStore<RewardBackItem> itemStore){		
		List<RewardBackItem> addItemList = null;		
		String itemId = ActivityRetrieveTypeHelper.getItemId(userId, ActivityRetrieveTypeEnum.retrieve);
		if(itemStore == null){
			return addItemList;
		}
		if(itemStore.getItem(itemId) != null){
			return addItemList;
		}
		RewardBackItem item = new RewardBackItem();
		item.setId(itemId);
		item.setUserId(userId);
		item.setLastSingleTime(System.currentTimeMillis());
		List<RewardBackTodaySubItem> subTodayItemList = new ArrayList<RewardBackTodaySubItem>();
		subTodayItemList = UserFeatruesMgr.getInstance().doCreat(userId);
		item.setTodaySubitemList(subTodayItemList);
		List<RewardBackSubItem> subItemList = new ArrayList<RewardBackSubItem>();
		item.setSubList(subItemList);
		if(addItemList == null){
			addItemList = new ArrayList<RewardBackItem>();
		}
		addItemList.add(item);
		return addItemList;
	}
	
	private void checkOtherDay(Player player) {
		ActivityRetrieveTypeHolder dataHolder = new ActivityRetrieveTypeHolder();
		List<RewardBackItem> itemList = dataHolder.getItemList(player.getUserId());
		if(itemList == null){
			return;
		}
		for(RewardBackItem item : itemList){
//			if (ActivityTypeHelper.isNewDayHourOfActivity(5, targetItem.getLastTime())) {
//				sendEmailIfGiftNotTaken(player, targetItem.getSubItemList());
//				targetItem.reset(targetCfg);
//				dataHolder.updateItem(player, targetItem);
//			}
			if(ActivityTypeHelper.isNewDayHourOfActivity(5, item.getLastSingleTime())){
				List<RewardBackSubItem> subItemList = new ArrayList<RewardBackSubItem>();
				subItemList = UserFeatruesMgr.getInstance().doFresh(player.getUserId(),item.getTodaySubitemList());
				item.setSubList(subItemList);
				List<RewardBackTodaySubItem> subTodayItemList = new ArrayList<RewardBackTodaySubItem>();
				subTodayItemList = UserFeatruesMgr.getInstance().doCreat(player.getUserId());
				item.setTodaySubitemList(subTodayItemList);
				item.setLastSingleTime(System.currentTimeMillis());
				dataHolder.updateItem(player, item);
			}			
		}		
	}	
}
