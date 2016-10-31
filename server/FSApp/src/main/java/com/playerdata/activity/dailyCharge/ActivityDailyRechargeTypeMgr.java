package com.playerdata.activity.dailyCharge;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeCfg;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeCfgDAO;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeSubCfg;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeSubCfgDAO;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItem;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItemHolder;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeSubItem;
import com.playerdata.activityCommon.AbstractActivityMgr;
import com.playerdata.activityCommon.UserActivityChecker;

public class ActivityDailyRechargeTypeMgr extends AbstractActivityMgr<ActivityDailyRechargeTypeItem> {

	private static ActivityDailyRechargeTypeMgr instance = new ActivityDailyRechargeTypeMgr();

	public static ActivityDailyRechargeTypeMgr getInstance() {
		return instance;
	}

	/**
	 * 添加完成的进度
	 * 
	 * @param player
	 * @param count
	 */
	public void addFinishCount(Player player, int count) {
		List<ActivityDailyRechargeTypeItem> items = ActivityDailyRechargeTypeItemHolder.getInstance().getItemList(player.getUserId());
		if (null == items || items.isEmpty())
			return;
		for (ActivityDailyRechargeTypeItem item : items) {
			item.setFinishCount(item.getFinishCount() + count);
			ActivityDailyRechargeTypeItemHolder.getInstance().updateItem(player, item);
		}
		ActivityDailyRechargeTypeItemHolder.getInstance().synAllData(player);
	}
	
	/**
	 * 领取充值奖励
	 * 
	 * @param player
	 * @param activityID 活动主id
	 * @param subItemId 活动子id
	 * @return
	 */
	public ActivityComResult takeGift(Player player, String activityID, String subItemId) {
		ActivityDailyRechargeTypeItemHolder dataHolder = ActivityDailyRechargeTypeItemHolder.getInstance();
		ActivityDailyRechargeTypeItem dataItem = dataHolder.getItem(player.getUserId(), activityID);
		ActivityComResult result = ActivityComResult.newInstance(false);
		// 未激活
		if (dataItem == null) {
			result.setReason("活动尚未开启");
		} else {
			ActivityDailyRechargeTypeSubItem targetItem = null;
			List<ActivityDailyRechargeTypeSubItem> subItemList = (List<ActivityDailyRechargeTypeSubItem>) dataItem.getSubItemList();
			for (ActivityDailyRechargeTypeSubItem itemTmp : subItemList) {
				if (StringUtils.equals(itemTmp.getCfgId(), subItemId)) {
					targetItem = itemTmp;
					break;
				}
			}
			if (null == targetItem) {
				result.setReason("找不到子活动类型的数据");
				return result;
			}
			if (targetItem.isGet()) {
				result.setReason("已领取过该奖励");
				return result;
			}
			ActivityDailyChargeSubCfg subCfg = ActivityDailyChargeSubCfgDAO.getInstance().getCfgById(targetItem.getCfgId());
			if (null == subCfg) {
				result.setReason("找不到子活动类型的配置数据");
				return result;
			}
			ActivityDailyChargeCfg cfg = ActivityDailyChargeCfgDAO.getInstance().getCfgById(activityID);
			// 判断领取条件
			if (isLevelEnough(player, cfg) && dataItem.getFinishCount() >= subCfg.getCount()) {
				if (takeGift(player, targetItem)) {
					result.setSuccess(true);
					dataHolder.updateItem(player, dataItem);
				} else {
					result.setSuccess(false);
					result.setReason("数据异常");
				}
			} else {
				result.setSuccess(false);
				result.setReason("不满足兑换条件");
			}
		}
		return result;
	}

	private boolean takeGift(Player player, ActivityDailyRechargeTypeSubItem targetItem) {
		ActivityDailyChargeSubCfg subCfg = ActivityDailyChargeSubCfgDAO.getInstance().getCfgById(targetItem.getCfgId());
		if (subCfg == null) {
			GameLog.error(LogModule.ComActivityDailyRecharge, null, "通用活动找不到奖励配置文件", null);
			return false;
		}
		targetItem.setGet(true);
		ComGiftMgr.getInstance().addGiftById(player, subCfg.getGiftId());
		return true;
	}
	
	/**
	 * 邮件补发过期未领取的奖励
	 * 
	 * @param player
	 * @param item
	 */
	@Override
	public void expireActivityHandler(Player player, ActivityDailyRechargeTypeItem item) {
		List<ActivityDailyRechargeTypeSubItem> subItems = (List<ActivityDailyRechargeTypeSubItem>) item.getSubItemList();
		ActivityDailyChargeCfg cfg = ActivityDailyChargeCfgDAO.getInstance().getCfgById(item.getCfgId());
		if (isLevelEnough(player, cfg)) {
			ActivityDailyChargeSubCfgDAO chargeSubCfgDAO = ActivityDailyChargeSubCfgDAO.getInstance();
			ComGiftMgr giftMgr = ComGiftMgr.getInstance();
			for (ActivityDailyRechargeTypeSubItem subItem : subItems) {
				ActivityDailyChargeSubCfg subCfg = chargeSubCfgDAO.getCfgById(subItem.getCfgId());
				if (null == subCfg) {
					continue;
				}
				if (!subItem.isGet() && item.getFinishCount() >= subCfg.getCount()) {
					giftMgr.addGiftTOEmailById(player, subCfg.getGiftId(), null, cfg.getTitle());
				}
			}
		}
		item.reset();
	}

	@Override
	protected List<String> checkRedPoint(Player player, ActivityDailyRechargeTypeItem item) {
		List<String> redPointList = new ArrayList<String>();
		ActivityDailyChargeSubCfgDAO subCfgDao = ActivityDailyChargeSubCfgDAO.getInstance();
		List<ActivityDailyRechargeTypeSubItem> subItems = (List<ActivityDailyRechargeTypeSubItem>) item.getSubItemList();
		for (ActivityDailyRechargeTypeSubItem subItem : subItems) {
			ActivityDailyChargeSubCfg subCfg = subCfgDao.getCfgById(subItem.getCfgId());
			if (null == subCfg)
				continue;
			if ((subCfg.getCount() <= item.getFinishCount() && !subItem.isGet()) || !item.isHasViewed()) {
				redPointList.add(String.valueOf(item.getCfgId()));
				break;
			}
		}
		return redPointList;
	}
	
	protected UserActivityChecker<ActivityDailyRechargeTypeItem> getHolder(){
		return ActivityDailyRechargeTypeItemHolder.getInstance();
	}
	
	protected boolean isThisActivityIndex(int index){
		return index < 120000 && index > 110000;
	}
}
