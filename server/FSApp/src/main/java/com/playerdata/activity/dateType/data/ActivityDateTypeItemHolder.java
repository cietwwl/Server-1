package com.playerdata.activity.dateType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.dateType.ActivityDateTypeEnum;
import com.playerdata.activity.dateType.ActivityDateTypeHelper;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityDateTypeItemHolder{
	
	private static ActivityDateTypeItemHolder instance = new ActivityDateTypeItemHolder();
	
	public static ActivityDateTypeItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityDateType;
	

	public List<ActivityDateTypeItem> getItemList(String userId)	
	{
		
		List<ActivityDateTypeItem> itemList = new ArrayList<ActivityDateTypeItem>();
		Enumeration<ActivityDateTypeItem> mapEnum = getItemStore(userId).getExtPropertyEnumeration();
		while (mapEnum.hasMoreElements()) {
			ActivityDateTypeItem item = (ActivityDateTypeItem) mapEnum.nextElement();
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, ActivityDateTypeItem item){
		getItemStore(player.getUserId()).update(item.getId());
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public ActivityDateTypeItem getItem(String userId, ActivityDateTypeEnum typeEnum){		
		int id = Integer.parseInt(typeEnum.getCfgId());
		return getItemStore(userId).get(id);
	}
	
	
	public boolean addItem(Player player, ActivityDateTypeItem item){
	
		boolean addSuccess = getItemStore(player.getUserId()).addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public boolean removeItem(Player player,ActivityDateTypeEnum type){
		
		Integer id = Integer.parseInt(type.getCfgId());
		boolean addSuccess = getItemStore(player.getUserId()).removeItem(id);
		return addSuccess;
	}
	
	public void synAllData(Player player){
		List<ActivityDateTypeItem> itemList = getItemList(player.getUserId());			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	private RoleExtPropertyStore<ActivityDateTypeItem> getItemStore(String userId) {
		return null;
	}
	
}
