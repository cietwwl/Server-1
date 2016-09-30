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
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.manager.GameManager;
import com.rw.service.FresherActivity.FresherActivityChecker;
import com.rw.service.FresherActivity.FresherActivityCheckerResult;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.fresherActivity.FresherActivityCfgDao;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityBigItem;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;

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
	
	private PlayerExtPropertyStore<FresherActivityItem> getMapItemStroe(){
		RoleExtPropertyStoreCache<FresherActivityItem> freshCache = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.FresherActivity, FresherActivityItem.class);
		PlayerExtPropertyStore<FresherActivityItem> attachmentStore;
		try {
			attachmentStore = freshCache.getStore(ownerId);
			return attachmentStore;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Enumeration<FresherActivityItem> getFresherActivityItem(){
		PlayerExtPropertyStore<FresherActivityItem> mapItemStroe = getMapItemStroe();
		return mapItemStroe.getExtPropertyEnumeration();
	}
	
	private Map<Integer, FresherActivityItem> getFresherActivityItemMap(){
		Enumeration<FresherActivityItem> fresherActivityItems = getFresherActivityItem();
		Map<Integer, FresherActivityItem> map = new HashMap<Integer, FresherActivityItem>();
		while (fresherActivityItems.hasMoreElements()) {
			FresherActivityItem fresherActivityItem = (FresherActivityItem) fresherActivityItems.nextElement();
			map.put(fresherActivityItem.getCfgId(), fresherActivityItem);
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
		List<FresherActivityItem> list = new ArrayList<FresherActivityItem>();
		
		Enumeration<FresherActivityItem> fresherActivityItems = getFresherActivityItem();
		while (fresherActivityItems.hasMoreElements()) {
			FresherActivityItem fresherActivityItem = (FresherActivityItem) fresherActivityItems.nextElement();
			if(fresherActivityItem.getType() == type){
				list.add(fresherActivityItem);
			}
		}
		return list;
	}
	
	public void synAllData(Player player, int version) {
		List<FresherActivityItem> list = new ArrayList<FresherActivityItem>();
		Enumeration<FresherActivityItem> fresherActivityItems = getFresherActivityItem();
		while (fresherActivityItems.hasMoreElements()) {
			FresherActivityItem fresherActivityItem = (FresherActivityItem) fresherActivityItems.nextElement();
			//有活动可以领奖但是没有领奖 则表示活动还没有结束
			if(!fresherActivityItem.isFinish() || (!fresherActivityItem.isGiftTaken() && fresherActivityItem.isFinish()) || fresherActivityItem.getEndTime() > System.currentTimeMillis()){
				blnFinish = false;
			}
			list.add(fresherActivityItem);
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
		
		Enumeration<FresherActivityItem> fresherActivityItems = getFresherActivityItem();
		while (fresherActivityItems.hasMoreElements()) {
			FresherActivityItem fresherActivityItem = (FresherActivityItem) fresherActivityItems.nextElement();
			if(fresherActivityItem.getCfgId() == cfgId){
				return fresherActivityItem;
			}
		}
		return null;

	}
	
	private void updateFresherActivityItem(FresherActivityItem fresherActivityItem){
		PlayerExtPropertyStore<FresherActivityItem> mapItemStroe = getMapItemStroe();
		mapItemStroe.update(fresherActivityItem.getId());
	}
	
	public void synListData(Player player, List<FresherActivityItem> list){
		ClientDataSynMgr.synDataList(player, list, synType, eSynOpType.UPDATE_LIST);
	}
	
	public void synData(Player player, FresherActivityItem fresherActivityItem){
		ClientDataSynMgr.synData(player, fresherActivityItem, synType, eSynOpType.UPDATE_SINGLE);
	}
	
}
