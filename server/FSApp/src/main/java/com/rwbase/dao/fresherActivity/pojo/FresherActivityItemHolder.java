package com.rwbase.dao.fresherActivity.pojo;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.service.FresherActivity.FresherActivityCheckerResult;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.fresherActivity.FresherActivityCfgDao;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

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
	}
	
	private RoleExtPropertyStore<FresherActivityBigItem> getMapItemStroe(){
		RoleExtPropertyStoreCache<FresherActivityBigItem> playerExtCache = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.FRESHER_ACTIVITY, FresherActivityBigItem.class);
		
		RoleExtPropertyStore<FresherActivityBigItem> store;
		try {
			store = playerExtCache.getStore(ownerId);
			return store;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			GameLog.error("FresherActivityItemHolder InterruptedException", ownerId, e.getMessage());
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			GameLog.error("FresherActivityItemHolder Throwable", ownerId, e.getMessage());
		}
		return null;
	}
	
	private Map<Integer, FresherActivityBigItem> getFresherActivityBigItemMap(){
		RoleExtPropertyStore<FresherActivityBigItem> mapItemStroe = getMapItemStroe();
		Enumeration<FresherActivityBigItem> mapEnumeration = mapItemStroe.getExtPropertyEnumeration();
		Map<Integer, FresherActivityBigItem> map = new HashMap<Integer, FresherActivityBigItem>();
		while(mapEnumeration.hasMoreElements()){
			FresherActivityBigItem item = mapEnumeration.nextElement();
			map.put(item.getActivityType().ordinal(), item);

		}
		return map;
	}

	public List<FresherActivityItem> getFresherActivityItemList(){
		RoleExtPropertyStore<FresherActivityBigItem> mapItemStroe = getMapItemStroe();
		Enumeration<FresherActivityBigItem> mapEnumeration = mapItemStroe.getExtPropertyEnumeration();
		List<FresherActivityItem> list = new ArrayList<FresherActivityItem>();
		while(mapEnumeration.hasMoreElements()){
			FresherActivityBigItem item = mapEnumeration.nextElement();
			List<FresherActivityItem> itemList = item.getItemList();
			list.addAll(itemList);
		}
		return list;
	}
	
	private Map<Integer, FresherActivityItem> getFresherActivityItemMap(){
		RoleExtPropertyStore<FresherActivityBigItem> mapItemStroe = getMapItemStroe();
		Enumeration<FresherActivityBigItem> mapEnumeration = mapItemStroe.getExtPropertyEnumeration();
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
	 * 返回指定类型的活动
	 * 
	 * @param type
	 * @return
	 */
	public List<FresherActivityItem> getFresherActivityItemsByType(eActivityType type) {
		Map<Integer, FresherActivityBigItem> mapItemStoreList = getFresherActivityBigItemMap();
		FresherActivityBigItem fresherActivityBigItem = mapItemStoreList.get(type.ordinal());
		
		List<FresherActivityItem> list = new ArrayList<FresherActivityItem>();
		if(fresherActivityBigItem != null){
			list = fresherActivityBigItem.getItemList();
		}
		return list;
	}
	
	public void synAllData(Player player, int version) {
		List<FresherActivityItem> list = new ArrayList<FresherActivityItem>();
		Map<Integer, FresherActivityBigItem> mapItemStoreList = getFresherActivityBigItemMap();
		for (Iterator<Entry<Integer, FresherActivityBigItem>> iterator = mapItemStoreList.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, FresherActivityBigItem> next = iterator.next();
			FresherActivityBigItem item = next.getValue();
			List<FresherActivityItem> itemList = item.getItemList();
			for (FresherActivityItem fresherActivityItem : itemList) {
				if(!fresherActivityItem.isFinish() || (!fresherActivityItem.isGiftTaken() && fresherActivityItem.isFinish()) || fresherActivityItem.getEndTime() > System.currentTimeMillis()){
					blnFinish = false;
				}
			}
			list.addAll(itemList);
		}
		if(blnFinish){
			return;
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
		
	}
	
	public void achieveFresherActivityReward(Player player, FresherActivityItem fresherActivityItem){
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
		RoleExtPropertyStore<FresherActivityBigItem> mapItemStroe = getMapItemStroe();
		mapItemStroe.update(fresherActivityBigItem.getId());
	}
	
	public void synListData(Player player, List<FresherActivityItem> list){
		ClientDataSynMgr.synDataList(player, list, synType, eSynOpType.UPDATE_LIST);
	}
	
	public void synData(Player player, FresherActivityItem fresherActivityItem){
		ClientDataSynMgr.synData(player, fresherActivityItem, synType, eSynOpType.UPDATE_SINGLE);
	}
	
}
