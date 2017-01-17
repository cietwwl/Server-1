package com.playerdata.activity.exChangeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfgDAO;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeDropCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeDropCfgDAO;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeSubCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeSubCfgDAO;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItem;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItemHolder;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeSubItem;
import com.playerdata.activityCommon.activityType.IndexRankJudgeIF;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.pojo.ItemInfo;

public class ActivityExchangeTypeMgr implements ActivityRedPointUpdate, IndexRankJudgeIF{

	private static final int ACTIVITY_INDEX_BEGIN = 60000;
	private static final int ACTIVITY_INDEX_END = 70000;
	
	private static ActivityExchangeTypeMgr instance = new ActivityExchangeTypeMgr();
	public static final Random random = new Random();

	// private Map<Integer, Integer> idAndNumMap = new HashMap<Integer, Integer>();

	public static ActivityExchangeTypeMgr getInstance() {
		return instance;
	}

	public void synCountTypeData(Player player) {
		if (isOpen(System.currentTimeMillis())) {
			ActivityExchangeTypeItemHolder.getInstance().synAllData(player);
		}
	}

	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkCfgVersion(player);
		checkOtherDay(player);
		checkClose(player);
	}

	private void checkNewOpen(Player player) {
		String userId = player.getUserId();
		// List<ActivityExchangeTypeItem> addList = null;
		// RoleExtPropertyStoreCache<ActivityExchangeTypeItem> storeCach = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_EXCHANGE, ActivityExchangeTypeItem.class);
		// PlayerExtPropertyStore<ActivityExchangeTypeItem> store = null;
		// try {
		// store = storeCach.getStore(userId);
		createItems(userId, true);
		// if(addList != null){
		// store.addItem(addList);
		// }
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (Throwable e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	/**
	 * 返回需要添加到MapItemStore的对象(可能是活动开启、创建角色、或者配置发生变化)
	 * 
	 * @param userId
	 * @param allCfgList
	 * @param mapItemStore
	 * @return
	 */
	public List<ActivityExchangeTypeItem> createItems(String userId, boolean isHasPlayer) {
		RoleExtPropertyStoreCache<ActivityExchangeTypeItem> storeCach = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_EXCHANGE, ActivityExchangeTypeItem.class);
		RoleExtPropertyStore<ActivityExchangeTypeItem> store = null;
		List<ActivityExchangeTypeCfg> allCfgList = ActivityExchangeTypeCfgDAO.getInstance().getAllCfg();
		ArrayList<ActivityExchangeTypeItem> addItemList = null;
		ActivityExchangeTypeSubCfgDAO dao = ActivityExchangeTypeSubCfgDAO.getInstance();
		for (ActivityExchangeTypeCfg cfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (!isOpen(cfg)) {
				// 活动未开启
				continue;
			}
			ActivityExChangeTypeEnum activityExChangeTypeEnum = ActivityExChangeTypeEnum.getById(cfg.getEnumId());
			if (activityExChangeTypeEnum == null) {
				continue;
			}
			// String itemID = ActivityExChangeTypeHelper.getItemId(userId, activityExChangeTypeEnum);
			int id = Integer.parseInt(activityExChangeTypeEnum.getCfgId());
			if (isHasPlayer) {
				try {
					store = storeCach.getStore(userId);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (store != null) {
					if (store.get(id) != null) {
						continue;
					}
				}
			}
			ActivityExchangeTypeItem item = new ActivityExchangeTypeItem();
			item.setId(id);
			item.setEnumId(cfg.getEnumId());
			item.setCfgId(String.valueOf(cfg.getId()));
			item.setUserId(userId);
			item.setVersion(String.valueOf(cfg.getVersion()));
			item.setLasttime(System.currentTimeMillis());
			List<ActivityExchangeTypeSubItem> subItemList = new ArrayList<ActivityExchangeTypeSubItem>();
			List<ActivityExchangeTypeSubCfg> subItemCfgList = dao.getByParentCfgId(String.valueOf(cfg.getId()));
			if (subItemCfgList == null) {
				subItemCfgList = new ArrayList<ActivityExchangeTypeSubCfg>();
			}
			for (ActivityExchangeTypeSubCfg activityExchangeTypeSubCfg : subItemCfgList) {
				ActivityExchangeTypeSubItem subItem = new ActivityExchangeTypeSubItem();
				subItem.setCfgId(activityExchangeTypeSubCfg.getId());
				subItem.setTime(0);
				subItem.setIsrefresh(activityExchangeTypeSubCfg.isIsrefresh());
				subItemList.add(subItem);
			}
			item.setSubItemList(subItemList);
			if (addItemList == null) {
				addItemList = new ArrayList<ActivityExchangeTypeItem>();
			}
			addItemList.add(item);
		}

		if (isHasPlayer && addItemList != null) {
			try {
				store.addItem(addItemList);
			} catch (DuplicatedKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return addItemList;
	}

	public boolean isOpen(ActivityExchangeTypeCfg activityExchangeTypeCfg) {
		if (activityExchangeTypeCfg != null) {
			long startTime = activityExchangeTypeCfg.getChangeStartTime();
			long endTime = activityExchangeTypeCfg.getChangeEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}

	private void checkCfgVersion(Player player) {
		ActivityExchangeTypeItemHolder dataHolder = ActivityExchangeTypeItemHolder.getInstance();
		ActivityExchangeTypeCfgDAO dao = ActivityExchangeTypeCfgDAO.getInstance();
		List<ActivityExchangeTypeItem> itemList = null;// dataHolder.getItemList(player.getUserId());
		List<ActivityExchangeTypeCfg> cfgList = dao.getAllCfg();
		for (ActivityExchangeTypeCfg cfg : cfgList) {
			if (!isOpen(cfg)) {
				continue;
			}
			if (itemList == null) {
				itemList = dataHolder.getItemList(player.getUserId());
			}
			ActivityExchangeTypeItem freshItem = null;
			for (ActivityExchangeTypeItem item : itemList) {
				if (StringUtils.equals(item.getEnumId(), cfg.getEnumId()) && !StringUtils.equals(item.getVersion(), String.valueOf(cfg.getVersion()))) {
					freshItem = item;
				}
			}
			if (freshItem == null) {
				continue;
			}
			freshItem.reset(cfg, dao.newItemList(cfg));
			dataHolder.updateItem(player, freshItem);
		}
	}

	private void checkOtherDay(Player player) {
		ActivityExchangeTypeItemHolder dataHolder = ActivityExchangeTypeItemHolder.getInstance();
		ActivityExchangeTypeCfgDAO dao = ActivityExchangeTypeCfgDAO.getInstance();
		List<ActivityExchangeTypeItem> itemList = null;// dataHolder.getItemList(player.getUserId());
		List<ActivityExchangeTypeCfg> cfgList = dao.getAllCfg();
		long currentTime = DateUtils.getSecondLevelMillis();
		for (ActivityExchangeTypeCfg cfg : cfgList) {
			if (!isOpen(cfg)) {
				continue;
			}
			if (itemList == null) {
				itemList = dataHolder.getItemList(player.getUserId());
			}
			ActivityExchangeTypeItem freshItem = null;
			for (ActivityExchangeTypeItem item : itemList) {
				if (StringUtils.equals(item.getVersion(), String.valueOf(cfg.getVersion())) && StringUtils.equals(item.getEnumId(), cfg.getEnumId())) {
					freshItem = item;
				}
			}
			if (freshItem == null) {
				continue;
			}
			if (ActivityTypeHelper.isNewDayHourOfActivity(5, freshItem.getLasttime())) {
				freshItem.setLasttime(currentTime);
				List<ActivityExchangeTypeSubItem> subitemlist = freshItem.getSubItemList();
				for (ActivityExchangeTypeSubItem subitem : subitemlist) {
					if (subitem.isIsrefresh()) {
						subitem.setTime(0);
					}
				}
				dataHolder.updateItem(player, freshItem);
			}
		}
	}

	private void checkClose(Player player) {
		ActivityExchangeTypeItemHolder dataHolder = ActivityExchangeTypeItemHolder.getInstance();
		ActivityExchangeTypeCfgDAO dao = ActivityExchangeTypeCfgDAO.getInstance();
		List<ActivityExchangeTypeItem> itemList = null;// dataHolder.getItemList(player.getUserId());
		List<ActivityExchangeTypeCfg> cfgList = dao.getAllCfg();
		long createTime = player.getUserDataMgr().getCreateTime();
		long currentTime = DateUtils.getSecondLevelMillis();
		for (ActivityExchangeTypeCfg cfg : cfgList) {
			if (isOpen(cfg)) {// 配置开启
				continue;
			}
			if (createTime > cfg.getChangeEndTime()) {// 配置过旧
				continue;
			}
			if (currentTime < cfg.getChangeStartTime()) {// 配置过新
				continue;
			}
			if (itemList == null) {
				itemList = dataHolder.getItemList(player.getUserId());
			}
			ActivityExchangeTypeItem closeItem = null;
			for (ActivityExchangeTypeItem item : itemList) {
				if (StringUtils.equals(item.getVersion(), String.valueOf(cfg.getVersion()))) {
					closeItem = item;
					break;
				}
			}
			if (closeItem == null) {
				continue;
			}
			if (!closeItem.isClosed()) {
				closeItem.setClosed(true);
				closeItem.setTouchRedPoint(true);
				dataHolder.updateItem(player, closeItem);
			}
		}

		// for (ActivityExchangeTypeItem item : itemList) {
		// if (item.isClosed()) {
		// continue;
		// }
		// ActivityExchangeTypeCfg cfg = dao.getCfgById(item.getCfgId());
		// if (cfg == null) {
		// continue;
		// }
		// if (isOpen(cfg)) {
		// continue;
		// }
		// item.setClosed(true);
		// item.setTouchRedPoint(true);
		// dataHolder.updateItem(player, item);
		// }
	}

	public boolean isLevelEnough(Player player, ActivityExchangeTypeCfg cfg) {
		boolean iscan = false;
		iscan = player.getLevel() >= cfg.getLevelLimit() ? true : false;
		return iscan;

	}

	public ActivityComResult takeGift(Player player, ActivityExChangeTypeEnum countType, String subItemId) {
		ActivityExchangeTypeItemHolder dataHolder = ActivityExchangeTypeItemHolder.getInstance();

		ActivityExchangeTypeItem dataItem = dataHolder.getItem(player.getUserId(), countType);
		ActivityComResult result = ActivityComResult.newInstance(false);

		// 未激活
		if (dataItem == null) {
			result.setReason("活动尚未开启");

		} else {
			ActivityExchangeTypeSubItem targetItem = null;

			List<ActivityExchangeTypeSubItem> subItemList = dataItem.getSubItemList();
			for (ActivityExchangeTypeSubItem itemTmp : subItemList) {
				if (StringUtils.equals(itemTmp.getCfgId(), subItemId)) {
					targetItem = itemTmp;
					break;
				}
			}

			if (targetItem == null) {
				result.setReason("找不到子活动类型的数据");
				return result;
			}

			if (isCanTaken(player, targetItem)) {
				spendCost(player, targetItem);
				takeGift(player, targetItem);
				result.setSuccess(true);
				dataHolder.updateItem(player, dataItem);
			} else {
				result.setSuccess(false);
				result.setReason("不满足兑换条件");
			}

		}

		return result;
	}

	/**
	 * 
	 * @param player
	 * @param targetItem
	 * @param isspend
	 * @return
	 */
	public boolean isCanTaken(Player player, ActivityExchangeTypeSubItem targetItem) {
		ActivityExchangeTypeSubCfg activityExchangeTypeSubCfg = ActivityExchangeTypeSubCfgDAO.getInstance().getById(targetItem.getCfgId());
		if (activityExchangeTypeSubCfg == null) {
			return false;
		}
		if (targetItem.getTime() >= activityExchangeTypeSubCfg.getTime()) {
			// 没有了兑换次数
			return false;
		}
		// 临时方案，需要在启动服务器时生成配置相关对象，后续由小飞优化
		HashMap<Integer, Integer> itemCostMap = activityExchangeTypeSubCfg.getChangelist();
		HashMap<Integer, Integer> eSpecialItemCostMap = activityExchangeTypeSubCfg.geteSpecialItemChangeList();
		for (Map.Entry<Integer, Integer> entry : eSpecialItemCostMap.entrySet()) {
			if (player.getReward(eSpecialItemId.getDef(entry.getKey())) < entry.getValue()) {
				return false;
			}
		}
		if (itemCostMap.isEmpty()) {
			return true;
		}

		if (ItemBagMgr.getInstance().hasEnoughItems(player.getUserId(), itemCostMap)) {
			return true;
		}
		return false;
	}

	private void spendCost(Player player, ActivityExchangeTypeSubItem targetItem) {
		ActivityExchangeTypeSubCfg activityExchangeTypeSubCfg = ActivityExchangeTypeSubCfgDAO.getInstance().getById(targetItem.getCfgId());
		if (activityExchangeTypeSubCfg == null) {
			return;
		}
		if (targetItem.getTime() >= activityExchangeTypeSubCfg.getTime()) {
			// 没有了兑换次数
			return;
		}
		HashMap<Integer, Integer> itemCostMap = activityExchangeTypeSubCfg.getChangelist();
		HashMap<Integer, Integer> eSpecialItemCostMap = activityExchangeTypeSubCfg.geteSpecialItemChangeList();
		spendItem(itemCostMap, player);
		spendItem(eSpecialItemCostMap, player);
	}

	private void spendItem(Map<Integer, Integer> exchangeNeedslist, Player player) {
		if (exchangeNeedslist == null) {
			return;
		}
		ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();
		for (Map.Entry<Integer, Integer> entry : exchangeNeedslist.entrySet()) {
			int id = entry.getKey();
			if (id < eSpecialItemId.eSpecial_End.getValue()) {
				Map<Integer, Integer> map = new HashMap<Integer, Integer>();
				map.put(entry.getKey(), -entry.getValue());
				itemBagMgr.useLikeBoxItem(player, null, null, map);
			} else {
				itemBagMgr.useItemByCfgId(player, id, entry.getValue());
			}
		}
	}

	private void takeGift(Player player, ActivityExchangeTypeSubItem targetItem) {
		ActivityExchangeTypeSubCfg subCfg = ActivityExchangeTypeSubCfgDAO.getInstance().getById(targetItem.getCfgId());
		if (subCfg == null) {
			return;
		}
		targetItem.setTime(targetItem.getTime() + 1);
		// ComGiftMgr.getInstance().addGiftById(player, subCfg.getAwardGift());

		String[] str = subCfg.getAwardGift().split("_");
		ItemBagMgr.getInstance().addItem(player, Integer.parseInt(str[0]), Integer.parseInt(str[1]));
	}

	/**
	 * 根据传入的玩家和副本，额外获得兑换道具;
	 * 
	 * @param player 玩家等级是否足够
	 * @param copyCfg 战斗场景是否有掉落
	 */
	public Map<Integer, Integer> AddItemOfExchangeActivity(Player player, CopyCfg copyCfg) {
		Map<Integer, Integer> idAndNumMap = new HashMap<Integer, Integer>();
		List<ActivityExchangeTypeCfg> allCfgList = ActivityExchangeTypeCfgDAO.getInstance().getAllCfg();
		ActivityExchangeTypeDropCfgDAO activityExchangeTypeCfgDAO = ActivityExchangeTypeDropCfgDAO.getInstance();
		for (ActivityExchangeTypeCfg activityExchangeTypeCfg : allCfgList) {// 遍历所有的活动
			if (!isDropOpen(activityExchangeTypeCfg)) {
				// 活动未开启
				continue;
			}
			if (player.getLevel() < activityExchangeTypeCfg.getLevelLimit()) {
				// 等级不足
				continue;
			}
			List<ActivityExchangeTypeDropCfg> dropCfgList = activityExchangeTypeCfgDAO.getByParentId(String.valueOf(activityExchangeTypeCfg.getId()));
			if (dropCfgList == null) {
				return idAndNumMap;
			}
			for (ActivityExchangeTypeDropCfg cfg : dropCfgList) {// 遍历单个活动可能对应的所有掉落道具类型
				Map<Integer, Integer[]> map = cfg.getDropMap();

				if (map.get(copyCfg.getLevelType()) != null) {// 该掉落配置表的该条记录适合此类战斗场景
					Integer[] numAndProbability = map.get(copyCfg.getLevelType());

					if (numAndProbability.length != 2) {
						idAndNumMap = null;
						return idAndNumMap;
					}
					if (random.nextInt(10000) <= numAndProbability[1]) {
						// player.getItemBagMgr().addItem(Integer.parseInt(cfg.getItemId()),
						// numAndProbability[0]);
						idAndNumMap.put(Integer.parseInt(cfg.getItemId()), numAndProbability[0]);
					}
				}
			}
		}
		return idAndNumMap;
	}

	/**
	 * 根据传入的玩家和副本，额外获得兑换道具，当前适用副本预掉落
	 * 
	 * @param player 玩家等级是否足够
	 * @param copyCfg 战斗场景是否有掉落
	 */
	public void AddItemOfExchangeActivityBefore(Player player, CopyCfg copyCfg, List<ItemInfo> itemInfoList) {
		Map<Integer, Integer> idAndNumMap = AddItemOfExchangeActivity(player, copyCfg);
		if (idAndNumMap == null) {
			// 没声场额外掉落
			return;
		}
		for (Map.Entry<Integer, Integer> entry : idAndNumMap.entrySet()) {
			ItemInfo itemInfo = new ItemInfo();
			itemInfo.setItemID(entry.getKey());
			itemInfo.setItemNum(entry.getValue());
			itemInfoList.add(itemInfo);
		}
	}

	public boolean isDropOpen(ActivityExchangeTypeCfg activityExchangeTypeCfg) {
		if (activityExchangeTypeCfg != null) {
			// if(player.getLevel() < activityCountTypeCfg.getLevelLimit()){
			// return false;
			// }
			long startTime = activityExchangeTypeCfg.getDropStartTime();
			long endTime = activityExchangeTypeCfg.getDropEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime > startTime;
		}
		return false;
	}

	@Override
	public void updateRedPoint(Player player, String eNum) {
		ActivityExchangeTypeItemHolder activityCountTypeItemHolder = new ActivityExchangeTypeItemHolder();
		ActivityExchangeTypeCfg cfg = ActivityExchangeTypeCfgDAO.getInstance().getCfgById(eNum);
		if (cfg == null) {
			return;
		}
		ActivityExChangeTypeEnum exchangeEnum = ActivityExChangeTypeEnum.getById(cfg.getEnumId());
		if (exchangeEnum == null) {
			return;
		}
		ActivityExchangeTypeItem dataItem = activityCountTypeItemHolder.getItem(player.getUserId(), exchangeEnum);
		if (dataItem == null) {
			return;
		}
		if (!dataItem.isTouchRedPoint()) {
			dataItem.setTouchRedPoint(true);
		}

		List<ActivityExchangeTypeSubItem> exchangeSubitemlist = dataItem.getSubItemList();
		for (ActivityExchangeTypeSubItem subitem : exchangeSubitemlist) {
			if (isCanTaken(player, subitem)) {
				if (dataItem.getHistoryRedPoint().contains(subitem.getCfgId())) {
					continue;
				}
				dataItem.getHistoryRedPoint().add(subitem.getCfgId());
			}
		}
		activityCountTypeItemHolder.updateItem(player, dataItem);
	}

	public boolean isOpen(long param) {
		List<ActivityExchangeTypeCfg> allList = ActivityExchangeTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityExchangeTypeCfg cfg : allList) {
			if (isOpen(cfg, param)) {
				return true;
			}
		}
		return false;
	}

	private boolean isOpen(ActivityExchangeTypeCfg cfg, long param) {
		if (cfg != null) {
			long startTime = cfg.getChangeStartTime();
			long endTime = cfg.getChangeEndTime();
			long currentTime = param;
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}

	@Override
	public boolean isThisActivityIndex(int index){
		return index < ACTIVITY_INDEX_END && index > ACTIVITY_INDEX_BEGIN;
	}
}
