package com.playerdata.activity.chargeRebate;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bm.login.AccoutBM;
import com.log.GameLog;
import com.playerdata.EmailMgr;
import com.playerdata.Player;
import com.playerdata.VipMgr;
import com.playerdata.activity.chargeRebate.dao.ActivityChargeRebateDAO;
import com.playerdata.activity.chargeRebate.dao.ActivityChargeRebateData;
import com.playerdata.charge.ChargeMgr;
import com.playerdata.charge.cfg.ChargeTypeEnum;
import com.rw.manager.GameManager;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.user.accountInfo.TableAccount;

public class ActivityChargeRebateMgr {

	private static ActivityChargeRebateMgr _instance = new ActivityChargeRebateMgr();

	public static HashMap<Integer, String> vipEmailMap = new HashMap<Integer, String>();

	private static String rebateEmailId = "10319";
	private static String hundredEmailId = "10320";

	static {
		vipEmailMap.put(1, "10304");
		vipEmailMap.put(2, "10305");
		vipEmailMap.put(3, "10306");
		vipEmailMap.put(4, "10307");
		vipEmailMap.put(5, "10308");
		vipEmailMap.put(6, "10309");
		vipEmailMap.put(7, "10310");
		vipEmailMap.put(8, "10311");
		vipEmailMap.put(9, "10312");
		vipEmailMap.put(10, "10313");
		vipEmailMap.put(11, "10314");
		vipEmailMap.put(12, "10315");
		vipEmailMap.put(13, "10316");
		vipEmailMap.put(14, "10317");
		vipEmailMap.put(15, "10318");

	}

	public static ActivityChargeRebateMgr getInstance() {
		return _instance;
	}

	protected ActivityChargeRebateMgr() {
	};

	public void processChargeRebate(Player player, String accountId) {

		try {
			TableAccount userAccount = AccoutBM.getInstance().getByAccountId(accountId);
			String openAccount = userAccount.getOpenAccount();
			if (StringUtils.isEmpty(openAccount)) {
				return;
			}

			ActivityChargeRebateData activityChargeRebateData = ActivityChargeRebateDAO.getInstance().queryActivityChargeRebateData(openAccount);
			if (activityChargeRebateData == null || activityChargeRebateData.isSendReward()) {
				return;
			}
			int zoneId = GameManager.getZoneId();
			activityChargeRebateData.setSendReward(true);
			activityChargeRebateData.setZoneId(zoneId);
			ActivityChargeRebateDAO.getInstance().updateActivityChargeRebateData(activityChargeRebateData);
			
			GameLog.debug("activity charge rebate openAccount:" + openAccount + ",zoneId:" + zoneId);
			
			int chargeMoney = activityChargeRebateData.getChargeMoney();
			int result = 0;
			if (chargeMoney > 10000) {
				int temp = chargeMoney - 10000;
				result = 10000 * 15 + temp * 10;
			} else {
				result = chargeMoney * 15;
			}

			if (result > 0) {
				// 返回vip经验
				ChargeMgr chargeMgr = ChargeMgr.getInstance();
				chargeMgr.addVipExp(player, activityChargeRebateData.getVipExp(), true);
				// 返还月卡
				int monthCard = activityChargeRebateData.getMonthCard();
				if (monthCard > 0) {
					chargeMgr.addMonthCard(player, ChargeTypeEnum.MonthCard, monthCard);
				}
				int vipMonthCard = activityChargeRebateData.getVipMonthCard();
				if (vipMonthCard > 0) {
					chargeMgr.addMonthCard(player, ChargeTypeEnum.VipMonthCard, vipMonthCard);
				}
				Map<Integer, Integer> itemMap = new HashMap<Integer, Integer>();
				itemMap.put(2, result);
				EmailUtils.sendEmail(player.getUserId(), rebateEmailId, itemMap);

				int vip = player.getVip();
				for (int i = 1; i <= vip; i++) {
					String vipEmailId = vipEmailMap.get(i);
					if (!StringUtils.isEmpty(vipEmailId)) {
						EmailUtils.sendEmail(player.getUserId(), vipEmailId);
					}
				}

				if (chargeMoney >= 1000) {
					EmailUtils.sendEmail(player.getUserId(), hundredEmailId);
				}
			}

			// 返还竞技之王的头框
			if (activityChargeRebateData.isArenaKing()) {
				player.getSettingMgr().addHeadBox("20007");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] objs){
		new ClassPathXmlApplicationContext(new String[] { "classpath:applicationContext.xml" });
		String openAccount = "1204212437";
		ActivityChargeRebateData activityChargeRebateData = ActivityChargeRebateDAO.getInstance().queryActivityChargeRebateData(openAccount);
		if (activityChargeRebateData == null || activityChargeRebateData.isSendReward()) {
			return;
		}
		int zoneId = GameManager.getZoneId();
		activityChargeRebateData.setSendReward(true);
		activityChargeRebateData.setZoneId(zoneId);
		ActivityChargeRebateDAO.getInstance().updateActivityChargeRebateData(activityChargeRebateData);
	}
}
