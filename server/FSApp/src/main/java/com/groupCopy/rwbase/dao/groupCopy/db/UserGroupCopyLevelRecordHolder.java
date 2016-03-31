package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;

public class UserGroupCopyLevelRecordHolder{
	
	
	final private String userId;
	
	final private AtomicInteger dataVersion = new AtomicInteger(1);
	
	public UserGroupCopyLevelRecordHolder(String groupIdP) {
		userId = groupIdP;
	}
	

	public List<UserGroupCopyLevelRecord> getItemList()	
	{
		
		List<UserGroupCopyLevelRecord> itemList = new ArrayList<UserGroupCopyLevelRecord>();
		Enumeration<UserGroupCopyLevelRecord> mapEnum = getItemStore().getEnum();
		while (mapEnum.hasMoreElements()) {
			UserGroupCopyLevelRecord item = (UserGroupCopyLevelRecord) mapEnum.nextElement();
			itemList.add(item);
		}
		
		return itemList;
	}
	

	public UserGroupCopyLevelRecord getByLevel(String level){
		UserGroupCopyLevelRecord target = null;
		for (UserGroupCopyLevelRecord item : getItemList()) {
			if(StringUtils.equals(item.getLevel() , level)){
				target = item;
			}
		}
		return target;
	}
	
	public void updateItem( UserGroupCopyLevelRecord item ){
		getItemStore().updateItem(item);
		update();
//		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public UserGroupCopyLevelRecord getItem(String itemId){
		return getItemStore().getItem(itemId);
	}
	
	
	public boolean addItem( UserGroupCopyLevelRecord item ){
	
		boolean addSuccess = getItemStore().addItem(item);
		if(addSuccess){
			update();
//			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}

	private void update(){
		dataVersion.incrementAndGet();
	}
	
	public int getVersion(){
		return dataVersion.get();
	}
	private MapItemStore<UserGroupCopyLevelRecord> getItemStore(){
		MapItemStoreCache<UserGroupCopyLevelRecord> itemStoreCache = MapItemStoreFactory.getUserGroupCopyLevelRecordCache();
		return itemStoreCache.getMapItemStore(userId, UserGroupCopyLevelRecord.class);
	}
	
}
