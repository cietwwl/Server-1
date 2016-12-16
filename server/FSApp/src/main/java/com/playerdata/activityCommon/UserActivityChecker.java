package com.playerdata.activityCommon;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.playerdata.activityCommon.activityType.ActivitySubCfgIF;
import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeItemIF;
import com.playerdata.activityCommon.activityType.ActivityTypeSubItemIF;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;


/**
 * 活动状态检测
 * 
 * @author aken
 */
@SuppressWarnings("rawtypes")
public abstract class UserActivityChecker<T extends ActivityTypeItemIF> {
	
	private Class<T> clazz;
	private final static long ONE_DAY_MS = 24 * 60 * 60 * 1000L;
	
	public void updateItem(Player player, T item){
		getItemStore(player.getUserId()).update(item.getId());
		if(null != getSynType()){
			ClientDataSynMgr.updateData(player, item, getSynType(), eSynOpType.UPDATE_SINGLE);
		}
	}
	
	public void updateItem(Player player, List<T> items){
		List<Integer> ids = new ArrayList<Integer>();
		for(T item : items){
			ids.add(item.getId());
		}
		getItemStore(player.getUserId()).updateItems(ids);
		if(null != getSynType()){
			ClientDataSynMgr.updateData(player, items, getSynType(), eSynOpType.UPDATE_PART_LIST);
		}
	}

	/**
	 * 同步活动数据
	 * <note>没数据的时候也同步</note>
	 * @param player
	 */
	public void synAllData(Player player){
		List<T> itemList = getItemList(player.getUserId());
		if(null != itemList && null != getSynType()){
			ClientDataSynMgr.synDataList(player, itemList, getSynType(), eSynOpType.UPDATE_LIST);
		}
	}
	
	/**
	 * 当数据为空时，不同步（表示活动未开启）
	 * <note>只用于登录的时候</note>
	 * @param player
	 */
	public void synAllDataWithoutEmpty(Player player){
		List<T> itemList = getItemList(player.getUserId());
		if(null != itemList && !itemList.isEmpty() && null != getSynType()){
			ClientDataSynMgr.synDataList(player, itemList, getSynType(), eSynOpType.UPDATE_LIST);
		}
	}
	
	public List<T> getItemList(String userId){
		return refreshActivity(userId);
	}
	
	public T getItem(String userId, String actId){		
		int id = Integer.parseInt(actId);
		return getItemStore(userId).get(id);
	}
	
	/**
	 * 刷新活动
	 * @param userId
	 * @return
	 */
	public List<T> refreshActivity(String userId){
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
	@SuppressWarnings("unchecked")
	protected List<T> addNewActivity(String userId){
		List<? extends ActivityCfgIF> activeDailyList = ActivityDetector.getInstance().getAllActivityOfType(getActivityType());
		List<T> newAddItems = new ArrayList<T>();
		RoleExtPropertyStore<T> itemStore = getItemStore(userId);
		Player player = PlayerMgr.getInstance().find(userId);
		if(null == player){
			return newAddItems;
		}
		int playerLevel = player.getLevel();
		int playerVip = player.getVip();
		for(ActivityCfgIF cfg : activeDailyList){
			if(playerLevel < cfg.getLevelLimit() && playerVip < cfg.getVipLimit()){
				continue;
			}
			T item = itemStore.get(cfg.getId());
			if(null == item || Integer.parseInt(item.getCfgId()) != cfg.getCfgId()){
				if(null != item){
					itemStore.removeItem(cfg.getId());
				}
				// 有新增的活动
				item = (T) getActivityType().getNewActivityTypeItem();
				if(null != item){
					item.setId(cfg.getId());
					item.setCfgId(String.valueOf(cfg.getCfgId()));
					item.setUserId(userId);
					item.setVersion(cfg.getVersion());
					item.setSubItemList(newSubItemList(String.valueOf(cfg.getCfgId())));
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
	
	public List<? extends ActivityTypeSubItemIF> newSubItemList(String cfgId){
		List<ActivityTypeSubItemIF> subItemList = new ArrayList<ActivityTypeSubItemIF>();
		List<String> todaySubs = getTodaySubActivity(cfgId);
		for(String subId : todaySubs){
			ActivityTypeSubItemIF subItem = getActivityType().getNewActivityTypeSubItem();
			if(null != subItem){
				subItem.setCfgId(subId);
				subItemList.add(subItem);
			}
		} 
		return subItemList;
	}
	
	/**
	 * 删除过期的活动
	 * @param userId
	 * @return 在有效期内的活动
	 */
	private ArrayList<T> removeExpireActivity(String userId){
		ActivityDetector detector = ActivityDetector.getInstance();
		List<Integer> removeList = new ArrayList<Integer>();
		Map<Integer, T> activeItemMap = new HashMap<Integer, T>();
		RoleExtPropertyStore<T> itemStore = getItemStore(userId);
		Enumeration<T> mapEnum = itemStore.getExtPropertyEnumeration();
		while (mapEnum.hasMoreElements()) {
			T item = mapEnum.nextElement();
			boolean isActive = detector.containsActivityByCfgId(getActivityType(), item.getCfgId(), item.getVersion());
			if(isActive){
				activeItemMap.put(item.getId(), item);
			}else{
				expireActivityHandler(userId, item);
				item.setClosed(true);
				removeList.add(item.getId());
			}
		}
		if(!removeList.isEmpty()) itemStore.removeItem(removeList);
		return new ArrayList<T>(activeItemMap.values());
	}
	
	/**
	 * 获取活动当前第几天
	 * 
	 * @return
	 */
	private int getCurrentDay(ActivityCfgIF cfg) {
		if(!cfg.isDailyRefresh() || cfg.isEveryDaySame()) return 1;
		return (int) ((System.currentTimeMillis() - cfg.getStartTime()) / ONE_DAY_MS) + 1;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getTodaySubActivity(String cfgID){
		List<String> todaySubs = new ArrayList<String>();
		CfgCsvDao<? extends ActivityCfgIF> actDao = getActivityType().getActivityDao();
		CfgCsvDao<? extends ActivitySubCfgIF> subDao = getActivityType().getSubActivityDao();
		ActivityCfgIF cfg = actDao.getCfgById(cfgID);
		if(null == cfg || null == subDao) return todaySubs;
		if(!ActivityDetector.getInstance().isActive(cfg)) return todaySubs;
		//还在活跃期内，取当天的数据
		int todayNum = getCurrentDay(cfg);
		for(ActivitySubCfgIF subCfg : subDao.getAllCfg()){
			if(StringUtils.equals(subCfg.getDay(), String.valueOf(todayNum)) && 
					StringUtils.equals(String.valueOf(subCfg.getType()), cfgID)){
				todaySubs.add(String.valueOf(subCfg.getId()));
			}
		}
		return todaySubs;
	}
	
	/**
	 * 重载该函数做活动过期处理
	 * @param player
	 * @param item
	 */
	@SuppressWarnings("unchecked")
	private void expireActivityHandler(String userId, T item){
		Player player = PlayerMgr.getInstance().find(userId);
		if(null != player){
			getActivityType().getActivityMgr().expireActivityHandler(player, item);
		}
	}
	
	public RoleExtPropertyStore<T> getItemStore(String userId) {
		RoleExtPropertyStoreCache<T> cache = RoleExtPropertyFactory.getPlayerExtCache(getExtPropertyType(), getGenericClazz());
		try {
			return cache.getStore(userId);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private Class<T> getGenericClazz(){
		if(null != clazz) return clazz;
		ParameterizedType type = (ParameterizedType)getClass().getGenericSuperclass();
		clazz = (Class<T>)type.getActualTypeArguments()[0];
		return clazz;
	}
	
	public abstract ActivityType getActivityType();
	
	protected abstract PlayerExtPropertyType getExtPropertyType();
	
	protected abstract eSynType getSynType();
}
