package com.playerdata.groupFightOnline.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.bm.GFightConst;
import com.playerdata.groupFightOnline.bm.GFightHelper;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.service.Email.EmailUtils;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GFFinalRewardItemHolder {
	private static GFFinalRewardItemHolder instance = new GFFinalRewardItemHolder();

	public static GFFinalRewardItemHolder getInstance() {
		return instance;
	}
	
	final private eSynType synType = eSynType.GFightFinalReward;
	
	public GFFinalRewardItem getGFReward(Player player, int resourceID, String rewardID){
		return getItemStore(player.getUserId()).getItem(rewardID);
	}
	
	public boolean addGFReward(String userID, int resourceID, GFFinalRewardItem rewardItem){
		return getItemStore(userID).addItem(rewardItem);
	}
	
	/**
	 * 移除单个奖励
	 * @param player
	 * @param resourceID
	 * @param rewardID
	 * @return
	 */
	public boolean removeSingleRewardItem(Player player, int resourceID, String rewardID){
		return getItemStore(player.getUserId()).removeItem(rewardID);
	}
	
	/**
	 * 移除单个奖励
	 * @param rewardID
	 * @return
	 */
	public boolean removeSingleRewardItem(String rewardID){
		String[] idArr = rewardID.split("_");
		if(idArr.length != 3) return false;
		//三个字符串分别代表:资源点id，角色id，奖励类型
		return getItemStore(idArr[1]).removeItem(rewardID);
	}
	
	/**
	 * 同步个人的所有奖励信息
	 * 如果有超时奖励,就发邮件,并删除
	 * @param player
	 */
	public void synData(Player player){
		List<GFFinalRewardItem> itemList = new ArrayList<GFFinalRewardItem>();
		long currentTime = System.currentTimeMillis();
		List<String> removeIDArr = new ArrayList<String>();
		Enumeration<GFFinalRewardItem> rewardEnum = getItemStore(player.getUserId()).getEnum();
		while(rewardEnum.hasMoreElements()){
			GFFinalRewardItem rwdItem = rewardEnum.nextElement();
			if(currentTime - rwdItem.getRewardGetTime() > GFightConst.REWARD_CONTAIN_TIME){
				EmailUtils.sendEmail(rwdItem.getUserID(), String.valueOf(rwdItem.getEmailId()), GFightHelper.itemListToString(rwdItem.getRewardContent()), rwdItem.getRewardDesc());
				removeIDArr.add(rwdItem.getId());
			}else{
				itemList.add(rwdItem);
			}
		}
		for(String removeID : removeIDArr){
			removeSingleRewardItem(removeID);
		}
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}
	
	/**
	 * 玩家的全部奖励
	 * @param userID
	 * @param resourceID
	 * @return
	 */
	private MapItemStore<GFFinalRewardItem> getItemStore(String userID) {
		MapItemStoreCache<GFFinalRewardItem> cache = MapItemStoreFactory.getGFFinalRewardItemCache();
		return cache.getMapItemStore(userID, GFFinalRewardItem.class);
	}
}
