package com.playerdata.groupFightOnline.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.dataForClient.GFDefendArmySimpleLeader;
import com.playerdata.groupFightOnline.enums.GFArmyState;
import com.playerdata.groupFightOnline.enums.GFResourceState;
import com.playerdata.groupFightOnline.manager.GFightOnlineGroupMgr;
import com.playerdata.groupFightOnline.manager.GFightOnlineResourceMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GFDefendArmyItemHolder {

	private static GFDefendArmyItemHolder instance = new GFDefendArmyItemHolder();

	public static GFDefendArmyItemHolder getInstance() {
		return instance;
	}

	final private eSynType synSelfType = eSynType.GFDefendArmyData;
	final private eSynType synSimpleLeaderType = eSynType.GFightSimpleLeader;
	private static int MAX_DEFEND_ARMY_COUNT = 5; // 每个人最多设置的防守队伍数量

	private Map<String, AtomicInteger> versionMap = new ConcurrentHashMap<String, AtomicInteger>();

	/**
	 * 获取个人的所有队伍
	 * 
	 * @param userId
	 * @param armyId
	 * @return
	 */
	public List<GFDefendArmyItem> getUserDefArmyList(Player player) {
		String userId = player.getUserId();
		String groupID = GroupHelper.getInstance().getUserGroupId(userId);
		List<GFDefendArmyItem> itemlist = new ArrayList<GFDefendArmyItem>();
		if (StringUtils.isNotBlank(groupID)) {
			if (!haveDefenders(player)) {
				initPersonalDefendArmy(player);
			}
			List<GFDefendArmyItem> itemTmpList = GFDefendArmyItemHolder.getInstance().getItemList(groupID);
			for (GFDefendArmyItem itemTmp : itemTmpList) {
				if (StringUtils.equals(itemTmp.getUserID(), userId)) {
					itemlist.add(itemTmp);
				}
			}
		}
		return itemlist;
	}

	/**
	 * 获取帮派所有的防守队伍
	 * 
	 * @param groupId
	 * @return
	 */
	public List<GFDefendArmyItem> getItemList(String groupId) {
		List<GFDefendArmyItem> itemList = new ArrayList<GFDefendArmyItem>();
		Enumeration<GFDefendArmyItem> mapEnum = getItemStore(groupId).getEnum();
		while (mapEnum.hasMoreElements()) {
			GFDefendArmyItem item = (GFDefendArmyItem) mapEnum.nextElement();
			itemList.add(item);
		}
		return itemList;
	}

	/**
	 * 查看某公会的某防守队伍
	 * 
	 * @param groupId
	 * @param armyId
	 * @return
	 */
	public GFDefendArmyItem getItem(String groupId, String armyId) {
		return getItemStore(groupId).getItem(armyId);
	}

	/**
	 * 更新一个防守队伍
	 * 
	 * @param item
	 * @return
	 */
	public boolean updateItem(GFDefendArmyItem item) {
		String groupId = item.getGroupID();
		boolean updateSuccess = getItemStore(groupId).updateItem(item);
		updateVersion(groupId);
		return updateSuccess;
	}

	/**
	 * 添加一个防守队伍
	 * 
	 * @param item
	 * @return
	 */
	public boolean addItem(GFDefendArmyItem item) {
		String groupId = item.getGroupID();
		boolean addSuccess = getItemStore(groupId).addItem(item);
		updateVersion(groupId);
		return addSuccess;
	}

	/**
	 * 添加一组防守队伍
	 * 
	 * @param groupId
	 * @param itemList
	 * @return
	 */
	public boolean addItemList(String groupId, List<GFDefendArmyItem> itemList) {
		try {
			boolean addSuccess = getItemStore(groupId).addItem(itemList);
			updateVersion(groupId);
			return addSuccess;
		} catch (DuplicatedKeyException e) {
			// handle..
			e.printStackTrace();
			return false;
		}
	}

	public void synByVersion(Player player, String groupId, int version) {
		AtomicInteger curVersion = versionMap.get(groupId);
		if (curVersion == null) {
			curVersion = new AtomicInteger();
			versionMap.put(groupId, curVersion);
		}
		if (curVersion.get() != version || curVersion.get() == 0) {
			synSimpleLeaderData(player, groupId);
			if (StringUtils.equals(GroupHelper.getInstance().getUserGroupId(player.getUserId()), groupId)) {
				synSelfData(player);
			}
		}
	}

	private void synSimpleLeaderData(Player player, String groupId) {
		List<GFDefendArmyItem> serverDataList = getItemList(groupId);
		List<GFDefendArmySimpleLeader> simpleLeaderList = new ArrayList<GFDefendArmySimpleLeader>();
		for (GFDefendArmyItem item : serverDataList) {
			GFDefendArmySimpleLeader simpleLeader = item.getSimpleLeader();
			if (simpleLeader != null)
				simpleLeaderList.add(item.getSimpleLeader());
		}
		int curVersion = versionMap.get(groupId).get();
		ClientDataSynMgr.synDataGroupList(player, groupId, simpleLeaderList, synSimpleLeaderType, eSynOpType.UPDATE_LIST, curVersion);
	}

	private void synSelfData(Player player) {
		List<GFDefendArmyItem> items = getUserDefArmyList(player);
		if (items.size() == 0)
			return;
		ClientDataSynMgr.synDataList(player, items, synSelfType, eSynOpType.UPDATE_LIST);
	}

	private void updateVersion(String groupId) {
		if (versionMap.get(groupId) == null) {
			versionMap.put(groupId, new AtomicInteger());
		}
		versionMap.get(groupId).incrementAndGet();
	}

	/**
	 * 判断玩家是否初始化过防守队伍
	 * 
	 * @param player
	 * @return
	 */
	private boolean haveDefenders(Player player) {
		String groupID = GroupHelper.getInstance().getUserGroupId(player.getUserId());
		if (!StringUtils.isNotBlank(groupID))
			return false;
		return getItemStore(groupID).getItem(player.getUserId() + "_1") != null;
	}

	/**
	 * 初始化玩家个人的防守队伍
	 * 
	 * @param player
	 */
	private void initPersonalDefendArmy(Player player) {
		String groupID = GroupHelper.getInstance().getUserGroupId(player.getUserId());
		List<GFDefendArmyItem> initItems = new ArrayList<GFDefendArmyItem>();
		for (int i = 1; i <= MAX_DEFEND_ARMY_COUNT; i++) {
			String armyID = player.getUserId() + "_" + i;
			GFDefendArmyItem item = new GFDefendArmyItem();
			item.setArmyID(armyID);
			item.setUserID(player.getUserId());
			item.setGroupID(groupID);
			item.setTeamID(i);
			item.setState(GFArmyState.EMPTY.getValue());
			initItems.add(item);
		}
		addItemList(groupID, initItems);
	}

	public void removePersonalDefendArmy(String userID, String groupID) {
		GFightOnlineGroupData gfgData = GFightOnlineGroupMgr.getInstance().get(groupID);
		if (gfgData == null)
			return;
		GFightOnlineResourceData resData = GFightOnlineResourceMgr.getInstance().get(gfgData.getResourceID());
		if (resData == null || GFResourceState.FIGHT.equals(resData.getState()))
			return;
		for (int i = 1; i <= MAX_DEFEND_ARMY_COUNT; i++) {
			String armyID = userID + "_" + i;
			GFDefendArmyItem armyItem = getItemStore(groupID).getItem(armyID);
			if (getItemStore(groupID).removeItem(armyID) && armyItem != null && GFArmyState.NORMAL.equals((armyItem.getState()))) {
				GFightOnlineGroupMgr.getInstance().addDefenderCount(groupID, -1);
			}
		}
		versionMap.get(groupID).getAndIncrement();
	}

	private MapItemStore<GFDefendArmyItem> getItemStore(String groupID) {
		MapItemStoreCache<GFDefendArmyItem> cache = MapItemStoreFactory.getGFDefendArmyCache();
		return cache.getMapItemStore(groupID, GFDefendArmyItem.class);
	}
}
