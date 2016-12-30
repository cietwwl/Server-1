package com.playerdata.activity.dailyDiscountType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountItemCfg;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountItemCfgDao;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountTypeCfg;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItem;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItemHolder;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeSubItem;
import com.playerdata.activityCommon.AbstractActivityMgr;
import com.playerdata.activityCommon.UserActivityChecker;
import com.rwbase.common.enu.eSpecialItemId;

public class ActivityDailyDiscountTypeMgr extends AbstractActivityMgr<ActivityDailyDiscountTypeItem> {
	
	private static final int ACTIVITY_INDEX_BEGIN = 80000;
	private static final int ACTIVITY_INDEX_END = 90000;

	private static ActivityDailyDiscountTypeMgr instance = new ActivityDailyDiscountTypeMgr();

	public static ActivityDailyDiscountTypeMgr getInstance() {
		return instance;
	}

	public ActivityComResult buyItem(Player player, ActivityDailyDiscountTypeCfg cfg, String subItemId) {
		ActivityDailyDiscountTypeItemHolder dataHolder = ActivityDailyDiscountTypeItemHolder.getInstance();
		ActivityDailyDiscountTypeItem dataItem = dataHolder.getItem(player.getUserId(), String.valueOf(cfg.getCfgId()));
		ActivityComResult result = ActivityComResult.newInstance(false);
		// 未激活
		if (dataItem == null) {
			result.setReason("活动尚未开启");
			result.setSuccess(false);
		} else {
			ActivityDailyDiscountTypeSubItem targetItem = null;
			List<ActivityDailyDiscountTypeSubItem> subItemList = dataItem.getSubItemList();
			ActivityDailyDiscountItemCfg itemCfg = null;
			List<ActivityDailyDiscountItemCfg> itemCfgList = ActivityDailyDiscountItemCfgDao.getInstance().getAllCfg();
			for (ActivityDailyDiscountTypeSubItem itemTmp : subItemList) {
				if (StringUtils.equals(itemTmp.getCfgId(), subItemId)) {
					targetItem = itemTmp;
					break;
				}
			}
			if (targetItem == null) {
				result.setReason("数据异常，请重登陆");
				result.setSuccess(false);
				return result;
			}
			for (ActivityDailyDiscountItemCfg itemCfgTmp : itemCfgList) {
				if (StringUtils.equals(itemCfgTmp.getId(), subItemId)) {
					itemCfg = itemCfgTmp;
					break;
				}
			}
			if (itemCfg == null) {
				result.setReason("异常，请联系游戏官方");
				result.setSuccess(false);
				return result;
			}
			if (!isLevelEnough(player, cfg)) {
				result.setReason("您的等级不足哦");
				result.setSuccess(false);
				return result;
			}
			if (!isVipEnough(player, itemCfg)) {
				result.setReason("您的贵族等级不足哦");
				result.setSuccess(false);
				return result;
			}
			if (!isCountEnough(targetItem.getCount(), itemCfg)) {
				result.setReason("次数不足");
				result.setSuccess(false);
				return result;
			}
			if (!isGoldEnough(player, itemCfg)) {
				result.setReason("您的钻石不足哦");
				result.setSuccess(false);
				return result;
			} else {
				Map<Integer, Integer> map = new HashMap<Integer, Integer>();
				map.put(eSpecialItemId.Gold.getValue(), -itemCfg.getPriceAfterDiscount());
				ItemBagMgr.getInstance().useLikeBoxItem(player, null, null, map);
			}
			targetItem.setCount(targetItem.getCount() + 1);
			dataHolder.updateItem(player, dataItem);
			result.setReason("购买成功");
			result.setSuccess(true);

		}
		return result;
	}

	/**
	 * 购买次数是否充足
	 * @param count
	 * @param cfg
	 * @return
	 */
	private boolean isCountEnough(int count, ActivityDailyDiscountItemCfg cfg) {
		return count < cfg.getCountLimit();
	}

	/**
	 * 钻石是否充足
	 * @param player
	 * @param itemCfg
	 * @return
	 */
	private boolean isGoldEnough(Player player, ActivityDailyDiscountItemCfg itemCfg) {
		return player.getUserGameDataMgr().isEnoughCurrency(eSpecialItemId.Gold, itemCfg.getPriceAfterDiscount());
	}
	
	/**
	 * vip等级是否达到
	 * @param player
	 * @param itemCfg
	 * @return
	 */
	private boolean isVipEnough(Player player, ActivityDailyDiscountItemCfg itemCfg){
		return player.getVip() >= itemCfg.getVipLimit();
	}
	
	@Override
	protected List<String> checkRedPoint(Player player, ActivityDailyDiscountTypeItem item) {
		List<String> redPointList = new ArrayList<String>();
		if (!item.isHasViewed()) {
			redPointList.add(String.valueOf(item.getCfgId()));
		}
		return redPointList;
	}

	protected UserActivityChecker<ActivityDailyDiscountTypeItem> getHolder(){
		return ActivityDailyDiscountTypeItemHolder.getInstance();
	}
	
	@Override
	public boolean isThisActivityIndex(int index){
		return index < ACTIVITY_INDEX_END && index > ACTIVITY_INDEX_BEGIN;
	}	
}
