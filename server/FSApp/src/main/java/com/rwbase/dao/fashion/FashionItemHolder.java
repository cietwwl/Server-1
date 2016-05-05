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

public class FashionItemHolder{
	final private String userId;
	final private eSynType fashionSynType = eSynType.FASHION_ITEM;
	private NotifyChangeCallBack notifyProxy;

	/**
	 * 使用传入的消息通知代理来通知增删改
	 * @param roleIdP
	 * @param proxy
	 */
	public FashionItemHolder(String roleIdP,NotifyChangeCallBack proxy) {
		userId = roleIdP;
		notifyProxy = proxy;
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
			if (userId.equals(item.getUserId())){
				itemList.add(item);
			}
		}
		return itemList;
	}
	
	public List<FashionItem> getBroughtItemList()
	{
		List<FashionItem> itemList = new ArrayList<FashionItem>();
		Enumeration<FashionItem> mapEnum = getItemStore().getEnum();
		while (mapEnum.hasMoreElements()) {
			FashionItem item = (FashionItem) mapEnum.nextElement();
			if (item.isBrought() && userId.equals(item.getUserId())){
				itemList.add(item);
			}
		}
		return itemList;
	}
	
	public void updateItem(Player player, FashionItem item){
		boolean updateResult = getItemStore().updateItem(item);
		if (!updateResult){
			GameLog.error("时装", player.getUserId(), "更新FashionItem失败，ID="+item.getId());
		}
		ClientDataSynMgr.updateData(player, item, fashionSynType, eSynOpType.UPDATE_SINGLE);
		notifyProxy.delayNotify();
	}
	
	public FashionItem getItem(int fashionModelId){
		return getItemStore().getItem(userId + "_" + fashionModelId);
	}
	
	public boolean removeItem(Player player, FashionItem item){
		boolean success = getItemStore().removeItem(item.getId());//player.getUserId()+"_"+
		if(success){
			ClientDataSynMgr.updateData(player, item, fashionSynType, eSynOpType.REMOVE_SINGLE);
			notifyProxy.delayNotify();
		}else{
			GameLog.error("时装", player.getUserId(), "删除时装失败:"+item.getId());
		}
		return success;
	}
	
	public boolean addItem(Player player, FashionItem item){
		boolean addSuccess = getItemStore().addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, fashionSynType, eSynOpType.ADD_SINGLE);
			notifyProxy.delayNotify();
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
