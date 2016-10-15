package com.playerdata.activity.dailyCharge.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.dailyCharge.ActivityDetector;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeCfg;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeSubCfgDAO;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityDailyRechargeTypeItemHolder{
	
	private static ActivityDailyRechargeTypeItemHolder instance = new ActivityDailyRechargeTypeItemHolder();
	
	public static ActivityDailyRechargeTypeItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityDailyRechargeType;
	
	public List<ActivityDailyRechargeTypeItem> getItemList(String userId){
		return refreshDailyRecharge(userId);
	}
	
	public void updateItem(Player player, ActivityDailyRechargeTypeItem item){
		getItemStore(player.getUserId()).update(item.getId());
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public ActivityDailyRechargeTypeItem getItem(String userId, String cfgId){		
//		String itemId = getActivityID(cfgId, userId);
//		return getItemStore(userId).getItem(itemId);
		int id = Integer.parseInt(cfgId);
		return getItemStore(userId).get(id);
		
	}

	public void synAllData(Player player){
		List<ActivityDailyRechargeTypeItem> itemList = getItemList(player.getUserId());
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	/**
	 * 刷新活动
	 * @param userId
	 * @return
	 */
	public List<ActivityDailyRechargeTypeItem> refreshDailyRecharge(String userId){
		List<ActivityDailyRechargeTypeItem> afterRemove = removeExpireActivity(userId);
		List<ActivityDailyRechargeTypeItem> newAdd = addNewActivity(userId);
		afterRemove.addAll(newAdd);
		return afterRemove;
	}
	
	/**
	 * 增加的活动
	 * @param userId
	 * @return 新添加的活动
	 */
	private List<ActivityDailyRechargeTypeItem> addNewActivity(String userId){	
		List<ActivityDailyChargeCfg> activeDailyList = ActivityDetector.getInstance().getAllDailyActivity();
		List<ActivityDailyRechargeTypeItem> newAddItems = new ArrayList<ActivityDailyRechargeTypeItem>();
		ActivityDailyChargeSubCfgDAO  chargeSubCfgDAO = ActivityDailyChargeSubCfgDAO.getInstance();
		PlayerExtPropertyStore<ActivityDailyRechargeTypeItem> itemStore = getItemStore(userId);
		for(ActivityDailyChargeCfg cfg : activeDailyList){
//			String activityID = getActivityID(String.valueOf(cfg.getId()), userId);
			ActivityDailyRechargeTypeItem item = itemStore.get(cfg.getId());
			if(null == item){
				// 有新增的活动
				item = new ActivityDailyRechargeTypeItem();
				item.setId(cfg.getId());
				item.setCfgId(String.valueOf(cfg.getId()));
				item.setUserId(userId);
				item.setVersion(cfg.getVersion());
				List<ActivityDailyRechargeTypeSubItem> subItemList = new ArrayList<ActivityDailyRechargeTypeSubItem>();
				List<String> todaySubs = chargeSubCfgDAO.getTodaySubActivity(String.valueOf(cfg.getId()));
				for(String subId : todaySubs){
					ActivityDailyRechargeTypeSubItem subItem = new ActivityDailyRechargeTypeSubItem();
					subItem.setCfgId(subId);
					subItem.setGet(false);
					subItemList.add(subItem);
				}
				item.setSubItemList(subItemList);
				newAddItems.add(item);
			}
		}
		try {
			itemStore.addItem(newAddItems);
		} catch (DuplicatedKeyException e) {
			e.printStackTrace();
		}
		return newAddItems;
	}
	
	/**
	 * 删除过期的活动
	 * @param userId
	 * @return 在有效期内的活动
	 */
	private ArrayList<ActivityDailyRechargeTypeItem> removeExpireActivity(String userId){
		List<Integer> removeList = new ArrayList<Integer>();
		Map<String, ActivityDailyRechargeTypeItem> activeItemMap = new HashMap<String, ActivityDailyRechargeTypeItem>();
		PlayerExtPropertyStore<ActivityDailyRechargeTypeItem> rechargeStore = getItemStore(userId);
		Enumeration<ActivityDailyRechargeTypeItem> mapEnum = rechargeStore.getExtPropertyEnumeration();
		ActivityDetector detector = ActivityDetector.getInstance();
		while (mapEnum.hasMoreElements()) {
			ActivityDailyRechargeTypeItem item = mapEnum.nextElement();
			boolean isActive = detector.containsActivity(item.getCfgId());
			if(isActive){
				activeItemMap.put(item.getId()+"", item);
			}else{
				//TODO 需要添加活动结束的事件
				item.setClosed(true);
				removeList.add(item.getId());
			}
		}
		if(!removeList.isEmpty()) rechargeStore.removeItem(removeList);
		return new ArrayList<ActivityDailyRechargeTypeItem>(activeItemMap.values());
	}
	
	private String getActivityID(String cfgId, String userId){
		return cfgId + "_" + userId;
	}
	
	private PlayerExtPropertyStore<ActivityDailyRechargeTypeItem> getItemStore(String userId) {
		RoleExtPropertyStoreCache<ActivityDailyRechargeTypeItem> storeCache = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_DAILYCHARGE, ActivityDailyRechargeTypeItem.class);
		try {
			return storeCache.getStore(userId);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
