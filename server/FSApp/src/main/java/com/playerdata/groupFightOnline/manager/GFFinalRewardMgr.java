package com.playerdata.groupFightOnline.manager;

import com.playerdata.Player;
import com.playerdata.groupFightOnline.data.GFFinalRewardItem;
import com.playerdata.groupFightOnline.data.GFFinalRewardItemHolder;
import com.playerdata.groupFightOnline.enums.GFRewardType;

public class GFFinalRewardMgr {
	
	private static GFFinalRewardMgr instance = new GFFinalRewardMgr();
	
	public static GFFinalRewardMgr getInstance() {
		return instance;
	}
	
	/**
	 * 给玩家添加一条奖励
	 * @param player
	 * @param resourceID
	 * @param rewardType
	 * @return
	 */
	public boolean addGFReward(String userID, int resourceID, GFFinalRewardItem rewardItem){
		return GFFinalRewardItemHolder.getInstance().addGFReward(userID, resourceID, rewardItem);
	}
	
	/**
	 * 移除某个资源点所有的奖励
	 * @param player
	 * @param resourceID
	 * @return
	 */
	public boolean removeAllRewardItem(Player player, int resourceID){
		return GFFinalRewardItemHolder.getInstance().removeAllRewardItem(player, resourceID);
	}
	
	/**
	 * 移除单个奖励
	 * @param player
	 * @param resourceID
	 * @param rewardID
	 * @return
	 */
	public boolean removeRewardItemOnResources(Player player, int resourceID, String rewardID){
		return GFFinalRewardItemHolder.getInstance().removeSingleRewardItem(player, resourceID, rewardID);
	}
	
	/**
	 * 同步个人的所有奖励信息
	 * @param player
	 */
	public void synData(Player player){
		GFFinalRewardItemHolder.getInstance().synData(player);
	}
	
	public String getOwnerID(String userID, int resourceID){
		return resourceID + "_" + userID;
	}
	
	public String getRewardID(String userID, int resourceID, GFRewardType rewardType){
		return resourceID + "_" + userID + "_" + rewardType.getValue();
	}
}
