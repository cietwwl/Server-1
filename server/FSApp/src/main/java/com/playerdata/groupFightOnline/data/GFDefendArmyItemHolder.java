package com.playerdata.groupFightOnline.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
	private static AtomicInteger defendArmyVersion = new AtomicInteger(0);
	private static GFDefendArmyItemHolder instance = new GFDefendArmyItemHolder();

	public static GFDefendArmyItemHolder getInstance() {
		return instance;
	}

	private GFDefendArmyItemHolder() {
		
	}
	
	final private eSynType synType = eSynType.GFDefendArmyData;
	
	public int getCurrentVersion(){
		return defendArmyVersion.get();
	}
	
	/**
	 * 获取已经设置的防守队伍信息(公会级别)
	 * @param player
	 * @return
	 */
	public List<GFDefendArmyItem> getGroupItemList(Player player, String groupID)
	{
		return getGroupItemList(player, groupID, 0);
	}
	
	/**
	 * 获取已经设置的防守队伍信息(公会级别)
	 * @param player
	 * @param version
	 * @return
	 */
	public List<GFDefendArmyItem> getGroupItemList(Player player, String groupID, int version)
	{
		List<GFDefendArmyItem> defendArmyList = new ArrayList<GFDefendArmyItem>();
		Enumeration<GFDefendArmyItem> mapEnum = getItemStore(groupID).getEnum();
		while (mapEnum.hasMoreElements()) {
			GFDefendArmyItem item = (GFDefendArmyItem) mapEnum.nextElement();
			if(item.getVersion() > version)
				defendArmyList.add(item);
		}
		return defendArmyList;
	}
	
	/**
	 * 更新个人的一个防守队伍
	 * @param player
	 * @param item
	 */
	private int updateItem(Player player, GFDefendArmyItem item){
		int newVersion = defendArmyVersion.incrementAndGet();
		item.setVersion(newVersion);
		getItemStore(item.getGroupID()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
		return newVersion;
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
		if(itemlist.size() == 0) {
			initPersonalDefendArmy(player);
			return getItem(player);
		}
		return itemlist;
	}
	
	/**
	 * 获取个人的某一个队伍
	 * @param userId
	 * @param armyId
	 * @return
	 */
	public GFDefendArmyItem getItem(Player player, String armyId){
		String groupID = player.getGuildUserMgr().getGuildId();
		return getItemStore(groupID).getItem(armyId);
	}
	
	/**
	 * 
	 * @param player
	 * @param items
	 * @return
	 */
	private boolean addItems(Player player, List<GFDefendArmyItem> items){
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
	 * @return 最新的版本号
	 */
	public int resetItems(Player player, List<GFDefendArmyItem> items){
		int newVersion = defendArmyVersion.incrementAndGet();
		long operateTime = System.currentTimeMillis();
		for(GFDefendArmyItem item : items) {
			GFDefendArmyItem needUpateItem = getItem(player, item.getArmyID());
			if(needUpateItem == null) continue;  //TODO  需要错误log
			needUpateItem.setSetDefenderTime(operateTime);
			needUpateItem.setSimpleArmy(item.getSimpleArmy());
			needUpateItem.setState(GFArmyState.NORMAL.getValue());
			needUpateItem.setVersion(newVersion);
			updateItem(player, needUpateItem.getArmyID());
		}
		return newVersion;
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
	
	/**
	 * 只用来同步个人的防守队伍信息
	 * 帮派的其它防守队伍，需要用请求
	 * @param player
	 */
	public void synAllData(Player player){
		ClientDataSynMgr.synDataList(player, getItem(player), synType, eSynOpType.UPDATE_LIST);
	}
	
	/**
	 * 初始化玩家个人的防守队伍
	 * @param player
	 */
	private void initPersonalDefendArmy(Player player) {
		String groupID = player.getGuildUserMgr().getGuildId();
		List<GFDefendArmyItem> initItems = new ArrayList<GFDefendArmyItem>();
		for(int i = 1; i <= MAX_DEFEND_ARMY_COUNT; i++){
			String armyID = player.getUserId() + "_" + i;
			GFDefendArmyItem item = new GFDefendArmyItem();
			item.setArmyID(armyID);
			item.setUserID(player.getUserId());
			item.setGroupID(groupID);
			item.setTeamID(i);
			item.setState(GFArmyState.EMPTY.getValue());
			initItems.add(item);
		}
		addItems(player, initItems);
	}
	
	private MapItemStore<GFDefendArmyItem> getItemStore(String groupID) {
		MapItemStoreCache<GFDefendArmyItem> cache = MapItemStoreFactory.getGFDefendArmyCache();
		return cache.getMapItemStore(groupID, GFDefendArmyItem.class);
	}
}
