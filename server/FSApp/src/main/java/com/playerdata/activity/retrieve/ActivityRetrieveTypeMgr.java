package com.playerdata.activity.retrieve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.common.HPCUtil;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.retrieve.data.ActivityRetrieveTypeHolder;
import com.playerdata.activity.retrieve.data.RewardBackItem;
import com.playerdata.activity.retrieve.data.RewardBackSubItem;
import com.playerdata.activity.retrieve.data.RewardBackTodaySubItem;
import com.playerdata.activity.retrieve.userFeatures.UserFeatruesMgr;
import com.playerdata.activity.retrieve.userFeatures.UserFeaturesEnum;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.enu.eSpecialItemId;

public class ActivityRetrieveTypeMgr {

	private static final int normal = 0;
	private static final int perfect = 1;

	private static ActivityRetrieveTypeMgr instance = new ActivityRetrieveTypeMgr();

	public static ActivityRetrieveTypeMgr getInstance() {
		return instance;
	}

	public void synCountTypeData(Player player) {
		ActivityRetrieveTypeHolder.getInstance().synAllData(player);
	}

	/**
	 * 类月卡和在线礼包模式，登陆生成，每日更新
	 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkOtherDay(player);
	}

	private void checkNewOpen(Player player) {
		// ActivityRetrieveTypeHolder dataHolder = new ActivityRetrieveTypeHolder();
		// // String userId = player.getUserId();
		// List<RewardBackItem> addItemList = creatItems(userId, dataHolder.getItemStore(userId));
		// if(addItemList != null){
		// dataHolder.addItemList(player, addItemList);
		// }

		// RoleExtPropertyStoreCache<RewardBackItem> storeCache = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_RETRIEVE, RewardBackItem.class);
		// PlayerExtPropertyStore<RewardBackItem> store = null;
		// String userId= player.getUserId();
		// List<RewardBackItem> addList = null;
		// try {
		// store = storeCache.getStore(userId);
		// addList = creatItems(userId, store);
		// if(store != null&&addList != null){
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

	public List<RewardBackItem> creatItems(String userId, boolean isHasPlayer) {
		int id = ActivityRetrieveTypeEnum.retrieve.getId();
		RewardBackItem item = new RewardBackItem();
		Long currentTime = DateUtils.getSecondLevelMillis();
		item.setId(id);
		item.setUserId(userId);
		item.setLastSingleTime(currentTime);
		item.setLastAddPowerTime(currentTime);
		List<RewardBackTodaySubItem> subTodayItemList = UserFeatruesMgr.getInstance().doCreat();
		item.setTodaySubitemList(subTodayItemList);
		List<RewardBackSubItem> subItemList = new ArrayList<RewardBackSubItem>();
		item.setSubList(subItemList);
		List<RewardBackItem> addItemList = new ArrayList<RewardBackItem>();
		addItemList.add(item);
		return addItemList;
	}

	private void checkOtherDay(Player player) {
		ActivityRetrieveTypeHolder dataHolder = new ActivityRetrieveTypeHolder();
		List<RewardBackItem> itemList = dataHolder.getItemList(player.getUserId());
		if (itemList == null) {
			return;
		}
		for (RewardBackItem item : itemList) {
			if (HPCUtil.isResetTime(item.getLastSingleTime())) {
				List<RewardBackSubItem> subItemList = UserFeatruesMgr.getInstance().doFresh(player, item.getTodaySubitemList());
				item.setSubList(subItemList);
				List<RewardBackTodaySubItem> subTodayItemList = UserFeatruesMgr.getInstance().doCreat();
				item.setTodaySubitemList(subTodayItemList);
				item.setLastSingleTime(System.currentTimeMillis());
				dataHolder.updateItem(player, item);
			}
		}
	}

	public ActivityComResult retrieve(Player player, String typeId, int costType) {
		ActivityComResult result = ActivityComResult.newInstance(false);
		if (costType != normal && costType != perfect) {
			result.setReason("无效的找回类型");
			return result;
		}
		UserFeaturesEnum iEnum = UserFeaturesEnum.getById(typeId);
		if (iEnum == null) {
			result.setReason("无效的功能类型");
			return result;
		}
		String userId = player.getUserId();
		ActivityRetrieveTypeHolder dataHolder = ActivityRetrieveTypeHolder.getInstance();

		RewardBackItem item = dataHolder.getItem(userId);
		if (item == null) {
			GameLog.error(LogModule.ComActivityRetrieve, userId, "异常了", null);
			result.setReason("");
			return result;
		}
		List<RewardBackSubItem> subItemList = item.getSubList();
		if (subItemList == null) {
			GameLog.error(LogModule.ComActivityRetrieve, userId, "异常了，list", null);
			result.setReason("");
			return result;
		}
		RewardBackSubItem subItem = null;
		for (RewardBackSubItem tmp : subItemList) {
			if (StringUtils.equals(tmp.getId() + "", typeId)) {
				subItem = tmp;
				break;
			}
		}
		if (subItem == null) {
			result.setReason("没找到昨日的未完成数据");
			return result;
		}
		result = checkEnoughByType(player, costType, subItem);
		if (result.isSuccess()) {
			dataHolder.updateItem(player, item);
		}
		return result;
	}

	private ActivityComResult checkEnoughByType(Player player, int retrieveType, RewardBackSubItem subItem) {
		ActivityComResult result = ActivityComResult.newInstance(false);
		int tmp = 0;
		int type = 0;// 货币类型
		String rewards = null;
		if (retrieveType == normal) {
			tmp = subItem.getNormalCost();
			rewards = subItem.getNormalReward();
			type = subItem.getNormalType();
		} else {
			tmp = subItem.getPerfectCost();
			rewards = subItem.getPerfectReward();
			type = subItem.getPerfectType();
		}
		if (subItem.getCount() == subItem.getMaxCount()) {
			result.setReason("已经找回过了或者昨天该功能未开放");
			return result;
		}
		if (type >= eSpecialItemId.eSpecial_End.getValue()) {
			result.setReason("货币类型不对，策划填错表");
			return result;
		}
		if (player.getReward(eSpecialItemId.getDef(type)) >= tmp) {
			result.setReason("找回成功");
			result.setSuccess(true);
			Map<Integer, Integer> map = new HashMap<Integer, Integer>();
			map.put(type, -tmp);
			ItemBagMgr.getInstance().useLikeBoxItem(player, null, null, map);
			// player.getItemBagMgr().useItemByCfgId(id, entry.getValue());
			String[] reward = rewards.split(";");
			for (String tmpreward : reward) {
				String[] str = tmpreward.split(":");
				ItemBagMgr.getInstance().addItem(player, Integer.parseInt(str[0]), Integer.parseInt(str[1]));
			}
			subItem.setIstaken(true);
			subItem.setCount(subItem.getMaxCount());
		} else {
			result.setReason("没有足够货币");
		}
		return result;
	}

}
