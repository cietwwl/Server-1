package com.playerdata.groupFightOnline.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.groupFightOnline.data.GFDefendArmyItem;
import com.playerdata.groupFightOnline.data.GFDefendArmyItemHolder;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupHolder;
import com.playerdata.groupFightOnline.data.UserGFightOnlineData;
import com.playerdata.groupFightOnline.data.UserGFightOnlineHolder;
import com.playerdata.groupFightOnline.dataException.GFArmyDataException;
import com.playerdata.groupFightOnline.dataException.HaveSelectEnimyException;
import com.playerdata.groupFightOnline.dataException.NoSuitableDefenderException;
import com.playerdata.groupFightOnline.dataForClient.DefendArmyHerosInfo;
import com.playerdata.groupFightOnline.dataForClient.DefendArmySimpleInfo;
import com.playerdata.groupFightOnline.dataForClient.GFArmyState;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.common.MapItemStoreFactory;

public class GFDefendArmyMgr {
	
	private static int MAX_DEFEND_ARMY_COUNT = 5;	//每个人最多设置的防守队伍数量
	
	// public static int LOCK_ITEM_MAX_TIME = 10 * 60 * 1000;	//被选中或战斗锁定时间2分钟
	
	final public static int LOCK_ITEM_MAX_TIME = 60 * 1000;
	
	private static GFDefendArmyMgr instance;
	
	public static GFDefendArmyMgr getInstance() {
		return instance;
	}
	
	
	public void synGroupData(Player player, String groupId, int version){
		
		GFDefendArmyItemHolder.getInstance().synByVersion(player, groupId, version);
		
		
	}
	
	
//	/*
//	 * 获取用户已经拥有的时装
//	 */
//	public List<GFDefendArmyItem> getItemList(String groupId)	
//	{		
//		List<GFDefendArmyItem> itemList = new ArrayList<GFDefendArmyItem>();
//		Enumeration<GFDefendArmyItem> mapEnum = getItemStore(groupId).getEnum();
//		while (mapEnum.hasMoreElements()) {
//			GFDefendArmyItem item = (GFDefendArmyItem) mapEnum.nextElement();			
//			itemList.add(item);
//		}
//		
//		return itemList;
//	}
//	
//	/**
//	 * 获取已经设置的防守队伍信息(公会级别)
//	 * @param player
//	 * @return
//	 */
//	public List<GFDefendArmyItem> getGroupItemList(Player player, String groupID){
//		return getGroupItemList(player, groupID, 0);
//	}
//	
//	/**
//	 * 获取已经设置的防守队伍信息(公会级别)
//	 * @param player
//	 * @param version
//	 * @return
//	 */
//	public List<GFDefendArmyItem> getGroupItemList(Player player, String groupID, int version){
//		List<GFDefendArmyItem> defendArmyList = new ArrayList<GFDefendArmyItem>();
//		Enumeration<GFDefendArmyItem> mapEnum = getItemStore(groupID).getEnum();
//		while (mapEnum.hasMoreElements()) {
//			GFDefendArmyItem item = (GFDefendArmyItem) mapEnum.nextElement();
//			//if(item.getVersion() > version && !GFArmyState.EMPTY.equals(item.getState()))
//			if(!GFArmyState.EMPTY.equals(item.getState()))
//				defendArmyList.add(item);
//		}
//		return defendArmyList;
//	}
	
	/**
	 *  获取个人的所有队伍
	 * @param userId
	 * @param armyId
	 * @return
	 */
	private List<GFDefendArmyItem> getUserDefArmyList(Player player){
		String userId = player.getUserId();
		String groupID = GroupHelper.getUserGroupId(userId);
		List<GFDefendArmyItem> itemlist = new ArrayList<GFDefendArmyItem>();
		if(StringUtils.isNotBlank(groupID)){
			
			List<GFDefendArmyItem> itemTmpList = GFDefendArmyItemHolder.getInstance().getItemList(groupID);
			// 如果个人防守队伍为空，就为个人创建最大数量的空队伍
			if(!isPlayerHasDefArmy(userId, itemTmpList)){
				initPersonalDefendArmy(player);
			}
			
			for (GFDefendArmyItem itemTmp : itemTmpList) {
				if(StringUtils.equals(itemTmp.getUserID(), userId)){					
					itemlist.add(itemTmp);
				}
			}
			
		}
		
		return itemlist;
	}


	private boolean isPlayerHasDefArmy(String userId,List<GFDefendArmyItem> itemTmpList) {
		boolean isPlayerHasArmy = false;
		for (GFDefendArmyItem itemTmp : itemTmpList) {
			if(StringUtils.equals(itemTmp.getUserID(), userId)){
				isPlayerHasArmy = true;
				break;
			}
		}
		return isPlayerHasArmy;
	}
	
	/**
	 * 获取个人的某一个队伍
	 * @param userId
	 * @param armyId
	 * @return
	 */
	private GFDefendArmyItem getItem(Player player, String armyId){
		List<GFDefendArmyItem> selfItems = getUserDefArmyList(player);
		for(GFDefendArmyItem item : selfItems){
			if(item.getArmyID().equals(armyId))
				return item;
		}
		return null;
	}
	
	/**
	 * 查看某公会的某防守队伍
	 * @param groupId
	 * @param armyId
	 * @return
	 */
	public GFDefendArmyItem getItem(String groupId, String armyId){
		
		List<GFDefendArmyItem> itemTmpList = GFDefendArmyItemHolder.getInstance().getItemList(groupId);
		GFDefendArmyItem target = null;
		for (GFDefendArmyItem itemTmp : itemTmpList) {
			if(StringUtils.equals(armyId, itemTmp.getArmyID())){
				target = itemTmp;
				break;
			}			
		}
		
		
		return target;
	}
	
	/**
	 * 更新一条自己防守队伍信息（一定是自己的帮派）
	 * 主要用在备战阶段
	 * @param player
	 * @param armyItem 队伍信息
	 * @param state 最新的状态
	 */
	public void updateItem(Player player, GFDefendArmyItem armyItem, GFArmyState state){
		String groupId = GroupHelper.getUserGroupId(player.getUserId());	
		updateItem(groupId, armyItem, state);
	}
	/**
	 * 更新一条自己防守队伍信息（一定是自己的帮派）
	 * 主要用在备战阶段
	 * @param player
	 * @param armyItem 队伍信息
	 * @param state 最新的状态
	 */
	public void updateItem(String groupId , GFDefendArmyItem armyItem, GFArmyState state){
		
		if(state.equals(GFArmyState.EMPTY)){
			GFightOnlineGroupHolder.getInstance().addDefenderCount(groupId, -1);
		}else {			
			if(state.equals(GFArmyState.NEWADD)) 
				GFightOnlineGroupHolder.getInstance().addDefenderCount(groupId, 1);
			if(state.equals(GFArmyState.DEFEATED)) 
				GFightOnlineGroupHolder.getInstance().deductAliveCount(groupId);
		}
		armyItem.setState(state.getValue());
		GFDefendArmyItemHolder.getInstance().updateItem(armyItem);
	}
	
	/**
	 * 重置个人的防守队伍信息
	 * @param player
	 * @param items
	 * @return 最新的版本号
	 * @throws GFArmyDataException 
	 */
	public void resetItems(Player player, List<DefendArmyHerosInfo> items) throws GFArmyDataException{
		HashSet<String> heroIDRepeatCheckSet = new HashSet<String>();
		for(DefendArmyHerosInfo item : items){
			for(String id : item.getHeroIDs()){
				if(heroIDRepeatCheckSet.contains(id)) throw new GFArmyDataException("一个英雄不能存在于两个队伍");
				heroIDRepeatCheckSet.add(id);
			}
		}
		for(DefendArmyHerosInfo heros : items) {
			GFDefendArmyItem armyItem = getItem(player, heros.getDefendArmyID());
			if(heros.getHeroIDs().size() == 0) {
				if(GFArmyState.NORMAL.equals(armyItem.getState())){
					armyItem.setSimpleArmy(null);
					updateItem(player, armyItem, GFArmyState.EMPTY);
				}else if(!GFArmyState.EMPTY.equals(armyItem.getState())){
					throw new GFArmyDataException("非NORMAL和EMPTY状态的队伍，不能置空");
				}
			} else {
				ArmyInfoSimple simpleArmy = ArmyInfoHelper.getSimpleInfo(player.getUserId(), heros.getMagicID(), heros.getHeroIDs());
				if(simpleArmy == null) throw new GFArmyDataException("heros无法生成防守队伍信息");
				long operateTime = System.currentTimeMillis();
				armyItem.setSetDefenderTime(operateTime);
				armyItem.setSimpleArmy(simpleArmy);
				if(GFArmyState.EMPTY.equals(armyItem.getState())){
					updateItem(player, armyItem, GFArmyState.NEWADD);
				}else if(GFArmyState.NORMAL.equals(armyItem.getState())){
					updateItem(player, armyItem, GFArmyState.NORMAL);
				}else throw new GFArmyDataException("非NORMAL和EMPTY状态的队伍，不能更改");
			}
		}
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
	 * 清除所有公会的所有防守队伍
	 * @param groupIDArr
	 * @return
	 */
	public boolean clearAllRecords(String groupID){
		MapItemStoreCache<GFDefendArmyItem> cache = MapItemStoreFactory.getGFDefendArmyCache();
		return cache.getMapItemStore(groupID, GFDefendArmyItem.class).clearAllRecords();
	}
	
//	/**
//	 * 只用来同步个人的防守队伍信息
//	 * 帮派的其它防守队伍，需要用请求
//	 * @param player
//	 */
//	public void synSelfData(Player player){
//		ClientDataSynMgr.synDataList(player, getUserDefArmyList(player), synType, eSynOpType.UPDATE_PART_LIST, defendArmyVersion.get());
//	}


	public void startFight(Player player, GFDefendArmyItem armyItem) {
		// 逻辑判断都已经在函数调用之前做了
		armyItem.setState(GFArmyState.FIGHTING.getValue());
		armyItem.setLastOperateTime(System.currentTimeMillis());
		GFDefendArmyItemHolder.getInstance().updateItem(armyItem);
	}
	
	/**
	 * 选择一个待战对手
	 * @param player
	 * @param groupID
	 * @param isIgnoreExistEnimy 是否忽略已经有的对手
	 * @return
	 * @throws HaveSelectEnimyException 
	 * @throws NoSuitableDefenderException 
	 */
	public boolean selectEnimyItem(Player player, String groupID, boolean isIgnoreExistEnimy) throws HaveSelectEnimyException, NoSuitableDefenderException{
		synchronized (GFDefendArmyItem.class) {
			UserGFightOnlineData userGFData = UserGFightOnlineHolder.getInstance().get(player.getUserId());
			DefendArmySimpleInfo randomDefender = userGFData.getRandomDefender();
			if(!isIgnoreExistEnimy && randomDefender != null && System.currentTimeMillis() - randomDefender.getLockArmyTime() <= LOCK_ITEM_MAX_TIME) {
				throw new HaveSelectEnimyException("已经选择过对手");
			}
			GFDefendArmyItem canFightItem = getCanFightItem(groupID);
			if(canFightItem == null) throw new NoSuitableDefenderException("找不到可以挑战的队伍");
			canFightItem.setState(GFArmyState.SELECTED.getValue());
			canFightItem.setLastOperateTime(System.currentTimeMillis());
			
			
			DefendArmySimpleInfo defenderSimple = new DefendArmySimpleInfo();
			defenderSimple.setGroupID(groupID);
			defenderSimple.setDefendArmyID(canFightItem.getArmyID());
			defenderSimple.setLockArmyTime(System.currentTimeMillis());
			userGFData.setRandomDefender(defenderSimple);
			
			UserGFightOnlineHolder.getInstance().update(player, userGFData);
			
			GFDefendArmyItemHolder.getInstance().updateItem(canFightItem);
			return true;
		}
	}
	
	/**
	 * 更换一个对手
	 * @param player
	 * @param groupID
	 * @param selectedID
	 * @return
	 * @throws NoSuitableDefenderException 
	 * @throws HaveSelectEnimyException 
	 */
	public boolean changeEnimyItem(Player player, String groupID) throws HaveSelectEnimyException, NoSuitableDefenderException{
		synchronized (GFDefendArmyItem.class) {
			//找到之前被选中的
			UserGFightOnlineData userGFData = UserGFightOnlineHolder.getInstance().get(player.getUserId());
			DefendArmySimpleInfo randomDefender = userGFData.getRandomDefender();
			if(!selectEnimyItem(player, groupID, true)) return false;
			if(randomDefender != null) { //如果不存在，直接不管
				GFDefendArmyItem selectedItem = getItem(groupID, randomDefender.getDefendArmyID());
				if(selectedItem != null){
					selectedItem.setState(GFArmyState.NORMAL.getValue());
					GFDefendArmyItemHolder.getInstance().updateItem(selectedItem);
				}
			}
			return true;
		}
	}
	
	/**
	 * 随机选取一个可以被攻击的防守队伍
	 * @param groupID 帮派id
	 * @return
	 */
	private GFDefendArmyItem getCanFightItem(String groupID){
		
		List<GFDefendArmyItem> canFightList = new ArrayList<GFDefendArmyItem>();
		List<GFDefendArmyItem> itemTmpList = GFDefendArmyItemHolder.getInstance().getItemList(groupID);

		for (GFDefendArmyItem item : itemTmpList) {
			if(GFArmyState.NORMAL.equals(item.getState())){
				canFightList.add(item);
			}
			else if(GFArmyState.SELECTED.equals(item.getState()) || GFArmyState.FIGHTING.equals(item.getState())){
				if(System.currentTimeMillis() - item.getLastOperateTime() > LOCK_ITEM_MAX_TIME){
					item.setState(GFArmyState.NORMAL.getValue());					
					canFightList.add(item);
				}
			}
			
		}
		
		if(canFightList.isEmpty()) return null;
		int randomIndex = (int)(Math.random() * canFightList.size());
		
		return canFightList.get(randomIndex);
	}
	
	
	/**
	 * 初始化玩家个人的防守队伍
	 * @param player
	 */
	private void initPersonalDefendArmy(Player player) {
		String groupID = GroupHelper.getUserGroupId(player.getUserId());
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
	
	/**
	 * 添加一组防守队伍
	 * @param player
	 * @param items
	 * @return
	 */
	private boolean addItems(Player player, List<GFDefendArmyItem> itemList){
		String groupId = GroupHelper.getUserGroupId(player.getUserId());
		if(StringUtils.isNotBlank(groupId)){			
			return GFDefendArmyItemHolder.getInstance().addItemList(groupId, itemList);
		}
		return false;
		
		
	}
	
}
