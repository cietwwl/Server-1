package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfgDao;
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
	
	final private AtomicInteger dataVersion = new AtomicInteger(0);
	
	private final eSynType synType = eSynType.USE_GROUP_COPY_DATA;
	
	public UserGroupCopyMapRecordHolder(String userID) {
		userId = userID;
		checkAndInitData();
	}
	
	private void checkAndInitData(){
		List<GroupCopyMapCfg> allCfg = GroupCopyMapCfgDao.getInstance().getAllCfg();
		UserGroupCopyMapRecord record;
		for (GroupCopyMapCfg cfg : allCfg) {
			record = getItemByID(cfg.getId());
			if(record == null){
				record = createRecord(cfg);
				getItemStore().addItem(record);
			}
		}
	}
	
	private UserGroupCopyMapRecord createRecord(GroupCopyMapCfg cfg){
		UserGroupCopyMapRecord record = new UserGroupCopyMapRecord();
		record.setId(getRecordID(cfg.getId()));
		record.setLeftFightCount(cfg.getEnterCount());
		record.setUserId(userId);
		record.setChaterID(cfg.getId());
		return record;
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
	
	
	public boolean updateItem(Player player, UserGroupCopyMapRecord item ){
		boolean success = getItemStore().updateItem(item);
		if(success){
			update();
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
		}
		return success;
	}
	
	public UserGroupCopyMapRecord getItemByID(String itemId){
		return getItemStore().getItem(getRecordID(itemId));
	}
	
	/**
	 * 主键id
	 * @param id
	 * @return
	 */
	private String getRecordID(String id){
		return userId+"_"+id;
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
