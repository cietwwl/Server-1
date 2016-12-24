package com.playerdata.charge.action;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeCfgDAO;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeSubCfg;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeSubCfgDAO;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItem;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItemHolder;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeSubItem;
import com.playerdata.charge.ChargeResult;
import com.playerdata.charge.IChargeAction;
import com.playerdata.charge.cfg.ChargeCfg;
import com.playerdata.charge.cfg.ChargeTypeEnum;
import com.playerdata.charge.data.ChargeParam;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.Email.EmailUtils;
import com.rw.service.Privilege.MonthCardPrivilegeMgr;
import com.rw.service.dailyActivity.DailyActivityHandler;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.email.EmailCfg;
import com.rwbase.dao.email.EmailCfgDAO;

public class MonthCardChargeAction implements IChargeAction {

	public static final String SEND_MONTH_CARD_SUCCESS_EMAIL_ID = "17001";
	public static final String SEND_MONTH_CARD_FAIL_EMAIL_ID = "17002";
	
	private static final SimpleDateFormat _sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
	
	// 发送月卡给指定的玩家
	private IReadOnlyPair<ChargeResult, ActivityTimeCardTypeSubItem> addMonthCardToPlayer(Player targetPlayer, String timeCardSubCfgId) {
		ChargeResult result = ChargeResult.newResult(false);
		ActivityTimeCardTypeItemHolder dataHolder = ActivityTimeCardTypeItemHolder.getInstance();
		ActivityTimeCardTypeItem dataItem = dataHolder.getItem(targetPlayer.getUserId());
		if (dataItem == null) {// 首次读取创建记录
			dataItem = ActivityTimeCardTypeCfgDAO.getInstance().newItem(targetPlayer);
			if (dataItem != null) {
				dataHolder.addItem(targetPlayer, dataItem);
			}
		}

		List<ActivityTimeCardTypeSubItem> monthCardList = dataItem.getSubItemList();
		ActivityTimeCardTypeSubItem targetItem = null;
		ChargeTypeEnum cardtypenume = ActivityTimeCardTypeSubCfgDAO.getInstance().getById(timeCardSubCfgId).getChargeType();

		String cardtype = cardtypenume.getCfgId();
		for (ActivityTimeCardTypeSubItem itemTmp : monthCardList) {
			if (StringUtils.equals(itemTmp.getChargetype(), cardtype)) {
				targetItem = itemTmp;
				break;
			}
		}

		if (targetItem == null) {// newitem已添加list，不会null
			GameLog.error("chargemgr", "买月卡", "chargeMgr.list里没有该项月卡类型！！" + targetPlayer);
			result.setTips("购买月卡异常");
		} else {
			int tempdayleft = targetItem.getDayLeft();
			if (cardtypenume == ChargeTypeEnum.VipMonthCard) {
				if (targetItem.getDayLeft() > 0) {
					result.setSuccess(false);
					result.setTips("已经拥有至尊月卡，不能再购买");
					return Pair.Create(result, null);
				} else {
					result.setSuccess(true);
					targetItem.setDayLeft(Short.MAX_VALUE * 2); // 至尊月卡，现在是终身的，所以这里设置一个很长的剩余天数
				}
			} else {
				targetItem.setDayLeft(targetItem.getDayLeft() + ActivityTimeCardTypeSubCfgDAO.getInstance().getBynume(cardtypenume).getDays());
			}
			dataHolder.updateItem(targetPlayer, dataItem);
			result.setSuccess(true);

			GameLog.info("月卡", targetPlayer.getUserId(), "日常任务刷新", null);
			DailyActivityHandler.getInstance().sendTaskList(targetPlayer);

			if (tempdayleft < ActivityTimeCardTypeSubCfgDAO.getInstance().getById(timeCardSubCfgId).getDaysLimit()) {
				result.setTips("购买月卡成功");
			} else {
				result.setTips("剩余日期超过5天但依然冲了钱。。。");
				GameLog.error("chargemgr", "买月卡", "没到期也能付费,玩家名 =" + targetPlayer.getUserName() + " 月卡cfgid =" + timeCardSubCfgId);
			}
		}
		return Pair.Create(result, targetItem);
	}
	
	/**
	 * 赠送月卡失败的处理
	 * 
	 * @param player
	 */
	protected void onPresentMonthCardFail(Player player, ChargeCfg cfg) {
		switch (cfg.getChargeType()) {
		case VipMonthCard:
			EmailUtils.sendEmail(player.getUserId(), SEND_MONTH_CARD_FAIL_EMAIL_ID);
			break;
		case MonthCard:
			EmailUtils.sendEmail(player.getUserId(), SEND_MONTH_CARD_FAIL_EMAIL_ID, Collections.singletonMap(eSpecialItemId.Gold.getValue(), cfg.getMoneyCount() / 10));
			break;
		default:
			GameLog.error("onPresentMonthCardFail", player.getUserId(), "无法识别的月卡类型：" + cfg.getChargeType());
			break;
		}
	}
	
	/**
	 * 赠送月卡成功
	 * @param player
	 */
	protected void onPresentMonthCardSuccess(Player player, String friendId, String timeCardSubCfgId){
		EmailCfg emailCfg = EmailCfgDAO.getInstance().getCfgById(SEND_MONTH_CARD_SUCCESS_EMAIL_ID);
		String sendTime = _sdf.format(DateUtils.getCurrent().getTime());
		
		ActivityTimeCardTypeSubCfg cardCfg = ActivityTimeCardTypeSubCfgDAO.getInstance().getById(timeCardSubCfgId);
		ChargeTypeEnum cardEnum = cardCfg.getChargeType();
		
		String content = String.format(emailCfg.getContent(), player.getUserName(), sendTime, cardEnum.getName(), cardCfg.getGold());
		EmailUtils.sendEmail(friendId, SEND_MONTH_CARD_SUCCESS_EMAIL_ID, "", content);
	}

	/**
	 * 赠送月卡
	 * 
	 * @param friendPlayer
	 * @param selfPlayer
	 * @param timeCardSubCfgId
	 * @param target
	 * @return
	 */
	protected ChargeResult presentMonthCardToFriend(Player friendPlayer, Player selfPlayer, String timeCardSubCfgId, ChargeCfg target) {
//		IReadOnlyPair<ChargeResult, ActivityTimeCardTypeSubItem> pairResult = this.presentMonthCard(friendPlayer, timeCardSubCfgId);
//		if (!pairResult.getT1().isSuccess()) {
//			// 至尊月卡赠送失败的处理逻辑
//			onPresentMonthCardFail(selfPlayer);
//		}
//		ChargeResult result = pairResult.getT1();
//		if (result.isSuccess()) {
//			String orderStr = pairResult.getT2().getChargetype();
//			try {
//				if (StringUtils.isNotBlank(orderStr)) {
//					int order = Integer.parseInt(orderStr);
//					List<ChargeTypeEnum> orderList = ChargeTypeEnum.getOrderList();
//					if (0 <= order && order < orderList.size()) {
//						ChargeTypeEnum type = orderList.get(order);
//						MonthCardPrivilegeMgr.getShareInstance().signalMonthCardChange(friendPlayer, type, true);
//						onPresentMonthCardSuccess(selfPlayer, friendPlayer.getUserId(), timeCardSubCfgId);
//					}
//				}
//			} catch (Exception e) {
//				GameLog.info("特权", friendPlayer.getUserId(), "无法获取充值类型:" + orderStr, e);
//			}
//		}
//		return result;
		ChargeResult result = this.buyMonthCard(friendPlayer, timeCardSubCfgId, target);
		if (result.isSuccess()) {
			onPresentMonthCardSuccess(selfPlayer, friendPlayer.getUserId(), timeCardSubCfgId);
		} else {
			onPresentMonthCardFail(selfPlayer, target);
		}
		result.setSuccess(true); // 赠送月卡失败，但是处理了充值到自身，所以也应该成功
		return result;
	}

	/**
	 * 购买月卡
	 * 
	 * @param player
	 * @param timeCardSubCfgId
	 * @param target
	 * @return
	 */
	protected ChargeResult buyMonthCard(Player player, String timeCardSubCfgId, ChargeCfg target) {
		IReadOnlyPair<ChargeResult, ActivityTimeCardTypeSubItem> pairResult = this.addMonthCardToPlayer(player, timeCardSubCfgId);
		ChargeResult result = pairResult.getT1();
		if (result.isSuccess()) {
			String orderStr = pairResult.getT2().getChargetype();
			try {
				if (StringUtils.isNotBlank(orderStr)) {
					int order = Integer.parseInt(orderStr);
					List<ChargeTypeEnum> orderList = ChargeTypeEnum.getOrderList();
					if (0 <= order && order < orderList.size()) {
						ChargeTypeEnum type = orderList.get(order);
						MonthCardPrivilegeMgr.getShareInstance().signalMonthCardChange(player, type, true);
					}
				}
				player.getUserGameDataMgr().addReCharge(target.getGoldCount());
			} catch (Exception e) {
				GameLog.info("特权", player.getUserId(), "无法获取充值类型:" + orderStr, e);
			}
		}
		return result;
	}
	
	@Override
	public boolean doCharge(Player player, ChargeCfg target, ChargeParam param) {
		String friendId = param.getFriendId();
		List<ActivityTimeCardTypeSubCfg> timeCardList = ActivityTimeCardTypeSubCfgDAO.getInstance().getAllCfg();
		for (ActivityTimeCardTypeSubCfg timecardcfg : timeCardList) {
			if (timecardcfg.getChargeType() == target.getChargeType()) {
				Player friendPlayer = null;
				if(friendId != null) {
					friendId = friendId.trim();
				}
				if (StringUtils.isNotBlank(friendId)) {
					friendPlayer = PlayerMgr.getInstance().find(friendId);
					// 找不到要赠送的好友
					if (null == friendPlayer) {
						onPresentMonthCardFail(player, target);
						return true;
					}
				}
				if (null != friendPlayer) {
					return presentMonthCardToFriend(friendPlayer, player, timecardcfg.getId(), target).isSuccess();
				} else {
					return buyMonthCard(player, timecardcfg.getId(), target).isSuccess();
				}
			}
		}
		return false;
	}
}
