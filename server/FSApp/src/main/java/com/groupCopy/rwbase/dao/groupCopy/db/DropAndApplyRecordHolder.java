package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfgDao;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class DropAndApplyRecordHolder {
	
	private final String groupId;
	private final eSynType synType = eSynType.GROUP_ITEM_DROP_APPLY;
	private final AtomicInteger dataVersion = new AtomicInteger(0);

	public DropAndApplyRecordHolder(String groupId) {
		this.groupId = groupId;
		checkAndInitData();
	}

	
	public void checkAndInitData(){
		List<GroupCopyMapCfg> list = GroupCopyMapCfgDao.getInstance().getAllCfg();
		List<CopyItemDropAndApplyRecord> addList = null;
		for (GroupCopyMapCfg cfg : list) {
			CopyItemDropAndApplyRecord record = getItemByID(cfg.getId());
			if(record == null){
				record = new CopyItemDropAndApplyRecord(cfg.getId(), groupId);
				if(addList == null){
					addList = new ArrayList<CopyItemDropAndApplyRecord>();
				}
				addList.add(record);
			}
		}
		
		if(addList != null){
			try {
				getItemStore().addItem(addList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private String getRecordID(String chaterID){
		return groupId+"_"+chaterID ;
	}
	
	public List<CopyItemDropAndApplyRecord> getItemList()	
	{
		
		List<CopyItemDropAndApplyRecord> itemList = new ArrayList<CopyItemDropAndApplyRecord>();
		CopyItemDropAndApplyRecord item;
		List<GroupCopyMapCfg> allCfg = GroupCopyMapCfgDao.getInstance().getAllCfg();
		for (GroupCopyMapCfg cfg : allCfg) {
			item =getItemStore().getItem(getRecordID(cfg.getId()));
			if(item != null){
				itemList.add(item);
			}
		}
		return itemList;
	}
	
	public void updateItem(Player player, CopyItemDropAndApplyRecord item){
		getItemStore().updateItem(item);
		updateVersion();
		if(player != null){
			synSingleData(player, item.getChaterID());
		}
	}
	
	public CopyItemDropAndApplyRecord getItemByID(String chaterID){
		return getItemStore().getItem(getRecordID(chaterID));
	}
	
	public ItemDropAndApplyTemplate getItemApplyDataByID(String chaterID, int itemID){
		CopyItemDropAndApplyRecord item = getItemStore().getItem((getRecordID(chaterID)));
		return item.getDaMap().get(String.valueOf(itemID));
	}
	
	
	/**
	 * 同步单章节数据到客户端
	 * @param player
	 * @param chaterID
	 */
	public void synSingleData(Player player, String chaterID) {
		CopyItemDropAndApplyRecord item = getItemByID(chaterID);			
		ClientDataSynMgr.synData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}

	
	
	private MapItemStore<CopyItemDropAndApplyRecord> getItemStore(){
		MapItemStoreCache<CopyItemDropAndApplyRecord> cache = MapItemStoreFactory.getItemDropAndApplyRecordCache();
		return cache.getMapItemStore(groupId, CopyItemDropAndApplyRecord.class);
	}
	
	private void updateVersion(){
		dataVersion.incrementAndGet();
	}


	
}
