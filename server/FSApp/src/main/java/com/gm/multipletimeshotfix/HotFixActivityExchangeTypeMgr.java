package com.gm.multipletimeshotfix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.exChangeType.ActivityExchangeTypeMgr;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfgDAO;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeDropCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeDropCfgDAO;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeSubCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeSubCfgDAO;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItem;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItemHolder;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeSubItem;
import com.playerdata.activityCommon.UserActivityChecker;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.fashion.FashionItem;

/**
 * @Author HC
 * @date 2017年1月11日 上午6:03:38
 * @desc 
 **/

public class HotFixActivityExchangeTypeMgr extends ActivityExchangeTypeMgr{

	private static final int ACTIVITY_INDEX_BEGIN = 60000;
	private static final int ACTIVITY_INDEX_END = 70000;

	private static ActivityExchangeTypeMgr instance = new ActivityExchangeTypeMgr();
	public static final Random random = new Random();

	public static ActivityExchangeTypeMgr getInstance() {
		return instance;
	}

	public ActivityComResult takeGift(Player player, String cfgId, String subItemId) {
		ActivityExchangeTypeItemHolder dataHolder = ActivityExchangeTypeItemHolder.getInstance();
		ActivityExchangeTypeItem dataItem = dataHolder.getItem(player.getUserId(), cfgId);
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

			// Bug紧急修复，小熊猫
			ActivityExchangeTypeSubCfg subCfg = ActivityExchangeTypeSubCfgDAO.getInstance().getById(targetItem.getCfgId());
			if (subCfg != null) {
				String[] str = subCfg.getAwardGift().split("_");
				if (str[0].startsWith("900004")) {// 如果是小熊猫
					// 查找fashion
					FashionItem item = player.getFashionMgr().getItem(900004);
					if (item != null && item.getExpiredTime() < 0) {
						result.setReason("您已经拥有永久小熊喵，不能兑换");
						return result;
					}
				}
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
	 * 判断兑换次数和兑换材料是否足够
	 * 
	 * @param player
	 * @param targetItem
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
		String[] str = subCfg.getAwardGift().split("_");
		ItemBagMgr.getInstance().addItem(player, Integer.parseInt(str[0]), Integer.parseInt(str[1]));
	}

	/**
	 * 根据传入的玩家和副本，额外获得兑换道具;
	 * 
	 * @param player 玩家等级是否足够
	 * @param copyCfg 战斗场景是否有掉落
	 */
	private Map<Integer, Integer> AddItemOfExchangeActivity(Player player, CopyCfg copyCfg) {
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
			List<ActivityExchangeTypeDropCfg> dropCfgList = activityExchangeTypeCfgDAO.getByParentId(String.valueOf(activityExchangeTypeCfg.getCfgId()));
			if (dropCfgList == null) {
				continue;
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

	private boolean isDropOpen(ActivityExchangeTypeCfg activityExchangeTypeCfg) {
		if (activityExchangeTypeCfg != null) {
			long startTime = activityExchangeTypeCfg.getDropStartTime();
			long endTime = activityExchangeTypeCfg.getDropEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime > startTime;
		}
		return false;
	}

	protected List<String> checkRedPoint(Player player, ActivityExchangeTypeItem item) {
		List<String> redPointList = new ArrayList<String>();
		ActivityExchangeTypeItemHolder exchangeDataHolder = ActivityExchangeTypeItemHolder.getInstance();
		List<ActivityExchangeTypeCfg> exchangeAllCfgList = ActivityExchangeTypeCfgDAO.getInstance().getAllCfg();
		ActivityExchangeTypeMgr exchangeTypeMgr = ActivityExchangeTypeMgr.getInstance();
		for (ActivityExchangeTypeCfg cfg : exchangeAllCfgList) {
			ActivityExchangeTypeItem targetItem = exchangeDataHolder.getItem(player.getUserId(), String.valueOf(cfg.getId()));
			if (targetItem == null) {
				continue;
			}
			if (!targetItem.isTouchRedPoint()) {
				redPointList.add(targetItem.getCfgId());
				continue;
			}
			List<ActivityExchangeTypeSubItem> exchangeSubitemlist = targetItem.getSubItemList();
			for (ActivityExchangeTypeSubItem subitem : exchangeSubitemlist) {
				if (exchangeTypeMgr.isCanTaken(player, subitem)) {
					if (targetItem.getHistoryRedPoint().contains(subitem.getCfgId())) {
						continue;
					}
					redPointList.add(String.valueOf(cfg.getCfgId()));
					break;
				}
			}
		}
		return redPointList;
	}

	@Override
	public boolean isThisActivityIndex(int index) {
		return index < ACTIVITY_INDEX_END && index > ACTIVITY_INDEX_BEGIN;
	}

	@Override
	protected UserActivityChecker<ActivityExchangeTypeItem> getHolder() {
		return ActivityExchangeTypeItemHolder.getInstance();
	}
}
