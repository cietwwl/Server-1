package com.playerdata.groupFightOnline.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.dataForClient.GFArmyState;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GFDefendArmyItemHolder {
	public static int MAX_DEFEND_ARMY_COUNT = 5;
	private static GFDefendArmyItemHolder instance = new GFDefendArmyItemHolder();

	public static GFDefendArmyItemHolder getInstance() {
		return instance;
	}

	private GFDefendArmyItemHolder() {
		
	}
	
	final private eSynType synType = eSynType.GFDefendArmyData;
	
	/**
	 * 获取已经设置的防守队伍信息(公会级别)
	 * @param player
	 * @return
	 */
	public List<GFDefendArmyItem> getGroupItemList(Player player)
	{
		String groupID = player.getGuildUserMgr().getGuildId();
		List<GFDefendArmyItem> defendArmyList = new ArrayList<GFDefendArmyItem>();
		// TODO 需要添加判断，公会是否进入争夺战
		Enumeration<GFDefendArmyItem> mapEnum = getItemStore(groupID).getEnum();
		while (mapEnum.hasMoreElements()) {
			GFDefendArmyItem item = (GFDefendArmyItem) mapEnum.nextElement();
			defendArmyList.add(item);
		}
		return defendArmyList;
	}
	
	/**
	 * 更新个人的一个防守队伍
	 * @param player
	 * @param item
	 */
	public void updateItem(Player player, GFDefendArmyItem item){
		getItemStore(item.getGroupID()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	/**
	 * 更新个人的一个防守队伍
	 * @param player
	 * @param armyId
	 */
	public void updateItem(Player player, String armyId){
		GFDefendArmyItem item = getItem(player, armyId);
		updateItem(player, item);
	}
	
	/**
	 *  获取个人的所有队伍
	 * @param userId
	 * @param armyId
	 * @return
	 */
	public List<GFDefendArmyItem> getItem(Player player){
		String groupID = player.getGuildUserMgr().getGuildId();
		Enumeration<GFDefendArmyItem> itemEnum = getItemStore(groupID).getEnum();
		List<GFDefendArmyItem> itemlist = new ArrayList<GFDefendArmyItem>();
		while(itemEnum.hasMoreElements()) {
			GFDefendArmyItem item = itemEnum.nextElement();
			if(item.getUserID().equals(player.getUserId()))
				itemlist.add(item);
		}
		// 如果个人防守队伍为空，就为个人创建最大数量的空队伍
		return itemlist;
	}
	
	/**
	 *  获取个人的某一个队伍
	 * @param userId
	 * @param armyId
	 * @return
	 */
	public GFDefendArmyItem getItem(Player player, String armyId){
		String groupID = player.getGuildUserMgr().getGuildId();
		String itemID = player.getUserId() + "_" + armyId;
		return getItemStore(groupID).getItem(itemID);
	}
	
	/**
	 * 
	 * @param player
	 * @param items
	 * @return
	 */
	public boolean addItems(Player player, List<GFDefendArmyItem> items){
		String groupID = player.getGuildUserMgr().getGuildId();
		try {
			return getItemStore(groupID).addItem(items);
		} catch (DuplicatedKeyException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 重置个人的防守队伍信息
	 * @param player
	 * @param items
	 * @return
	 */
	public boolean resetItems(Player player, List<GFDefendArmyItem> items){
		removePersonalArmy(player);
		return addItems(player, items);
	}
	
	/**
	 * 移除个人的防守队伍
	 * @param player
	 */
	public void removePersonalArmy(Player player){
		String groupID = player.getGuildUserMgr().getGuildId();
		List<String> selfArmyIDArr = new ArrayList<String>();
		for(int i = 0; i < MAX_DEFEND_ARMY_COUNT; i++)
			selfArmyIDArr.add(player.getUserId() + "_" + i);
		getItemStore(groupID).removeItem(selfArmyIDArr);
	}
	
	/**
	 * 清除所有公会的所有防守队伍
	 * @param groupIDArr
	 * @return
	 */
	public boolean clearTheRecords(List<String> groupIDArr){
		MapItemStoreCache<GFDefendArmyItem> cache = MapItemStoreFactory.getGFDefendArmyCache();
		boolean result = true;
		for (String groupID : groupIDArr)
			if(!cache.getMapItemStore(groupID, GFDefendArmyItem.class).clearAllRecords()) 
				result = false;
		return result;
	}
	
	public void synAllData(Player player){
		ClientDataSynMgr.synDataList(player, getGroupItemList(player), synType, eSynOpType.UPDATE_LIST);
	}
	
	public void synPersonalData(Player player){
		ClientDataSynMgr.synDataList(player, getItem(player), synType, eSynOpType.UPDATE_LIST);
	}

	private MapItemStore<GFDefendArmyItem> getItemStore(String groupID) {
		MapItemStoreCache<GFDefendArmyItem> cache = MapItemStoreFactory.getGFDefendArmyCache();
		return cache.getMapItemStore(groupID, GFDefendArmyItem.class);
	}
	
	private void initPersonalDefendArmy(Player player) {
		List<GFDefendArmyItem> initItems = new ArrayList<GFDefendArmyItem>();
		for(int i = 1; i <= MAX_DEFEND_ARMY_COUNT; i++){
			String teamID = player.getUserId() + "_" + i;
			GFDefendArmyItem item = new GFDefendArmyItem();
			item.setArmyID(teamID);
			item.setUserID(player.getUserId());
			item.setState(GFArmyState.EMPTY.getValue());
			initItems.add(item);
		}
		addItems(player, initItems);
	}
}
