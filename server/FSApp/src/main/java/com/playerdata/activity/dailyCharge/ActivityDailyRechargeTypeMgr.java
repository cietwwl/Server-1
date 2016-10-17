package com.playerdata.activity.dailyCharge;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeCfg;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeCfgDAO;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeSubCfg;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeSubCfgDAO;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItem;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItemHolder;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeSubItem;

public class ActivityDailyRechargeTypeMgr implements ActivityRedPointUpdate {

	private static ActivityDailyRechargeTypeMgr instance = new ActivityDailyRechargeTypeMgr();

	public static ActivityDailyRechargeTypeMgr getInstance() {
		return instance;
	}

	public void synData(Player player) {
		if(ActivityDetector.getInstance().hasDailyCharge()){
			ActivityDailyRechargeTypeItemHolder.getInstance().synAllData(player);
		}
		
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

	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时清空 */
	public void checkActivityOpen(Player player) {
		ActivityDailyRechargeTypeItemHolder.getInstance().refreshDailyRecharge(player.getUserId());
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
			List<ActivityDailyRechargeTypeSubItem> subItemList = dataItem.getSubItemList();
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

	private boolean isLevelEnough(Player player, ActivityDailyChargeCfg cfg) {
		if (null == cfg)
			return false;
		return player.getLevel() >= cfg.getLevelLimit();
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
	 * 设置活动是否被查看的红点
	 */
	public void updateRedPoint(Player player, String eNum) {
		ActivityDailyRechargeTypeItemHolder activityDailyRechargeItemHolder = ActivityDailyRechargeTypeItemHolder.getInstance();
		ActivityDailyChargeCfg cfg = ActivityDailyChargeCfgDAO.getInstance().getCfgById(eNum);
		if (cfg == null) {
			return;
		}
		ActivityDailyRechargeTypeItem dataItem = activityDailyRechargeItemHolder.getItem(player.getUserId(), eNum);
		if (dataItem == null) {
			GameLog.error(LogModule.ComActivityDailyRecharge, player.getUserId(), "心跳传入id获得的页签枚举无法找到活动数据", null);
			return;
		}

		if (!dataItem.isHasViewed()) {
			dataItem.setHasViewed(true);
		}
		activityDailyRechargeItemHolder.updateItem(player, dataItem);
	}

	/**
	 * 判断红点
	 * 
	 * @param player
	 * @return
	 */
	public List<String> haveRedPoint(Player player) {
		List<String> redPointList = new ArrayList<String>();
		List<ActivityDailyRechargeTypeItem> items = ActivityDailyRechargeTypeItemHolder.getInstance().getItemList(player.getUserId());
		if (null == items || items.isEmpty())
			return redPointList;
		ActivityDailyChargeSubCfgDAO chargeSubCfgDAO = ActivityDailyChargeSubCfgDAO.getInstance();
		for (ActivityDailyRechargeTypeItem item : items) {
			for (ActivityDailyRechargeTypeSubItem subItem : item.getSubItemList()) {
				ActivityDailyChargeSubCfg subCfg = chargeSubCfgDAO.getCfgById(subItem.getCfgId());
				if (null == subCfg)
					continue;
				if ((subCfg.getCount() <= item.getFinishCount() && !subItem.isGet()) || !item.isHasViewed()) {
					redPointList.add(String.valueOf(item.getCfgId()));
					break;
				}
			}
		}
		return redPointList;
	}
	
	
	
	
	
	
	
	/**
	 * 刷新每日充值
	 * 
	 * @param player
	 */
	public void dailyRefreshNewDaySubActivity(Player player) {
		ActivityDailyRechargeTypeItemHolder itemHolder = ActivityDailyRechargeTypeItemHolder.getInstance();
		ActivityDailyChargeSubCfgDAO chargeSubCfgDAO = ActivityDailyChargeSubCfgDAO.getInstance();
		List<ActivityDailyRechargeTypeItem> items = itemHolder.getItemList(player.getUserId());
		if (null == items || items.isEmpty()) {
			return;
		}
		for (ActivityDailyRechargeTypeItem item : items) {
			expireActivityEmail(player, item);
			// 这里要看，充值是否累计
			item.setFinishCount(0);
			List<ActivityDailyRechargeTypeSubItem> subItemList = new ArrayList<ActivityDailyRechargeTypeSubItem>();
			List<String> todaySubs = chargeSubCfgDAO.getTodaySubActivity(item.getCfgId());
			for (String subId : todaySubs) {
				ActivityDailyRechargeTypeSubItem subItem = new ActivityDailyRechargeTypeSubItem();
				subItem.setCfgId(subId);
				subItem.setGet(false);
				subItemList.add(subItem);
			}
			item.setSubItemList(subItemList);
			itemHolder.updateItem(player, item);
		}
		itemHolder.synAllData(player);
	}

	/**
	 * 邮件补发过期未领取的奖励
	 * 
	 * @param player
	 * @param item
	 */
	private void expireActivityEmail(Player player, ActivityDailyRechargeTypeItem item) {
		List<ActivityDailyRechargeTypeSubItem> subItems = item.getSubItemList();
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
	}
}
