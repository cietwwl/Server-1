package com.playerdata.groupFightOnline.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.groupFightOnline.bm.GFightConst;
import com.playerdata.groupFightOnline.data.GFDefendArmyItem;
import com.playerdata.groupFightOnline.data.GFDefendArmyItemHolder;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupData;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupHolder;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceData;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceHolder;
import com.playerdata.groupFightOnline.data.UserGFightOnlineData;
import com.playerdata.groupFightOnline.data.UserGFightOnlineHolder;
import com.playerdata.groupFightOnline.dataException.GFArmyDataException;
import com.playerdata.groupFightOnline.dataException.HaveFightEnimyException;
import com.playerdata.groupFightOnline.dataException.HaveSelectEnimyException;
import com.playerdata.groupFightOnline.dataException.NoSuitableDefenderException;
import com.playerdata.groupFightOnline.dataForClient.DefendArmyHerosInfo;
import com.playerdata.groupFightOnline.dataForClient.DefendArmySimpleInfo;
import com.playerdata.groupFightOnline.enums.GFArmyState;
import com.playerdata.groupFightOnline.enums.GFResourceState;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.common.MapItemStoreFactory;

public class GFDefendArmyMgr {

	private static GFDefendArmyMgr instance = new GFDefendArmyMgr();

	public static GFDefendArmyMgr getInstance() {
		return instance;
	}

	public void synGroupDefenderData(Player player, String groupId, int version) {
		GFDefendArmyItemHolder.getInstance().synByVersion(player, groupId, version);
	}

	/**
	 * 查看某公会的某防守队伍
	 * 
	 * @param groupId
	 * @param armyId
	 * @return
	 */
	public GFDefendArmyItem getItem(String groupId, String armyId) {
		return GFDefendArmyItemHolder.getInstance().getItem(groupId, armyId);
	}

	/**
	 * 更新一条自己防守队伍信息（一定是自己的帮派） 主要用在备战阶段
	 * 
	 * @param player
	 * @param armyItem 队伍信息
	 * @param state 最新的状态
	 */
	public void updateItem(Player player, GFDefendArmyItem armyItem, GFArmyState state) {
		String groupId = GroupHelper.getInstance().getUserGroupId(player.getUserId());
		updateItem(groupId, armyItem, state);
	}

	/**
	 * 更新一条防守队伍信息（不一定是自己的帮派） 主要用在开战阶段
	 * 
	 * @param groupId
	 * @param armyItem 队伍信息
	 * @param state 最新的状态
	 */
	public void updateItem(String groupId, GFDefendArmyItem armyItem, GFArmyState state) {
		if (state.equals(GFArmyState.EMPTY)) {
			GFightOnlineGroupMgr.getInstance().addDefenderCount(groupId, -1);
		} else {
			if (state.equals(GFArmyState.NEWADD))
				GFightOnlineGroupMgr.getInstance().addDefenderCount(groupId, 1);
			if (state.equals(GFArmyState.DEFEATED))
				GFightOnlineGroupMgr.getInstance().deductAliveCount(groupId);
		}
		armyItem.setState(state.getValue());
		GFDefendArmyItemHolder.getInstance().updateItem(armyItem);
	}

	/**
	 * 开战前，把所有的防守队伍信息更到最新 开战后，防守队伍属性，不会有更改
	 * 
	 * @param groupId
	 */
	public void updateAllItem(String groupId) {
		List<GFDefendArmyItem> allDefenders = GFDefendArmyItemHolder.getInstance().getItemList(groupId);
		for (GFDefendArmyItem defender : allDefenders) {
			if (defender.getSimpleArmy() == null)
				continue;
			ArmyInfoSimple simpleArmy = ArmyInfoHelper.getSimpleInfo(defender.getUserID(), defender.getSimpleArmy().getArmyMagic().getId(), defender.getSimpleArmy().getHeroIdList());
			defender.setSimpleArmy(simpleArmy);
			GFDefendArmyItemHolder.getInstance().updateItem(defender);
		}
	}

	/**
	 * 重置个人的防守队伍信息
	 * 
	 * @param player
	 * @param items
	 * @throws GFArmyDataException
	 */
	public void resetItems(Player player, List<DefendArmyHerosInfo> items) throws GFArmyDataException {
		HashSet<String> heroIDRepeatCheckSet = new HashSet<String>();
		for (DefendArmyHerosInfo item : items) {
			if (item.getHeroIDs() == null)
				continue;
			for (String id : item.getHeroIDs()) {
				if (StringUtils.isBlank(id))
					continue;
				if (heroIDRepeatCheckSet.contains(id))
					throw new GFArmyDataException("一个英雄不能存在于两个队伍");
				heroIDRepeatCheckSet.add(id);
			}
		}
		for (DefendArmyHerosInfo heros : items) {
			GFDefendArmyItem armyItem = getItem(player, heros.getDefendArmyID());
			if (armyItem == null)
				throw new GFArmyDataException("要修改的队伍无法创建成功");
			if (heros.getHeroIDs() == null || heros.getHeroIDs().size() == 0) {
				if (GFArmyState.NORMAL.equals(armyItem.getState())) {
					armyItem.setSimpleArmy(null);
					updateItem(player, armyItem, GFArmyState.EMPTY);
				} else if (!GFArmyState.EMPTY.equals(armyItem.getState())) {
					throw new GFArmyDataException("非NORMAL和EMPTY状态的队伍，不能置空");
				}
			} else {
				ArmyInfoSimple simpleArmy = ArmyInfoHelper.getSimpleInfo(player.getUserId(), heros.getMagicID(), heros.getHeroIDs());
				if (simpleArmy == null)
					throw new GFArmyDataException("heros无法生成防守队伍信息");
				long operateTime = System.currentTimeMillis();
				armyItem.setSetDefenderTime(operateTime);
				armyItem.setSimpleArmy(simpleArmy);
				if (GFArmyState.EMPTY.equals(armyItem.getState())) {
					updateItem(player, armyItem, GFArmyState.NEWADD);
				} else if (GFArmyState.NORMAL.equals(armyItem.getState())) {
					updateItem(player, armyItem, GFArmyState.NORMAL);
				} else
					throw new GFArmyDataException("非NORMAL和EMPTY状态的队伍，不能更改");
			}
		}
	}

	/**
	 * 清除所有公会的所有防守队伍
	 * 
	 * @param groupIDArr
	 * @return
	 */
	public boolean clearTheRecords(List<String> groupIDArr) {
		MapItemStoreCache<GFDefendArmyItem> cache = MapItemStoreFactory.getGFDefendArmyCache();
		boolean result = true;
		for (String groupID : groupIDArr)
			if (!cache.getMapItemStore(groupID, GFDefendArmyItem.class).clearAllRecords())
				result = false;
		return result;
	}

	/**
	 * 清除所有公会的所有防守队伍
	 * 
	 * @param groupIDArr
	 * @return
	 */
	public boolean clearAllRecords(String groupID) {
		MapItemStoreCache<GFDefendArmyItem> cache = MapItemStoreFactory.getGFDefendArmyCache();
		return cache.getMapItemStore(groupID, GFDefendArmyItem.class).clearAllRecords();
	}

	public void startFight(Player player, GFDefendArmyItem armyItem) {
		// 逻辑判断都已经在函数调用之前做了
		long fightLockTime = System.currentTimeMillis();
		armyItem.setState(GFArmyState.FIGHTING.getValue());
		armyItem.setLastOperateTime(fightLockTime);

		UserGFightOnlineData ugfData = UserGFightOnlineHolder.getInstance().get(player.getUserId());
		ugfData.getRandomDefender().setState(GFArmyState.FIGHTING.getValue());
		ugfData.getRandomDefender().setLockArmyTime(fightLockTime);

		GFDefendArmyItemHolder.getInstance().updateItem(armyItem);
	}

	/**
	 * 选择一个待战对手
	 * 
	 * @param player
	 * @param groupID
	 * @param isIgnoreExistEnimy 是否忽略已经有的对手
	 * @return
	 * @throws HaveSelectEnimyException
	 * @throws NoSuitableDefenderException
	 */
	public boolean selectEnimyItem(Player player, String groupID, boolean isIgnoreExistEnimy) throws HaveSelectEnimyException, NoSuitableDefenderException, HaveFightEnimyException {
		synchronized (GFDefendArmyItem.class) {
			UserGFightOnlineData userGFData = UserGFightOnlineHolder.getInstance().get(player.getUserId());
			DefendArmySimpleInfo randomDefender = userGFData.getRandomDefender();
			if (!isIgnoreExistEnimy && randomDefender != null) {
				if (GFArmyState.SELECTED.equals(randomDefender.getState())) {
					if (System.currentTimeMillis() - randomDefender.getLockArmyTime() <= GFightConst.LOCK_ITEM_MAX_TIME)
						throw new HaveSelectEnimyException("已经选择过对手");
				}
				if (GFArmyState.FIGHTING.equals(randomDefender.getState())) {
					if (System.currentTimeMillis() - randomDefender.getLockArmyTime() <= GFightConst.FIGHT_LOCK_ITEM_MAX_TIME)
						throw new HaveFightEnimyException("请您耐心等待上一场挑战的结果");
				}
			}
			GFDefendArmyItem canFightItem = getCanFightItem(groupID);
			if (canFightItem == null)
				throw new NoSuitableDefenderException("当前没有可以挑战的队伍");
			canFightItem.setState(GFArmyState.SELECTED.getValue());
			canFightItem.setLastOperateTime(System.currentTimeMillis());

			DefendArmySimpleInfo defenderSimple = new DefendArmySimpleInfo();
			defenderSimple.setGroupID(groupID);
			defenderSimple.setDefendArmyID(canFightItem.getArmyID());
			defenderSimple.setLockArmyTime(System.currentTimeMillis());
			defenderSimple.setState(GFArmyState.SELECTED.getValue());
			userGFData.setRandomDefender(defenderSimple);

			UserGFightOnlineHolder.getInstance().update(player, userGFData);
			GFDefendArmyItemHolder.getInstance().updateItem(canFightItem);
			return true;
		}
	}

	/**
	 * 更换一个对手
	 * 
	 * @param player
	 * @param groupID
	 * @param selectedID
	 * @return
	 * @throws NoSuitableDefenderException
	 * @throws HaveSelectEnimyException
	 * @throws HaveFightEnimyException
	 */
	public boolean changeEnimyItem(Player player, String groupID) throws HaveSelectEnimyException, NoSuitableDefenderException, HaveFightEnimyException {
		synchronized (GFDefendArmyItem.class) {
			// 找到之前被选中的
			UserGFightOnlineData userGFData = UserGFightOnlineHolder.getInstance().get(player.getUserId());
			DefendArmySimpleInfo randomDefender = userGFData.getRandomDefender();
			if (!selectEnimyItem(player, groupID, true))
				return false;
			if (randomDefender != null) { // 如果不存在，直接不管
				GFDefendArmyItem selectedItem = getItem(groupID, randomDefender.getDefendArmyID());
				if (selectedItem != null) {
					selectedItem.setState(GFArmyState.NORMAL.getValue());
					GFDefendArmyItemHolder.getInstance().updateItem(selectedItem);
				}
			}
			return true;
		}
	}

	/**
	 * 随机选取一个可以被攻击的防守队伍
	 * 
	 * @param groupID 帮派id
	 * @return
	 */
	private GFDefendArmyItem getCanFightItem(String groupID) {

		List<GFDefendArmyItem> canFightList = new ArrayList<GFDefendArmyItem>();
		List<GFDefendArmyItem> itemTmpList = GFDefendArmyItemHolder.getInstance().getItemList(groupID);

		for (GFDefendArmyItem item : itemTmpList) {
			if (GFArmyState.DEFEATED.equals(item.getState()))
				continue;

			if (GFArmyState.NORMAL.equals(item.getState())) {
				canFightList.add(item);
			} else if (GFArmyState.SELECTED.equals(item.getState())) {
				if (System.currentTimeMillis() - item.getLastOperateTime() > GFightConst.LOCK_ITEM_MAX_TIME) {
					item.setState(GFArmyState.NORMAL.getValue());
					canFightList.add(item);
				}
			} else if (GFArmyState.FIGHTING.equals(item.getState())) {
				if (System.currentTimeMillis() - item.getLastOperateTime() > GFightConst.FIGHT_LOCK_ITEM_MAX_TIME) {
					item.setState(GFArmyState.NORMAL.getValue());
					canFightList.add(item);
				}
			}
		}

		if (canFightList.isEmpty())
			return null;
		int randomIndex = (int) (Math.random() * canFightList.size());

		return canFightList.get(randomIndex);
	}

	/**
	 * 获取个人的某一个队伍
	 * 
	 * @param userId
	 * @param armyId
	 * @return
	 */
	private GFDefendArmyItem getItem(Player player, String armyId) {
		List<GFDefendArmyItem> selfItems = GFDefendArmyItemHolder.getInstance().getUserDefArmyList(player);
		for (GFDefendArmyItem item : selfItems) {
			if (StringUtils.equals(item.getArmyID(), armyId))
				return item;
		}
		return null;
	}

	/**
	 * 有英雄升级或者升星 更新角色的防守队伍信息 （只有备战阶段才更新）
	 * 
	 * @param player
	 */
	public void defenderChanged(Player player) {
		String groupID = GroupHelper.getInstance().getUserGroupId(player.getUserId());
		if (StringUtils.isBlank(groupID))
			return;
		GFightOnlineGroupData gfgData = GFightOnlineGroupHolder.getInstance().get(groupID);
		if (gfgData == null || gfgData.getResourceID() <= 0)
			return;
		GFightOnlineResourceData resData = GFightOnlineResourceHolder.getInstance().get(gfgData.getResourceID());
		if (resData == null || !GFResourceState.PREPARE.equals(resData.getState()))
			return;
		List<GFDefendArmyItem> defenders = GFDefendArmyItemHolder.getInstance().getUserDefArmyList(player);
		try {
			resetItems(player, defenderToHeroListInfo(defenders));
		} catch (GFArmyDataException e) {
			GameLog.error(LogModule.GroupFightOnline.getName(), player.getUserId(), String.format("heroChanged，更新个人防守队伍信息时，数据异常"), e);
		}
	}

	/**
	 * 提取英雄的id数据
	 * 
	 * @param defenders
	 * @return
	 */
	private List<DefendArmyHerosInfo> defenderToHeroListInfo(List<GFDefendArmyItem> defenders) {
		List<DefendArmyHerosInfo> newItems = new ArrayList<DefendArmyHerosInfo>();
		for (GFDefendArmyItem defender : defenders) {
			DefendArmyHerosInfo heros = new DefendArmyHerosInfo();
			if (defender.getSimpleArmy() == null)
				continue;
			heros.setDefendArmyID(defender.getArmyID());
			heros.setHeroIDs(defender.getSimpleArmy().getHeroIdList());
			heros.setMagicID(defender.getSimpleArmy().getArmyMagic().getId());
			newItems.add(heros);
		}
		return newItems;
	}
}
