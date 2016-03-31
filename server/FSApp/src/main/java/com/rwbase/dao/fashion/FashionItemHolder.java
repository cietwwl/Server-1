package com.rwbase.dao.fashion;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.readonly.FashionMgrIF.ItemFilter;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.common.NotifyChangeCallBack;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class FashionItemHolder extends NotifyChangeCallBack{
	final private String userId;
	final private eSynType fashionSynType = eSynType.FASHION_ITEM;
	
	public FashionItemHolder(String roleIdP) {
		userId = roleIdP;
	}

	public List<FashionItemIF> search(ItemFilter predicate){
		MapItemStore<FashionItem> itemStore = getItemStore();
		if (itemStore == null){ return new ArrayList<FashionItemIF>();}
		Enumeration<FashionItem> mapEnum = itemStore.getEnum();
		if (mapEnum == null){ return new ArrayList<FashionItemIF>();}
		
		List<FashionItemIF> itemList = new ArrayList<FashionItemIF>();
		while (mapEnum.hasMoreElements()) {
			FashionItem item = (FashionItem) mapEnum.nextElement();
			if (predicate.accept(item)){
				itemList.add(item);
			}
		}
		return itemList;
	}
	
	/*
	 * 获取用户已经拥有的时装
	 */
	public List<FashionItem> getItemList()
	{
		List<FashionItem> itemList = new ArrayList<FashionItem>();
		Enumeration<FashionItem> mapEnum = getItemStore().getEnum();
		while (mapEnum.hasMoreElements()) {
			FashionItem item = (FashionItem) mapEnum.nextElement();
			itemList.add(item);
		}
		return itemList;
	}
	
	public int getItemCount(){
		return getItemStore().getSize();
	}
	
	public void updateItem(Player player, FashionItem item){
		boolean updateResult = getItemStore().updateItem(item);
		if (!updateResult){
			GameLog.error("时装", player.getUserId(), "更新FashionItem失败，ID="+item.getId());
		}
		ClientDataSynMgr.updateData(player, item, fashionSynType, eSynOpType.UPDATE_SINGLE);
		notifyChange();
	}
	
	public FashionItem getItem(int fashionId){
		return getItemStore().getItem(String.valueOf(fashionId));
	}
	
	public FashionItem getItem(String itemId){
		return getItemStore().getItem(itemId);
	}

	/*暂时不用，先屏蔽
	public boolean removeItem(Player player, FashionItem item){
		boolean success = getItemStore().removeItem(item.getId());
		if(success){
			ClientDataSynMgr.updateData(player, item, fashionSynType, eSynOpType.REMOVE_SINGLE);
			notifyChange();
		}
		return success;
	}*/
	
	public boolean addItem(Player player, FashionItem item){
		boolean addSuccess = getItemStore().addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, fashionSynType, eSynOpType.ADD_SINGLE);
			notifyChange();
		}
		return addSuccess;
	}
	
	public void synAllData(Player player, int version){
		List<FashionItem> itemList = getItemList();			
		ClientDataSynMgr.synDataList(player, itemList, fashionSynType, eSynOpType.UPDATE_LIST);
	}

	
	public void flush(){
		getItemStore().flush();
	}
	
	private MapItemStore<FashionItem> getItemStore(){
		MapItemStoreCache<FashionItem> cache = MapItemStoreFactory.getFashionCache();
		return cache.getMapItemStore(userId, FashionItem.class);
	}
}
