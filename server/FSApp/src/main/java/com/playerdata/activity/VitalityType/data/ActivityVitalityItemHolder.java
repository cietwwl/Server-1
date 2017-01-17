package com.playerdata.activity.VitalityType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeEnum;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfgDAO;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityVitalityItemHolder{
	
	private static ActivityVitalityItemHolder instance = new ActivityVitalityItemHolder();
	
	public static ActivityVitalityItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityVitalityType;
	
	/*
	 * 活跃之王的活动项
	 */
	public List<ActivityVitalityTypeItem> getItemList(String userId){	
		List<ActivityVitalityTypeItem> itemList = new ArrayList<ActivityVitalityTypeItem>();
		Enumeration<ActivityVitalityTypeItem> mapEnum = getItemStore(userId).getExtPropertyEnumeration();
		while (mapEnum.hasMoreElements()) {
			ActivityVitalityTypeItem item = (ActivityVitalityTypeItem) mapEnum.nextElement();	
			if(ActivityVitalityCfgDAO.getInstance().getCfgListByEnumId(item.getEnumId()).isEmpty()){
				continue;
			}
			itemList.add(item);
		}
		return itemList;
	}
	
	public void removeItem(Player player, ActivityVitalityTypeItem item){
		getItemStore(player.getUserId()).removeItem(item.getId());
	}
	
	public void updateItem(Player player, ActivityVitalityTypeItem item){
		getItemStore(player.getUserId()).update(item.getId());
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public ActivityVitalityTypeItem getItem(String userId, ActivityVitalityTypeEnum acVitalityTypeEnum){
//		String itemId = ActivityVitalityTypeHelper.getItemId(userId, acVitalityTypeEnum);
		int id = Integer.parseInt(acVitalityTypeEnum.getCfgId());
		return getItemStore(userId).get(id);
	}	

	public boolean addItem(Player player, ActivityVitalityTypeItem item){
	
		boolean addSuccess = getItemStore(player.getUserId()).addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public boolean addItemList(Player player, List<ActivityVitalityTypeItem> itemList){
		try {
			boolean addSuccess = getItemStore(player.getUserId()).addItem(itemList);
			if(addSuccess){
				ClientDataSynMgr.updateDataList(player, getItemList(player.getUserId()), synType, eSynOpType.UPDATE_LIST);
			}
			return addSuccess;
		} catch (DuplicatedKeyException e) {
			//handle..
			e.printStackTrace();
			return false;
		}
	}	

	public void synAllData(Player player){
		List<ActivityVitalityTypeItem> itemList = getItemList(player.getUserId());	
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	public RoleExtPropertyStore<ActivityVitalityTypeItem> getItemStore(String userId) {
		RoleExtPropertyStoreCache<ActivityVitalityTypeItem> cach = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_VITALITY, ActivityVitalityTypeItem.class);
		try {
			return cach.getStore(userId);
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
