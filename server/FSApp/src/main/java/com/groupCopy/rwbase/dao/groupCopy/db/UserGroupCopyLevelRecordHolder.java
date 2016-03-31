package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class UserGroupCopyLevelRecordHolder{
	
	
	final private String userId;
	final private MapItemStore<UserGroupCopyLevelRecord> itemStore;
	final private eSynType synType = eSynType.GroupCopyMap;
	
	
	final private AtomicInteger dataVersion = new AtomicInteger(1);
	
	final private AtomicBoolean updateFlag = new AtomicBoolean(false);
	
	public UserGroupCopyLevelRecordHolder(String groupIdP) {
		userId = groupIdP;
		itemStore = new MapItemStore<UserGroupCopyLevelRecord>("userId", userId, UserGroupCopyLevelRecord.class);
	}
	

	public List<UserGroupCopyLevelRecord> getItemList()	
	{
		
		List<UserGroupCopyLevelRecord> itemList = new ArrayList<UserGroupCopyLevelRecord>();
		Enumeration<UserGroupCopyLevelRecord> mapEnum = itemStore.getEnum();
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
		itemStore.updateItem(item);
		update();
//		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public UserGroupCopyLevelRecord getItem(String itemId){
		return itemStore.getItem(itemId);
	}
	
	public boolean removeItem(UserGroupCopyLevelRecord item){
		
		boolean success = itemStore.removeItem(item.getId());
		if(success){
			update();
//			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.REMOVE_SINGLE);
		}
		return success;
	}
	
	public boolean addItem( UserGroupCopyLevelRecord item ){
	
		boolean addSuccess = itemStore.addItem(item);
		if(addSuccess){
			update();
//			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public void synAllData(Player player, int version){
		List<UserGroupCopyLevelRecord> itemList = getItemList();			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	public void update(){
		dataVersion.incrementAndGet();
		updateFlag.set(true);
	}
	
	public void flush(){
		itemStore.flush();
	}
	
}
