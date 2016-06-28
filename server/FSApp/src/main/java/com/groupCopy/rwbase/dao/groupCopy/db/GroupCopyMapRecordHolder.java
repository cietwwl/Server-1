package com.groupCopy.rwbase.dao.groupCopy.db;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.common.GameUtil;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfgDao;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.GroupCopyCmdProto.GroupCopyMapStatus;

public class GroupCopyMapRecordHolder{
	
	
	final private String groupId;
	final private eSynType synType = eSynType.GROUP_COPY_MAP;	
	
	final private AtomicInteger dataVersion = new AtomicInteger(1);
	
	
	public GroupCopyMapRecordHolder(String groupIdP) {
		groupId = groupIdP;
		checkAndInitData();
	}
	

	public void checkAndInitData() {
		List<GroupCopyMapCfg> list = GroupCopyMapCfgDao.getInstance().getAllCfg();
		GroupCopyMapRecord record;
		for (GroupCopyMapCfg cfg : list) {
			 record = getItem(cfg.getId());
			 if(record == null){
				 record = createRecord(cfg.getId());
				 getItemStore().addItem(record);
			 }
		}
	}
	
	private GroupCopyMapRecord createRecord(String mapID){
		GroupCopyMapRecord record = null;
		try {
			record = new GroupCopyMapRecord();
			record.setId(mapID);
			record.setGroupId(groupId);
			record.setStatus(GroupCopyMapStatus.LOCKING);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return record;
	}


	public List<GroupCopyMapRecord> getItemList()	
	{
		
		List<GroupCopyMapRecord> itemList = new ArrayList<GroupCopyMapRecord>();
		Enumeration<GroupCopyMapRecord> mapEnum = getItemStore().getEnum();
		while (mapEnum.hasMoreElements()) {
			GroupCopyMapRecord item = (GroupCopyMapRecord) mapEnum.nextElement();
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public boolean updateItem(Player player, GroupCopyMapRecord item ){
		boolean suc = getItemStore().updateItem(item);
		update();
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
		return suc;
	}
	
	public GroupCopyMapRecord getItem(String itemId){
		return getItemStore().getItem(itemId);
	}
	
	
	
	
	public void synAllData(Player player, int version){
		List<GroupCopyMapRecord> itemList = getItemList();			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	private void update(){
		dataVersion.incrementAndGet();
	}

	
	private MapItemStore<GroupCopyMapRecord> getItemStore(){
		MapItemStoreCache<GroupCopyMapRecord> itemStoreCache = MapItemStoreFactory.getGroupCopyMapRecordCache();
		return itemStoreCache.getMapItemStore(groupId, GroupCopyMapRecord.class);
	}


	/**
	 * 更新一下副本地图进度
	 * @param levelId
	 * @param p 进度值
	 */
	public void updateMapProgress(String levelId, double p) {
		getItem(levelId).setProgress(p);
		update();
	}


	public void checkDamageRank(String chaterID,
			GroupCopyArmyDamageInfo damageInfo) {
		boolean suc = getItem(chaterID).checkOrAddDamageRank(damageInfo);
		if(suc){
			//TODO 这个数据暂时没有向前端同步，后面再考虑是否开放
			update();
		}
	}




	
	

	
	
	


	
	
}
