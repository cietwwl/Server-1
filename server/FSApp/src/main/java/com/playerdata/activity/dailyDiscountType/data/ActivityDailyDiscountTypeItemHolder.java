package com.playerdata.activity.dailyDiscountType.data;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountItemCfg;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountItemCfgDao;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountTypeCfg;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountTypeSubCfg;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountTypeSubCfgDAO;
import com.playerdata.activityCommon.ActivityDetector;
import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityDailyDiscountTypeItemHolder extends UserActivityChecker<ActivityDailyDiscountTypeItem>{
	
	private static ActivityDailyDiscountTypeItemHolder instance = new ActivityDailyDiscountTypeItemHolder();
	
	public static ActivityDailyDiscountTypeItemHolder getInstance(){
		return instance;
	}
	
	/**
	 * 增加的活动
	 * @param userId
	 * @return 新添加的活动
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected List<ActivityDailyDiscountTypeItem> addNewActivity(String userId){
		List<? extends ActivityCfgIF> activeDailyList = ActivityDetector.getInstance().getAllActivityOfType(getActivityType());
		List<ActivityDailyDiscountTypeItem> newAddItems = new ArrayList<ActivityDailyDiscountTypeItem>();
		RoleExtPropertyStore<ActivityDailyDiscountTypeItem> itemStore = getItemStore(userId);
		Player player = PlayerMgr.getInstance().find(userId);
		for(ActivityCfgIF cfg : activeDailyList){
			if(null != player && player.getLevel() < cfg.getLevelLimit() && player.getVip() < cfg.getVipLimit()){
				continue;
			}
			ActivityDailyDiscountTypeItem item = itemStore.get(cfg.getId());
			if(null == item || Integer.parseInt(item.getCfgId()) != cfg.getCfgId()){
				if(null != item){
					itemStore.removeItem(cfg.getId());
				}
				// 有新增的活动
				item = new ActivityDailyDiscountTypeItem();
				if(null != item){
					item.setId(cfg.getId());
					item.setCfgId(String.valueOf(cfg.getCfgId()));
					item.setUserId(userId);
					item.setVersion(cfg.getVersion());
					item.setSubItemList(newSubItemList((ActivityDailyDiscountTypeCfg)cfg));
					newAddItems.add(item);
				}
			}
		}
		try {
			itemStore.addItem(newAddItems);
		} catch (DuplicatedKeyException e) {
			e.printStackTrace();
		}
		return newAddItems;
	}
	
	public List<ActivityDailyDiscountTypeSubItem> newSubItemList(ActivityDailyDiscountTypeCfg cfg){
		List<ActivityDailyDiscountTypeSubItem> subItemList = new ArrayList<ActivityDailyDiscountTypeSubItem>();
		if(null == cfg){
			return subItemList;
		}
		List<String> todaySubs = getTodaySubActivity(String.valueOf(cfg.getCfgId()));
		ActivityDailyDiscountTypeSubCfgDAO subDao = ActivityDailyDiscountTypeSubCfgDAO.getInstance();
		ActivityDailyDiscountItemCfgDao activityDailyDiscountItemCfgDao = ActivityDailyDiscountItemCfgDao.getInstance();
		for(String subCfgId : todaySubs){
			ActivityDailyDiscountTypeSubCfg subCfg = subDao.getCfgById(subCfgId);
			for(Integer itemId: subCfg.getItemList()){
				ActivityDailyDiscountItemCfg itemCfg = activityDailyDiscountItemCfgDao.getCfgById(String.valueOf(itemId));
				if(itemCfg == null){
					continue;
				}
				ActivityDailyDiscountTypeSubItem subitem = new ActivityDailyDiscountTypeSubItem();
				subitem.setCfgId(itemCfg.getId());
				subitem.setItemId(itemCfg.getItemId());
				subitem.setItemNum(itemCfg.getItemNum());
				subitem.setCount(0);
				subItemList.add(subitem);
			}
		}
		return subItemList;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ActivityType getActivityType() {
		return ActivityTypeFactory.DailyDiscount;
	}

	@Override
	protected PlayerExtPropertyType getExtPropertyType() {
		return PlayerExtPropertyType.ACTIVITY_DAILYDISCOUNT;
	}
	
	@Override
	protected eSynType getSynType() {
		return eSynType.ActivityDailyDiscountType;
	}
}
