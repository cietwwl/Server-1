package com.playerdata.groupFightOnline.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfgDAO;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GFBiddingItemHolder {
	
	final private eSynType synType = eSynType.GFBiddingData;
	
	private static GFBiddingItemHolder instance = new GFBiddingItemHolder();

	public static GFBiddingItemHolder getInstance() {
		return instance;
	}
	
	/**
	 * 获取某个资源点所有的压标信息
	 * 
	 * 主要用于资源点结算的时候算压标奖励
	 * @param resourceID
	 * @return
	 */
	public List<GFBiddingItem> getResourceItemList(int resourceID)
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
	 * 
	 * 只用在最后资源点奖励结算的时候，计算被压标数量奖励
	 * 是遍历获取的
	 * @param resourceID
	 * @param groupID
	 * @return
	 */
	public List<GFBiddingItem> getGroupItemList(int resourceID, String groupID)
	{
		List<GFBiddingItem> resBiddingList = new ArrayList<GFBiddingItem>();
		Enumeration<GFBiddingItem> mapEnum = getItemStore(resourceID).getEnum();
		while (mapEnum.hasMoreElements()) {
			GFBiddingItem item = mapEnum.nextElement();
			if(item.getBidGroup().equals(groupID))
				resBiddingList.add(item);
		}
		return resBiddingList;
	}
	
	/**
	 *  获取个人的某一个资源点的压标信息
	 * @param userId
	 * @param resourceID
	 * @return
	 */
	public GFBiddingItem getItem(Player player, int resourceID){
		String itemID = player.getUserId() + "_" + resourceID;
		return getItemStore(resourceID).getItem(itemID);
	}
	
	/**
	 *  移除个人的某一个资源点的压标信息
	 * @param userId
	 * @param resourceID
	 * @return
	 */
	public boolean removeItem(Player player, int resourceID){
		String itemID = player.getUserId() + "_" + resourceID;
		return getItemStore(resourceID).removeItem(itemID);
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
	 * @param resourceID
	 * @return
	 */
	public boolean removeItemsOnResource(int resourceID){
		return getItemStore(resourceID).clearAllRecords();
	}
	
	/**
	 * 同步个人的所有压标信息
	 * 所有资源点的
	 * @param player
	 */
	public void synAllData(Player player){
		List<GFightOnlineResourceCfg> cfgAll = GFightOnlineResourceCfgDAO.getInstance().getAllCfg();
		List<GFBiddingItem> itemList = new ArrayList<GFBiddingItem>();
		for(GFightOnlineResourceCfg cfg : cfgAll){
			GFBiddingItem bidItem = getItem(player, cfg.getResID());
			if(bidItem != null) itemList.add(getItem(player, cfg.getResID()));
		}
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	private MapItemStore<GFBiddingItem> getItemStore(int resourceID) {
		return getItemStore(String.valueOf(resourceID));
	}
	
	private MapItemStore<GFBiddingItem> getItemStore(String resourceID) {
		MapItemStoreCache<GFBiddingItem> cache = MapItemStoreFactory.getGFBiddingItemCache();
		return cache.getMapItemStore(String.valueOf(resourceID), GFBiddingItem.class);
	}
}
