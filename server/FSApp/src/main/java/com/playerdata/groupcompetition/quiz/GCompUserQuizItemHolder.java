package com.playerdata.groupcompetition.quiz;

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

public class GCompUserQuizItemHolder {
	
	final private eSynType synType = eSynType.GFBiddingData;
	
	private static GCompUserQuizItemHolder instance = new GCompUserQuizItemHolder();

	public static GCompUserQuizItemHolder getInstance() {
		return instance;
	}

	/**
	 * 更新个人的竞猜信息
	 * @param player
	 * @param item
	 */
	public void updateItem(Player player, GCompUserQuizItem item){
		getItemStore(player.getUserId()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	/**
	 * 检查是否已竞猜
	 * @param player
	 * @param item
	 */
	public boolean containsItem(Player player, int marchId){
		return getItemStore(player.getUserId()).getItem(player.getUserId() + "_" + marchId) == null;
	}
	
	/**
	 * 添加一条压标信息
	 * @param player
	 * @param item
	 * @return
	 */
	public boolean addItem(Player player, GCompUserQuizItem item){
		return getItemStore(player.getUserId()).addItem(item);
	}
	
	/**
	 * 同步个人的所有竞猜信息
	 * @param player
	 */
	public void synAllData(Player player){
		List<GCompUserQuizItem> itemList = getItemList(player.getUserId());
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}
	
	/**
	 * 获取一个玩家所有的竞猜项目
	 * 如果到了新的一届，会清除老一届的数据
	 * @param userId
	 * @return
	 */
	public List<GCompUserQuizItem> getItemList(String userId){
		List<GCompUserQuizItem> itemList = new ArrayList<GCompUserQuizItem>();
		MapItemStore<GCompUserQuizItem> mapItem = getItemStore(userId);
		if(null == mapItem){
			return itemList;
		}
		boolean needClear = false;
		Enumeration<GCompUserQuizItem> itemEnum = mapItem.getEnum();
		while(itemEnum.hasMoreElements()){
			GCompUserQuizItem item = itemEnum.nextElement();
			if(item.getSessionId() < getCurrentSessionID()){
				needClear = true;
				break;
			}
			itemList.add(item);
		}
		if(needClear){
			mapItem.clearAllRecords();
		}
		return itemList;
	}
	
	private MapItemStore<GCompUserQuizItem> getItemStore(String userId) {
		MapItemStoreCache<GCompUserQuizItem> cache = MapItemStoreFactory.getGCompQuizItemCache();
		return cache.getMapItemStore(userId, GCompUserQuizItem.class);
	}
	
	/**
	 * 获取当前阶段的赛事id
	 * @return
	 */
	public List<Integer> getCurrentMatchID(){
		return new ArrayList<Integer>();
	}
	
	/**
	 * 获取当前阶段的赛事id
	 * @return
	 */
	public static int getCurrentSessionID(){
		return 0;
	}
	
	/**
	 * 获取当前阶段的赛事id
	 * @return
	 */
	public static String getCurrentStageID(){
		return "";
	}
}
