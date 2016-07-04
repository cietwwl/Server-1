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
	
	final private eSynType synType = eSynType.GFBiddingData;
	
	public GFFinalRewardItem getGFReward(Player player, int resourceID, String rewardID){
		return getItemStore(player.getUserId(), resourceID).getItem(rewardID);
	}
	
	public boolean addGFReward(String userID, int resourceID, GFFinalRewardItem rewardItem){
		return getItemStore(userID, resourceID).addItem(rewardItem);
	}
	
	/**
	 * 移除某个资源点所有的奖励
	 * @param player
	 * @param resourceID
	 * @return
	 */
	public boolean removeAllRewardItem(Player player, int resourceID){
		return getItemStore(player.getUserId(), resourceID).clearAllRecords();
	}
	
	/**
	 * 移除单个奖励
	 * @param player
	 * @param resourceID
	 * @param rewardID
	 * @return
	 */
	public boolean removeSingleRewardItem(Player player, int resourceID, String rewardID){
		return getItemStore(player.getUserId(), resourceID).removeItem(rewardID);
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
