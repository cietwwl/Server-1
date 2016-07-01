package com.playerdata.groupFightOnline.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GFDefendArmyItemHolder{
	
	private static GFDefendArmyItemHolder instance = new GFDefendArmyItemHolder();
	
	public static GFDefendArmyItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.GFDefendArmyData;
	
	private Map<String,AtomicInteger> versionMap = new ConcurrentHashMap<String,AtomicInteger>();
	
	
	/*
	 * 获取用户已经拥有的时装
	 */
	public List<GFDefendArmyItem> getItemList(String groupId)	
	{
		
		List<GFDefendArmyItem> itemList = new ArrayList<GFDefendArmyItem>();
		Enumeration<GFDefendArmyItem> mapEnum = getItemStore(groupId).getEnum();
		while (mapEnum.hasMoreElements()) {
			GFDefendArmyItem item = (GFDefendArmyItem) mapEnum.nextElement();			
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(GFDefendArmyItem item){		
		String groupId = item.getGroupID();
		getItemStore(groupId).updateItem(item);
		updateVersion(groupId);
	}
	

	
	public boolean addItem(GFDefendArmyItem item){
	
		String groupId = item.getGroupID();
		boolean addSuccess = getItemStore(groupId).addItem(item);
		updateVersion(groupId);
		
		return addSuccess;
	}
	
	public boolean addItemList(String groupId, List<GFDefendArmyItem> itemList){
		try {
			boolean addSuccess = getItemStore(groupId).addItem(itemList);	
			updateVersion(groupId);
			return addSuccess;
		} catch (DuplicatedKeyException e) {
			//handle..
			e.printStackTrace();
			return false;
		}
	}
	
	public void synByVersion(Player player, String groupId, int version){
		AtomicInteger curVersion = versionMap.get(groupId);
		if(curVersion.get()!=version){			
			synAllData(player, groupId);
		}		
		
	}
	
	private void synAllData(Player player, String groupId){
		List<GFDefendArmyItem> serverDataList = getItemList(player.getUserId());			
		int curVersion = versionMap.get(groupId).get();
		ClientDataSynMgr.synDataGroupList(player, groupId, serverDataList, synType, eSynOpType.UPDATE_LIST, curVersion );
	}

	private void updateVersion(String groupId){
		if(versionMap.get(groupId) == null){
			versionMap.put(groupId, new AtomicInteger());
		}
		versionMap.get(groupId).incrementAndGet();
	}
	
	
	private MapItemStore<GFDefendArmyItem> getItemStore(String groupID) {
		MapItemStoreCache<GFDefendArmyItem> cache = MapItemStoreFactory.getGFDefendArmyCache();
		return cache.getMapItemStore(groupID, GFDefendArmyItem.class);
	}
	
}
