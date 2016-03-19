package com.rwbase.dao.fresherActivity.pojo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.manager.GameManager;
import com.rw.service.FresherActivity.FresherActivityCheckerResult;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.fresherActivity.FresherActivityCfgDao;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityBigItem;

/**
 * 开服活动数据
 * 
 * @author lida
 *
 */
public class FresherActivityItemHolder {
	private final static long DAY_TIME = 24 * 60 * 60 * 1000l;
	final private String ownerId;
	final private eSynType synType = eSynType.FRESHER_ATIVITY_DATA;
	private boolean blnFinish = true;

	/**
	 * 初始化玩家的开服活动数据
	 * 
	 * @param userId
	 */
	public FresherActivityItemHolder(String userId) {
		ownerId = userId;
		initFresherActivityMap(userId);

	}
	
	private MapItemStore<FresherActivityBigItem> getMapItemStroe(String ownerId){
		MapItemStoreCache<FresherActivityBigItem> cache = MapItemStoreFactory.getFresherActivityCache();
		return cache.getMapItemStore(ownerId, FresherActivityBigItem.class);
	}
	
	private Map<Integer, FresherActivityBigItem> getFresherActivityBigItemMap(){
		MapItemStore<FresherActivityBigItem> fresherActivityStore = getMapItemStroe(ownerId);
		Enumeration<FresherActivityBigItem> mapEnumeration = fresherActivityStore.getEnum();
		Map<Integer, FresherActivityBigItem> map = new HashMap<Integer, FresherActivityBigItem>();
		while(mapEnumeration.hasMoreElements()){
			FresherActivityBigItem item = mapEnumeration.nextElement();
			map.put(item.getActivityType().ordinal(), item);

		}
		return map;
	}

	public List<FresherActivityItem> getFresherActivityItemList(){
		MapItemStore<FresherActivityBigItem> fresherActivityStore = getMapItemStroe(ownerId);
		Enumeration<FresherActivityBigItem> mapEnumeration = fresherActivityStore.getEnum();
		List<FresherActivityItem> list = new ArrayList<FresherActivityItem>();
		while(mapEnumeration.hasMoreElements()){
			FresherActivityBigItem item = mapEnumeration.nextElement();
			List<FresherActivityItem> itemList = item.getItemList();
			list.addAll(itemList);
		}
		return list;
	}
	
	private Map<Integer, FresherActivityItem> getFresherActivityItemMap(){
		MapItemStore<FresherActivityBigItem> fresherActivityStore = getMapItemStroe(ownerId);
		Enumeration<FresherActivityBigItem> mapEnumeration = fresherActivityStore.getEnum();
		 Map<Integer, FresherActivityItem> map = new HashMap<Integer, FresherActivityItem>();
		while(mapEnumeration.hasMoreElements()){
			FresherActivityBigItem item = mapEnumeration.nextElement();
			List<FresherActivityItem> itemList = item.getItemList();
			for (FresherActivityItem fresherActivityItem : itemList) {
				map.put(fresherActivityItem.getCfgId(), fresherActivityItem);
			}
		}
		return map;
	}
	
	/**
	 * 根据配置获取所有的新手活动配置
	 */
	private void initFresherActivityMap(String ownerId){
		List<FresherActivityCfg> allCfg = FresherActivityCfgDao.getInstance().getAllCfg();
		MapItemStore<FresherActivityBigItem> mapItemStroe = getMapItemStroe(ownerId);
		Map<Integer, FresherActivityBigItem> mapItemStoreList = getFresherActivityBigItemMap();
		boolean blnUpdate =false;
		for (FresherActivityCfg fresherActivityCfg : allCfg) {
			int cfgId = fresherActivityCfg.getCfgId();
			int activityType = fresherActivityCfg.getActivityType();
			
			if(mapItemStoreList.containsKey(activityType)){
				FresherActivityBigItem fresherActivityBigItem = mapItemStoreList.get(activityType);
				List<FresherActivityItem> itemList = fresherActivityBigItem.getItemList();
				boolean blnExist = false;
				for (FresherActivityItem fresherActivityItem : itemList) {
					//有活动可以领奖但是没有领奖 则表示活动还没有结束
					if(!fresherActivityItem.isFinish() || (!fresherActivityItem.isGiftTaken() && fresherActivityItem.isFinish()) || fresherActivityItem.getEndTime() > System.currentTimeMillis()){
						blnFinish = false;
					}
					if(fresherActivityItem.getCfgId() == cfgId){
						blnExist = true;
						refreshActivityTime(fresherActivityItem, fresherActivityCfg);
						break;
					}
				}
				if(!blnExist){
					if(createNewFresherActivity(fresherActivityCfg, fresherActivityBigItem, ownerId)){
						mapItemStroe.updateItem(fresherActivityBigItem);
						blnUpdate = true;
					}
				}
				continue;
			} else {
				FresherActivityBigItem fresherActivityBigItem = new FresherActivityBigItem();
				fresherActivityBigItem.setId(ownerId + activityType);
				fresherActivityBigItem.setOwnerId(ownerId);
				eActivityType type = eActivityType.getTypeByOrder(fresherActivityCfg.getActivityType());
				fresherActivityBigItem.setActivityType(type);
				if(createNewFresherActivity(fresherActivityCfg, fresherActivityBigItem, ownerId)){
					mapItemStroe.addItem(fresherActivityBigItem);
				}
				mapItemStoreList.put(type.ordinal(), fresherActivityBigItem);
			}
		}
		

	}
	
	public boolean createNewFresherActivity(FresherActivityCfg fresherActivityCfg, FresherActivityBigItem fresherActivityBigItem, String ownerId){
		long current = System.currentTimeMillis();
		FresherActivityItem fresherActivityItem = new FresherActivityItem();

		
		refreshActivityTime(fresherActivityItem, fresherActivityCfg);
		long endTime = fresherActivityItem.getEndTime();
		if (endTime != -1 && endTime <= current) {
			return false;
		}
		int cfgId = fresherActivityCfg.getCfgId();
		fresherActivityItem.setId(ownerId+cfgId);
		fresherActivityItem.setOwnerId(ownerId);
		fresherActivityItem.setCfgId(cfgId);
		fresherActivityItem.setType(fresherActivityCfg.geteType());
		String maxValue = fresherActivityCfg.getMaxValue();
		if(maxValue != null && !maxValue.equals("")){
			fresherActivityItem.setCurrentValue("0/" + fresherActivityCfg.getMaxValue());
		}
		List<FresherActivityItem> itemList = fresherActivityBigItem.getItemList();
		itemList.add(fresherActivityItem);
		blnFinish = false;
		return true;
	}

	/**
	 * 读取配置表的时间
	 * 
	 * @param fresherActivityItem
	 * @param fresherActivityCfg
	 */
	private void refreshActivityTime(FresherActivityItem fresherActivityItem, FresherActivityCfg fresherActivityCfg) {
		long openTime = GameManager.getOpenTime();
		fresherActivityItem.setStartTime(fresherActivityCfg.getStartTime() * DAY_TIME + openTime);
		if (fresherActivityCfg.getEndTime() == -1) {
			fresherActivityItem.setEndTime(-1);
		} else {
			fresherActivityItem.setEndTime(fresherActivityCfg.getStartTime() * DAY_TIME + openTime + fresherActivityCfg.getEndTime() * DAY_TIME);
		}
	}

	/**
	 * 返回指定类型的活动
	 * 
	 * @param type
	 * @return
	 */
	public List<FresherActivityItem> getFresherActivityItemsByType(eActivityType type) {
		MapItemStore<FresherActivityBigItem> itemStroe = getMapItemStroe(ownerId);
		Map<Integer, FresherActivityBigItem> mapItemStoreList = getFresherActivityBigItemMap();
		FresherActivityBigItem fresherActivityBigItem = mapItemStoreList.get(type.ordinal());
		
		List<FresherActivityItem> list = new ArrayList<FresherActivityItem>();
		if(fresherActivityBigItem != null){
			list = fresherActivityBigItem.getItemList();
		}
		return list;
	}
	
	public void synAllData(Player player, int version) {
		if (blnFinish) {
			return;
		}
		List<FresherActivityItem> list = new ArrayList<FresherActivityItem>();
		Map<Integer, FresherActivityBigItem> mapItemStoreList = getFresherActivityBigItemMap();
		for (Iterator<Entry<Integer, FresherActivityBigItem>> iterator = mapItemStoreList.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, FresherActivityBigItem> next = iterator.next();
			FresherActivityBigItem item = next.getValue();
			List<FresherActivityItem> itemList = item.getItemList();
			list.addAll(itemList);
		}
		synListData(player, list);
	}

	/**
	 * 更新活动状态
	 * 
	 * @param player
	 * @param result
	 */
	public void completeFresherActivity(Player player, FresherActivityCheckerResult result) {
		List<FresherActivityItem> refreshList = new ArrayList<FresherActivityItem>();
		List<Integer> synCfgId = new ArrayList<Integer>();

		Map<Integer, FresherActivityItem> FresherActivityMap = getFresherActivityItemMap();
		//同步更新完成状态的活动
		for (Integer activityId :  result.getCompleteList()) {

			FresherActivityItem fresherActivityItem = FresherActivityMap.get(activityId);
			fresherActivityItem.setFinish(true);
			refreshList.add(fresherActivityItem);
			updateFresherActivityItem(fresherActivityItem);
			synCfgId.add(fresherActivityItem.getCfgId());
		}

		// 同步更新进度的活动
		Map<Integer, String> currentProgress = result.getCurrentProgress();
		for (Iterator<Entry<Integer, String>> iterator = currentProgress.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, String> entry = iterator.next();
			int cfgId = entry.getKey();
			FresherActivityItem fresherActivityItem = FresherActivityMap.get(cfgId);
			FresherActivityCfg fresherActivityCfg = FresherActivityCfgDao.getInstance().getFresherActivityCfg(cfgId);
			if (fresherActivityCfg.getMaxValue() != null && !fresherActivityCfg.getMaxValue().equals("")) {
				fresherActivityItem.setCurrentValue(entry.getValue() + "/" + fresherActivityCfg.getMaxValue());
			}
			if (!synCfgId.contains(cfgId)) {
				synCfgId.add(cfgId);
				refreshList.add(fresherActivityItem);
				updateFresherActivityItem(fresherActivityItem);
			}
		}
		synListData(player, refreshList);
	}

	/**
	 * 设置领取活动奖励成功的标志位
	 * 
	 * @param player
	 * @param cfgId
	 */
	public void achieveFresherActivityReward(Player player, int cfgId) {
		FresherActivityItem fresherActivityItem = getFresherActivityItemsById(cfgId);
		fresherActivityItem.setGiftTaken(true);
		fresherActivityItem.setClosed(true);
		updateFresherActivityItem(fresherActivityItem);

		synData(player, fresherActivityItem);
	}
	
	public FresherActivityItem getFresherActivityItemsById(int cfgId){
		Map<String, FresherActivityCfg> maps = FresherActivityCfgDao.getInstance().getMaps();
		FresherActivityCfg fresherActivityCfg = maps.get(String.valueOf(cfgId));
		int activityType = fresherActivityCfg.getActivityType();
		
		Map<Integer, FresherActivityBigItem> mapItemStoreList = getFresherActivityBigItemMap();
		FresherActivityBigItem fresherActivityBigItem = mapItemStoreList.get(activityType);
		
		List<FresherActivityItem> itemList = fresherActivityBigItem.getItemList();
		for (FresherActivityItem fresherActivityItem : itemList) {
			if(fresherActivityItem.getCfgId() == cfgId){
				return fresherActivityItem;
			}
		}
		return null;
	}
	
	private void updateFresherActivityItem(FresherActivityItem fresherActivityItem){
		Map<Integer, FresherActivityBigItem> map = getFresherActivityBigItemMap();
		FresherActivityBigItem fresherActivityBigItem = map.get(fresherActivityItem.getType().ordinal());
		MapItemStore<FresherActivityBigItem> mapItemStroe = getMapItemStroe(ownerId);
		mapItemStroe.updateItem(fresherActivityBigItem);
	}
	
	public void synListData(Player player, List<FresherActivityItem> list){
		ClientDataSynMgr.synDataList(player, list, synType, eSynOpType.UPDATE_LIST);
	}
	
	public void synData(Player player, FresherActivityItem fresherActivityItem){
		ClientDataSynMgr.synData(player, fresherActivityItem, synType, eSynOpType.UPDATE_SINGLE);
	}
	
}
