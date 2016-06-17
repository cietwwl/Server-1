package com.playerdata.groupFightOnline.uData;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GFBiddingHolder {
	private static GFBiddingHolder instance = new GFBiddingHolder();

	public static GFBiddingHolder getInstance() {
		return instance;
	}

	private GFBiddingHolder() {
		
	}
	
	final private eSynType synType = eSynType.MagicChapterData;
	
	/**
	 * 获取某个资源点所有的压标信息
	 * @param resourceID
	 * @return
	 */
	public List<GFBiddingItem> getResourceItemList(String resourceID)
	{
		List<GFBiddingItem> resBiddingList = new ArrayList<GFBiddingItem>();
		Enumeration<GFBiddingItem> mapEnum = getItemStore(resourceID).getEnum();
		while (mapEnum.hasMoreElements()) {
			resBiddingList.add(mapEnum.nextElement());
		}
		return resBiddingList;
	}
	
	/**
	 * 获取某个公会所有的被压标信息
	 * 只用在最后奖励结算的时候，因为是遍历获取的
	 * @param resourceID
	 * @param groupID
	 * @return
	 */
	public List<GFBiddingItem> getGroupItemList(String resourceID, int groupID)
	{
		List<GFBiddingItem> resBiddingList = new ArrayList<GFBiddingItem>();
		Enumeration<GFBiddingItem> mapEnum = getItemStore(resourceID).getEnum();
		while (mapEnum.hasMoreElements()) {
			GFBiddingItem item = mapEnum.nextElement();
			if(item.getBidGroup() == groupID)
				resBiddingList.add(item);
		}
		return resBiddingList;
	}
	
	/**
	 * 更新个人的压标信息
	 * @param player
	 * @param item
	 */
	public void updateItem(Player player, GFBiddingItem item){
		getItemStore(item.getResourceID()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	/**
	 * 更新个人的压标信息
	 * @param player
	 * @param resourceID
	 */
	public void updateItem(Player player, String resourceID){
		GFBiddingItem item = getItem(player, resourceID);
		updateItem(player, item);
	}
	
	/**
	 *  获取个人的某一个资源点的压标信息
	 * @param userId
	 * @param resource_id
	 * @return
	 */
	public GFBiddingItem getItem(Player player, String resource_id){
		String itemID = player.getUserId() + "_" + resource_id;
		return getItemStore(player.getUserId()).getItem(itemID);
	}
	
	/**
	 * 添加一条压标信息
	 * @param player
	 * @param item
	 * @return
	 */
	public boolean addItem(Player player, GFBiddingItem item){
		return getItemStore(item.getResourceID()).addItem(item);
	}
	
	/**
	 * 移除某个资源点所有的压标
	 * @param resource_id
	 * @return
	 */
	public boolean removeItemsOnResource(String resource_id){
		return getItemStore(resource_id).clearAllRecords();
	}
	
	/**
	 * 同步个人的所有压标信息
	 * @param player
	 */
	public void synAllData(Player player){
		List<String> resList = new ArrayList<String>();
		List<GFBiddingItem> itemList = new ArrayList<GFBiddingItem>();
		for(String resourceID : resList)
			itemList.add(getItem(player, resourceID));
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	private MapItemStore<GFBiddingItem> getItemStore(String resourceID) {
		MapItemStoreCache<GFBiddingItem> cache = MapItemStoreFactory.getGFBiddingItemCache();
		return cache.getMapItemStore(resourceID, GFBiddingItem.class);
	}
}
