package com.playerdata.groupFightOnline.manager;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.groupFightOnline.data.GFBiddingItem;
import com.playerdata.groupFightOnline.data.GFBiddingItemHolder;

public class GFBiddingItemMgr {
	
	private static GFBiddingItemMgr instance = new GFBiddingItemMgr();

	public static GFBiddingItemMgr getInstance() {
		return instance;
	}
	
	/**
	 * 获取某个资源点所有的压标信息
	 * @param resourceID
	 * @return
	 */
	public List<GFBiddingItem> getResourceItemList(int resourceID){
		return GFBiddingItemHolder.getInstance().getResourceItemList(resourceID);
	}
	
	/**
	 * 获取某个公会所有的被压标信息
	 * 只用在最后奖励结算的时候，因为是遍历获取的
	 * @param resourceID
	 * @param groupID
	 * @return
	 */
	public List<GFBiddingItem> getGroupItemList(int resourceID, String groupID){
		return GFBiddingItemHolder.getInstance().getGroupItemList(resourceID, groupID);
	}
	
	/**
	 * 更新个人的压标信息
	 * @param player
	 * @param item
	 */
	public void updateItem(Player player, String biddingID, int rateID){
		
	}
	
	/**
	 * 更新个人的压标信息
	 * @param player
	 * @param resourceID
	 */
	public void updateItem(Player player, String resourceID){
		
	}
}
