package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;

public class UserGroupCopyMapRecordHolder{
	
	
	final private String userId;
	
	final private AtomicInteger dataVersion = new AtomicInteger(1);
	
	public UserGroupCopyMapRecordHolder(String groupIdP) {
		userId = groupIdP;
	}
	

	public List<UserGroupCopyMapRecord> getItemList()	
	{
		
		List<UserGroupCopyMapRecord> itemList = new ArrayList<UserGroupCopyMapRecord>();
		Enumeration<UserGroupCopyMapRecord> mapEnum = getItemStore().getEnum();
		while (mapEnum.hasMoreElements()) {
			UserGroupCopyMapRecord item = (UserGroupCopyMapRecord) mapEnum.nextElement();
			itemList.add(item);
		}
		
		return itemList;
	}
	

	public UserGroupCopyMapRecord getByLevel(String level){
		UserGroupCopyMapRecord target = null;
		for (UserGroupCopyMapRecord item : getItemList()) {
			if(StringUtils.equals(item.getChaterID() , level)){
				target = item;
			}
		}
		return target;
	}
	
	public boolean updateItem( UserGroupCopyMapRecord item ){
		boolean success = getItemStore().updateItem(item);
		if(success){
			update();
		}
		return success;
	}
	
	public UserGroupCopyMapRecord getItem(String itemId){
		return getItemStore().getItem(itemId);
	}
	
	
	public boolean addItem( UserGroupCopyMapRecord item ){
	
		boolean addSuccess = getItemStore().addItem(item);
		if(addSuccess){
			update();
		}
		return addSuccess;
	}

	private void update(){
		dataVersion.incrementAndGet();
	}
	
	public int getVersion(){
		return dataVersion.get();
	}
	private MapItemStore<UserGroupCopyMapRecord> getItemStore(){
		MapItemStoreCache<UserGroupCopyMapRecord> itemStoreCache = MapItemStoreFactory.getUserGroupCopyLevelRecordCache();
		return itemStoreCache.getMapItemStore(userId, UserGroupCopyMapRecord.class);
	}


	public void resetFightCount() {
		List<UserGroupCopyMapRecord> list = getItemList();
		for (UserGroupCopyMapRecord record : list) {
			record.setFightCount(0);
		}
		update();
	}
	
}
