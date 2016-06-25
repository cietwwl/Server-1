package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class UserGroupCopyMapRecordHolder{
	
	private final int COPY_FIGHT_COUT = 2;//帮派副本每天进入次数
	final private String userId;
	
	final private AtomicInteger dataVersion = new AtomicInteger(1);
	
	private final eSynType synType = eSynType.USE_GROUP_COPY_DATA;
	
	public UserGroupCopyMapRecordHolder(String userID) {
		userId = userID;
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
			if(StringUtils.equals(item.getId() , level)){
				target = item;
			}
		}
		return target;
	}
	
	public boolean updateItem(Player player, UserGroupCopyMapRecord item ){
		boolean success = getItemStore().updateItem(item);
		if(success){
			update();
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
		}
		return success;
	}
	
	public UserGroupCopyMapRecord getItem(String itemId){
		return getItemStore().getItem(itemId);
	}
	
	
	public boolean addItem(Player player, UserGroupCopyMapRecord item ){
	
		boolean addSuccess = getItemStore().addItem(item);
		if(addSuccess){
			update();
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
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
			record.setLeftFightCount(COPY_FIGHT_COUT);
		}
		update();
	}
	
	
	
	public void syncData(Player player){
		List<UserGroupCopyMapRecord> list = getItemList();
		if(!list.isEmpty()){
			ClientDataSynMgr.synDataList(player, getItemList(), synType, eSynOpType.UPDATE_LIST);
		}
	}
	
}
