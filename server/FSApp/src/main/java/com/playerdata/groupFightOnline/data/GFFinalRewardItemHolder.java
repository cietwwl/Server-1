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

public class GFFinalRewardItemHolder {
	private static GFFinalRewardItemHolder instance = new GFFinalRewardItemHolder();

	public static GFFinalRewardItemHolder getInstance() {
		return instance;
	}

	private GFFinalRewardItemHolder() {
		
	}
	
	final private eSynType synType = eSynType.GFBiddingData;
	
	/**
	 * 获取某个资源点所有的压标信息
	 * @param resourceID
	 * @return
	 */
	public List<GFFinalRewardItem> getResourceItemList(Player player, String resourceID)
	{
		List<GFFinalRewardItem> resBiddingList = new ArrayList<GFFinalRewardItem>();
		Enumeration<GFFinalRewardItem> mapEnum = getItemStore(player.g).getEnum();
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
		return getItemStore(resource_id).getItem(itemID);
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
	public boolean removeItemsOnResource(String resourceID){
		return getItemStore(resourceID).clearAllRecords();
	}
	
	/**
	 * 移除某个资源点所有的压标
	 * @param resourceID
	 * @return
	 */
	public boolean removeRewardItemOnResource(Player player, int resourceID){
		return removeItemsOnResource(String.valueOf(resourceID));
	}
	
	/**
	 * 同步个人的所有奖励信息
	 * @param player
	 */
	public void synData(Player player){
		List<GFFinalRewardItem> itemList = new ArrayList<GFFinalRewardItem>();
		List<GFightOnlineResourceCfg> resCfg = GFightOnlineResourceCfgDAO.getInstance().getAllCfg();
		for(GFightOnlineResourceCfg cfg : resCfg){
			Enumeration<GFFinalRewardItem> rewardEnum = getItemStore(player.getUserId(), cfg.getResID()).getEnum();
			while(rewardEnum.hasMoreElements()){
				itemList.add(rewardEnum.nextElement());
			}
		}
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	/**
	 * 玩家在某个资源点的全部奖励
	 * @param userID
	 * @param resourceID
	 * @return
	 */
	private MapItemStore<GFFinalRewardItem> getItemStore(String userID, int resourceID) {
		String ownerID = resourceID + "_" + userID;
		MapItemStoreCache<GFFinalRewardItem> cache = MapItemStoreFactory.getGFFinalRewardItemCache();
		return cache.getMapItemStore(ownerID, GFFinalRewardItem.class);
	}
}
