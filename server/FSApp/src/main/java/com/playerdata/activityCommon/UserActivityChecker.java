package com.playerdata.activityCommon;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeCfg;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;


/**
 * 活动状态检测
 * 
 * @author aken
 */
@SuppressWarnings("rawtypes")
public abstract class UserActivityChecker<T extends RoleExtProperty> {
	
	private ActivityDetector detector = ActivityDetector.getInstance();
	
	/**
	 * 刷新活动
	 * @param userId
	 * @return
	 */
	public List<T> refreshActivity(String userId, ActivityType<?, T> type){
		List<T> afterRemove = removeExpireActivity(userId);
		List<T> newAdd = addNewActivity(userId);
		afterRemove.addAll(newAdd);
		return afterRemove;
	}
	
	/**
	 * 增加的活动
	 * @param userId
	 * @return 新添加的活动
	 */
	private List<T> addNewActivity(String userId){
		List<ActivityDailyChargeCfg> activeDailyList = detector.getAllActivityOfType(Class<T>);
		List<T> newAddItems = new ArrayList<T>();
		ActivityDailyChargeSubCfgDAO  chargeSubCfgDAO = ActivityDailyChargeSubCfgDAO.getInstance();
		RoleExtPropertyStore<T> itemStore = getItemStore(userId);
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
	private ArrayList<T> removeExpireActivity(String userId){
		List<Integer> removeList = new ArrayList<Integer>();
		Map<String, ActivityDailyRechargeTypeItem> activeItemMap = new HashMap<String, ActivityDailyRechargeTypeItem>();
		RoleExtPropertyStore<ActivityDailyRechargeTypeItem> rechargeStore = getItemStore(userId);
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

	/**
	 * 获取活动当前第几天
	 * 
	 * @return
	 */
	public int getCurrentDay(ActivityCfgIF cfg) {
		return (int) ((System.currentTimeMillis() - cfg.getStartTime()) / (24 * 60 * 60 * 1000)) + 1;
	}
	
	public abstract RoleExtPropertyStore<T> getItemStore(String userId);
	
	public abstract ActivityType getActivityType();
}
