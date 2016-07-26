package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.rocketmq.common.DataVersion;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.service.player.PlayerService;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.common.PlayerDataMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GroupCopyRewardDistRecordHolder{
	
	final private String groupId;
	final private eSynType synType = eSynType.GROUP_COPY_REWARD;
	
	
	public GroupCopyRewardDistRecordHolder(String groupIdP) {
		groupId = groupIdP;
		initData();
	}
	
	private GroupCopyRewardDistRecord initData(){
		GroupCopyRewardDistRecord item = getItem();
		if(item == null){
			item = new GroupCopyRewardDistRecord();
			item.setGroupId(groupId);
			item.setId(groupId);
			getItemStore().addItem(item);
		}
		return item;
	}
	
	public void updateItem(Player player, GroupCopyRewardDistRecord item){
		getItemStore().updateItem(item);
		if(player != null){
			synAllData(player);
		}
	}
	
	public GroupCopyRewardDistRecord getItem(){
		return getItemStore().getItem(groupId);
	}
	
	
	
	public void synAllData(Player player){
		GroupCopyRewardDistRecord item = getItem();
		List<DistRewRecordItem> list = item.getRecordList();
		ClientDataSynMgr.synDataList(player, list, synType, eSynOpType.UPDATE_LIST);
	}

	
	
	private MapItemStore<GroupCopyRewardDistRecord> getItemStore(){
		MapItemStoreCache<GroupCopyRewardDistRecord> cache = MapItemStoreFactory.getGroupCopyRewardRecordCache();
		return cache.getMapItemStore(groupId, GroupCopyRewardDistRecord.class);
	}

	public void addDistRecord(DistRewRecordItem item) {
		GroupCopyRewardDistRecord record = getItem();
		if(record == null){
			record = initData();
		}
		record.addRecord(item);
		getItemStore().updateItem(record);
	}
}
