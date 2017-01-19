package com.playerdata.activity.VitalityType.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeEnum;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfgDAO;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityRewardCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityRewardCfgDAO;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalitySubCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalitySubCfgDAO;
import com.playerdata.activityCommon.ActivityDetector;
import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityVitalityItemHolder extends UserActivityChecker<ActivityVitalityTypeItem>{
	
	private static ActivityVitalityItemHolder instance = new ActivityVitalityItemHolder();
	
	public static ActivityVitalityItemHolder getInstance(){
		return instance;
	}
	
	public ActivityVitalityTypeItem getItem(String userId, ActivityVitalityTypeEnum acVitalityTypeEnum){
		int id = Integer.parseInt(acVitalityTypeEnum.getCfgId());
		return getItemStore(userId).get(id);
	}
	
	public List<ActivityVitalityTypeItem> getItemList(String userId){
		return refreshActivity(userId);
	}
	
	/**
	 * 增加的活动
	 * @param userId
	 * @return 新添加的活动
	 */
	@SuppressWarnings("unchecked")
	protected List<ActivityVitalityTypeItem> addNewActivity(String userId){
		List<? extends ActivityCfgIF> activeDailyList = ActivityDetector.getInstance().getAllActivityOfType(getActivityType());
		List<ActivityVitalityTypeItem> newAddItems = new ArrayList<ActivityVitalityTypeItem>();
		RoleExtPropertyStore<ActivityVitalityTypeItem> itemStore = getItemStore(userId);
		Player player = PlayerMgr.getInstance().find(userId);
		if(null == player){
			return newAddItems;
		}
		int playerLevel = player.getLevel();
		int playerVip = player.getVip();
		for(ActivityCfgIF cfg : activeDailyList){
			if(playerLevel < cfg.getLevelLimit() || playerVip < cfg.getVipLimit()){
				continue;
			}
			ActivityVitalityTypeItem item = itemStore.get(cfg.getId());
			if(null == item || Integer.parseInt(item.getCfgId()) != cfg.getCfgId()){
				if(null != item){
					itemStore.removeItem(cfg.getId());
				}
				// 有新增的活动
				item = new ActivityVitalityTypeItem();
				if(null != item){
					item.setId(cfg.getId());
					item.setCfgId(String.valueOf(cfg.getCfgId()));
					item.setUserId(userId);
					item.setVersion(cfg.getVersion());
					item.setCanGetReward(((ActivityVitalityCfg)cfg).isCanGetReward());
					item.setSubItemList(newSubItemList(String.valueOf(cfg.getCfgId())));
					item.setSubBoxItemList(newSubBoxItemList(String.valueOf(cfg.getCfgId())));
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

	@Override
	public List<ActivityVitalityTypeSubItem> newSubItemList(String cfgId) {
		ActivityVitalityCfg vitalityCfg = ActivityVitalityCfgDAO.getInstance().getCfgById(cfgId);
		if(null == vitalityCfg){
			return Collections.emptyList();
		}
		List<ActivityVitalityTypeSubItem> subItemList = new ArrayList<ActivityVitalityTypeSubItem>();
		List<String> todaySubs = getTodaySubActivity(cfgId);
		ActivityVitalitySubCfgDAO subDao = ActivityVitalitySubCfgDAO.getInstance();
		for(String subCfgId : todaySubs){
			ActivityVitalitySubCfg subCfg = subDao.getCfgById(subCfgId);
			ActivityVitalityTypeSubItem subItem = new ActivityVitalityTypeSubItem();
			subItem.setCfgId(String.valueOf(subCfg.getId()));
			subItem.setCount(0);
			subItem.setTaken(false);
			subItem.setGiftId(subCfg.getGiftId());
			subItem.setType(String.valueOf(subCfg.getActiveType()));
			subItemList.add(subItem);
		}
		return subItemList;
	}
	
	/**
	 * 获取对应活动的箱子奖励实体
	 * @param cfgId
	 * @return
	 */
	private List<ActivityVitalityTypeSubBoxItem> newSubBoxItemList(String cfgId){
		List<ActivityVitalityTypeSubBoxItem> subItemList = new ArrayList<ActivityVitalityTypeSubBoxItem>();
		List<ActivityVitalityRewardCfg> boxCfgs = getTodaySubBoxItemCfgs(cfgId);
		if(boxCfgs.isEmpty()){
			return subItemList;
		}
		for(ActivityVitalityRewardCfg activityVitalityRewardCfg : boxCfgs){		
			ActivityVitalityTypeSubBoxItem subitem = new ActivityVitalityTypeSubBoxItem();
			subitem.setCfgId(activityVitalityRewardCfg.getId());
			subitem.setCount(activityVitalityRewardCfg.getActivecount());
			subitem.setTaken(false);
			subitem.setGiftId(activityVitalityRewardCfg.getGiftId());
			subItemList.add(subitem);
		}
		return subItemList;
	}
	
	/**
	 * 获取对应活动的箱子奖励配置
	 * @param cfgID
	 * @return
	 */
	private List<ActivityVitalityRewardCfg> getTodaySubBoxItemCfgs(String cfgID){
		List<ActivityVitalityRewardCfg> todaySubs = new ArrayList<ActivityVitalityRewardCfg>();
		ActivityVitalityCfg cfg = ActivityVitalityCfgDAO.getInstance().getCfgById(cfgID);
		if(null == cfg) return todaySubs;
		List<ActivityVitalityRewardCfg> allCfgs = ActivityVitalityRewardCfgDAO.getInstance().getAllCfg();
		if(!ActivityDetector.getInstance().isActive(cfg)) return todaySubs;
		for(ActivityVitalityRewardCfg subCfg : allCfgs){
			if(subCfg.getActiveType() == cfg.getCfgId()){
				todaySubs.add(subCfg);
			}
		}
		return todaySubs;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public ActivityType getActivityType() {
		return ActivityTypeFactory.VitalityType;
	}

	@Override
	protected PlayerExtPropertyType getExtPropertyType() {
		return PlayerExtPropertyType.ACTIVITY_VITALITY;
	}

	@Override
	protected eSynType getSynType() {
		return eSynType.ActivityVitalityType;
	}
}
