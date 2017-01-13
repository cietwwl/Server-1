package com.rw.service.log;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.bm.targetSell.TargetSellManager;
import com.google.protobuf.ProtocolMessageEnum;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.UserDataMgr;
import com.playerdata.charge.cfg.ChargeCfg;
import com.rw.chargeServer.ChargeContentPojo;
import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.dao.cache.trace.DataEventRecorder;
import com.rw.fsutil.util.DateUtils;
import com.rw.manager.GameManager;
import com.rw.netty.ServerConfig;
import com.rw.netty.UserChannelMgr;
import com.rw.service.group.helper.GroupMemberHelper;
import com.rw.service.log.behavior.GameBehaviorRecord;
import com.rw.service.log.eLog.eBILogCopyEntrance;
import com.rw.service.log.eLog.eBILogRegSubChannelToClientPlatForm;
import com.rw.service.log.eLog.eBILogType;
import com.rw.service.log.infoPojo.RoleGameInfo;
import com.rw.service.log.infoPojo.ZoneLoginInfo;
import com.rw.service.log.infoPojo.ZoneRegInfo;
import com.rw.service.log.template.AccountLogoutLogTemplate;
import com.rw.service.log.template.ActivityBeginLogTemplate;
import com.rw.service.log.template.ActivityEndLogTemplate;
import com.rw.service.log.template.BIActivityCode;
import com.rw.service.log.template.BIActivityEntry;
import com.rw.service.log.template.BILogTemplate;
import com.rw.service.log.template.BILogTemplateHelper;
import com.rw.service.log.template.BITaskType;
import com.rw.service.log.template.ChatLogTemplate;
import com.rw.service.log.template.CoinChangedLogTemplate;
import com.rw.service.log.template.CopyBeginLogTemplate;
import com.rw.service.log.template.CopyEndLogTemplate;
import com.rw.service.log.template.EmailLogTemplate;
import com.rw.service.log.template.FinanceMainCoinHoldLogTemplate;
import com.rw.service.log.template.FinanceMainCoinLogTemplate;
import com.rw.service.log.template.FinanceMainConsumeLogTemplate;
import com.rw.service.log.template.GiftGoldChangedLogTemplate;
import com.rw.service.log.template.GiftPackageLogTemplate;
import com.rw.service.log.template.GoldChangeLogTemplate;
import com.rw.service.log.template.GuideLogTemplate;
import com.rw.service.log.template.ItemChangedLogTemplate;
import com.rw.service.log.template.OnlineCountLogTemplate;
import com.rw.service.log.template.PayLogTemplate;
import com.rw.service.log.template.RoleCreatedLogTemplate;
import com.rw.service.log.template.RoleLoginLogTemplate;
import com.rw.service.log.template.RoleLogoutLogTemplate;
import com.rw.service.log.template.RoleUpgradeLogTemplate;
import com.rw.service.log.template.TaskBeginLogTemplate;
import com.rw.service.log.template.TaskEndLogTemplate;
import com.rw.service.log.template.ZoneCountChargeGoldLogTemplate;
import com.rw.service.log.template.ZoneCountCoinLogTemplate;
import com.rw.service.log.template.ZoneCountGiftGoldLogTemplate;
import com.rw.service.log.template.ZoneCountLevelSpreadLogTemplate;
import com.rw.service.log.template.ZoneCountTotalAccountLogTemplate;
import com.rw.service.log.template.ZoneCountVipSpreadLogTemplate;
import com.rw.service.log.template.ZoneLoginLogTemplate;
import com.rw.service.log.template.ZoneLogoutLogTemplate;
import com.rw.service.log.template.ZoneRegLogTemplate;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.copypve.CopyType;
import com.rwbase.dao.email.EmailItem;
import com.rwbase.dao.fresherActivity.FresherActivityCfgDao;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityCfg;
import com.rwbase.dao.item.SpecialItemCfgDAO;
import com.rwbase.dao.item.pojo.ItemBaseCfg;
import com.rwbase.dao.item.pojo.SpecialItemCfg;
import com.rwbase.dao.task.DailyActivityCfgDAO;
import com.rwbase.dao.task.pojo.DailyActivityCfg;
import com.rwbase.dao.task.pojo.TaskItem;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwproto.MsgDef.Command;

public class BILogMgr {

	private static Map<eBILogType, Logger> LogMap = new HashMap<eBILogType, Logger>();

	private static BILogMgr instance = new BILogMgr();

	private Map<eBILogType, BILogTemplate> templateMap;

	public static BILogMgr getInstance() {
		return instance;
	}

	protected BILogMgr() {

		templateMap = new HashMap<eBILogType, BILogTemplate>();
		templateMap.put(eBILogType.ZoneReg, new ZoneRegLogTemplate());
		templateMap.put(eBILogType.ZoneLogin, new ZoneLoginLogTemplate());
		templateMap.put(eBILogType.ZoneLogout, new ZoneLogoutLogTemplate());
		templateMap.put(eBILogType.AccountLogout, new AccountLogoutLogTemplate());
		templateMap.put(eBILogType.RoleCreated, new RoleCreatedLogTemplate());
		templateMap.put(eBILogType.OnlineCount, new OnlineCountLogTemplate());
		templateMap.put(eBILogType.CopyBegin, new CopyBeginLogTemplate());
		templateMap.put(eBILogType.CopyEnd, new CopyEndLogTemplate());
		templateMap.put(eBILogType.RoleLogin, new RoleLoginLogTemplate());
		templateMap.put(eBILogType.RoleLogout, new RoleLogoutLogTemplate());
		templateMap.put(eBILogType.ItemChanged, new ItemChangedLogTemplate());
		templateMap.put(eBILogType.CoinChanged, new CoinChangedLogTemplate());
		templateMap.put(eBILogType.ZoneCountCoin, new ZoneCountCoinLogTemplate());
		templateMap.put(eBILogType.TaskBegin, new TaskBeginLogTemplate());
		templateMap.put(eBILogType.TaskEnd, new TaskEndLogTemplate());
		templateMap.put(eBILogType.TotalAccount, new ZoneCountTotalAccountLogTemplate());
		templateMap.put(eBILogType.LevelSpread, new ZoneCountLevelSpreadLogTemplate());
		templateMap.put(eBILogType.VipSpread, new ZoneCountVipSpreadLogTemplate());
		templateMap.put(eBILogType.ActivityBegin, new ActivityBeginLogTemplate());
		templateMap.put(eBILogType.ActivityEnd, new ActivityEndLogTemplate());
		templateMap.put(eBILogType.RoleUpgrade, new RoleUpgradeLogTemplate());
		templateMap.put(eBILogType.ZoneCountGiftGold, new ZoneCountGiftGoldLogTemplate());
		templateMap.put(eBILogType.ZoneCountChargeGold, new ZoneCountChargeGoldLogTemplate());
		templateMap.put(eBILogType.GiftGoldChanged, new GiftGoldChangedLogTemplate());
		templateMap.put(eBILogType.Chat, new ChatLogTemplate());
		templateMap.put(eBILogType.GoldChange, new GoldChangeLogTemplate());
		templateMap.put(eBILogType.Email, new EmailLogTemplate());
		templateMap.put(eBILogType.Pay, new PayLogTemplate());
		templateMap.put(eBILogType.FinanceMainCoinAdd, new FinanceMainCoinLogTemplate());
		templateMap.put(eBILogType.FinanceMainCoinConsume, new FinanceMainConsumeLogTemplate());
		templateMap.put(eBILogType.FinanceMainCoinHold, new FinanceMainCoinHoldLogTemplate());
		templateMap.put(eBILogType.GiftPackage, new GiftPackageLogTemplate());
		templateMap.put(eBILogType.Guide, new GuideLogTemplate());
	}

	private Logger getLogger(eBILogType type) {
		if (LogMap.containsKey(type)) {
			return LogMap.get(type);
		} else {
			Logger logger = Logger.getLogger(type.getLogName());
			try {

				logger.removeAllAppenders();
				logger.setAdditivity(false);
				PatternLayout layout = new PatternLayout();
				layout.setConversionPattern("[%-5p] %m%n");
				DailyRollingFileAppender appender;

				appender = new DailyRollingFileAppender(layout, "./log/biLog/" + type.getLogName() + "/" + type.getLogName(), "yyyy-MM-dd");

				logger.addAppender(appender);
				LogMap.put(type, logger);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return logger;
		}
	}

	public void initLogger() {
		eBILogType[] allValue = eBILogType.getAllValue();
		for (eBILogType bilog : allValue) {
			Logger logger = Logger.getLogger(bilog.getLogName());
			try {

				logger.removeAllAppenders();
				logger.setAdditivity(false);
				PatternLayout layout = new PatternLayout();
				layout.setConversionPattern("[%-5p] %m%n");
				DailyRollingFileAppender appender;

				appender = new DailyRollingFileAppender(layout, "./log/biLog/" + bilog.getLogName() + "/" + bilog.getLogName(), "yyyy-MM-dd");

				logger.addAppender(appender);
				LogMap.put(bilog, logger);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void logZoneReg(Player player) {
		logPlayer(eBILogType.ZoneReg, player, null);
		logPlayer(eBILogType.RoleCreated, player, null);
		// logZoneLogin(player);
	}

	public void logZoneLogin(String userId) {
		Player player = PlayerMgr.getInstance().find(userId);
		Map<String, String> moreInfo = new HashMap<String, String>();
		moreInfo.put("result", "1");
		logPlayer(eBILogType.ZoneLogin, player, moreInfo);
		logRoleLogin(player);
	}

	public void logGiftPackage(Player player, String activeCode, String giftPackageId, String giftPackageType, String opCode, String groupId, String reward) {

		Map<String, String> moreInfo = new HashMap<String, String>();
		moreInfo.put("giftPackageEntrance", "142");
		moreInfo.put("activeCode", activeCode);
		moreInfo.put("giftPackageId", giftPackageId);
		moreInfo.put("giftPackageType", giftPackageType);
		moreInfo.put("optype", opCode);
		moreInfo.put("factionId", groupId);
		moreInfo.put("giftReward", StringUtils.isEmpty(reward) ? "" : "item@num:" + reward);
		logPlayer(eBILogType.GiftPackage, player, moreInfo);
	}
	
	public void logGuide(Player player, String guideCode, int guideType) {
		Map<String, String> moreInfo = new HashMap<String, String>();
		moreInfo.put("guideEntrance", String.valueOf(player.getUserDataMgr().getEntranceId()));
		moreInfo.put("guideCode", guideCode);
		moreInfo.put("guideType", "1");
		moreInfo.put("optype", "guide_finsh");
		logPlayer(eBILogType.Guide, player, moreInfo);
	}

	public void logZoneLogout(Player player) {
		Map<String, String> moreInfo = new HashMap<String, String>();
		moreInfo.put("result", "1");
		logPlayer(eBILogType.ZoneLogout, player, moreInfo);
		logAccountLogout(player, moreInfo);
		logRoleLogout(player);
	}

	public void logChat(Player player, String targetUserId, int type, String content) {
		Map<String, String> moreInfo = new HashMap<String, String>();
		UserDataMgr userDataMgr = player.getUserDataMgr();
		ZoneRegInfo zoneRegInfo = userDataMgr.getZoneRegInfo();
		if (null != zoneRegInfo) {
			moreInfo.put("chatSenderAccount", zoneRegInfo.getRegChannelId() + "_" + userDataMgr.getAccount());
		}
		moreInfo.put("vip", String.valueOf(player.getVip()));
		moreInfo.put("chatReceiverUseId", targetUserId);
		moreInfo.put("chatType", String.valueOf(type));
		moreInfo.put("chatContent", String.valueOf(content));
		logPlayer(eBILogType.Chat, player, moreInfo);
	}

	private void logAccountLogout(Player player, Map<String, String> moreInfo) {

		logPlayer(eBILogType.AccountLogout, player, moreInfo);
	}

	private void logRoleLogin(Player player) {
		logPlayer(eBILogType.RoleLogin, player, null);
	}

	private void logRoleLogout(Player player) {
		Map<String, String> moreInfo = new HashMap<String, String>();
		int[] levelId = BILogTemplateHelper.getLevelId(player);
		moreInfo.put("sp_case", levelId[0] + "");
		moreInfo.put("nm_case", levelId[1] + "");

		logPlayer(eBILogType.RoleLogout, player, moreInfo);
	}

	/* 服务器当前没人在线时传入onlinecount为null */
	public void logOnlineCount(eBILogRegSubChannelToClientPlatForm regsubchanneltoclientplatform, String str) {
		Map<String, String> moreInfo = new HashMap<String, String>();

		if (str != null) {
			moreInfo.put("onlineCount", "" + regsubchanneltoclientplatform.getcount());
			moreInfo.put("loginZoneId", "" + ServerConfig.getInstance().getZoneId());
			moreInfo.put("loginClientPlatForm", regsubchanneltoclientplatform.getclientPlayForm());
			moreInfo.put("regSubChannelId", regsubchanneltoclientplatform.getregSubChannelId());
			moreInfo.put("threadId", "" + Thread.currentThread().getId());
		}
		log(eBILogType.OnlineCount, null, null, null, moreInfo);
	}

	public void logZoneCountCoin(String regSubChannelId, long zoneCoinRemain, String clientPlatForm) {

		Map<String, String> moreInfo = new HashMap<String, String>();

		moreInfo.put("threadId", "" + Thread.currentThread().getId());
		moreInfo.put("zoneCoinRemain", "" + zoneCoinRemain);
		moreInfo.put("loginZoneId", "" + ServerConfig.getInstance().getZoneId());
		moreInfo.put("regSubChannelId", regSubChannelId);
		moreInfo.put("loginClientPlatForm", clientPlatForm);

		log(eBILogType.ZoneCountCoin, null, null, null, moreInfo);
	}

	public void logZoneCountGiftGold(String regSubChannelId, long zoneGoldRemain, String clientPlatForm) {

		Map<String, String> moreInfo = new HashMap<String, String>();

		moreInfo.put("threadId", "" + Thread.currentThread().getId());
		moreInfo.put("zoneGiftGoldRemain", "" + zoneGoldRemain);
		moreInfo.put("loginZoneId", "" + ServerConfig.getInstance().getZoneId());
		moreInfo.put("regSubChannelId", regSubChannelId);
		moreInfo.put("loginClientPlatForm", clientPlatForm);

		log(eBILogType.ZoneCountGiftGold, null, null, null, moreInfo);
	}

	public void logZoneCountChargeGold(String regSubChannelId, long zoneGoldRemain, String clientPlatForm) {

		Map<String, String> moreInfo = new HashMap<String, String>();

		moreInfo.put("threadId", "" + Thread.currentThread().getId());
		moreInfo.put("zoneChargeGoldRemain", "" + zoneGoldRemain);
		moreInfo.put("loginZoneId", "" + ServerConfig.getInstance().getZoneId());
		moreInfo.put("regSubChannelId", regSubChannelId);
		moreInfo.put("loginClientPlatForm", clientPlatForm);
		log(eBILogType.ZoneCountChargeGold, null, null, null, moreInfo);
	}

	public void logZoneCountFinanceGold(String regSubChannelId, long zoneGoldRemain, String loginZoinId) {
		Map<String, String> moreInfo = new HashMap<String, String>();
		moreInfo.put("loginZoneId_regChannelId", "" + loginZoinId + "_" + regSubChannelId);
		moreInfo.put("threadId", "" + Thread.currentThread().getId());
		moreInfo.put("loginZoneId", "" + ServerConfig.getInstance().getZoneId());
		moreInfo.put("regSubChannelId", regSubChannelId);
		moreInfo.put("mainGoldGount", "" + zoneGoldRemain);
		log(eBILogType.FinanceMainCoinHold, null, null, null, moreInfo);
	}

	public void logZoneCountLevelSpread(String regSubChannelId, String level, long levelCount, String clientPlatForm) {

		Map<String, String> moreInfo = new HashMap<String, String>();
		moreInfo.put("threadId", "" + Thread.currentThread().getId());
		moreInfo.put("level", level);
		moreInfo.put("levelCount", "" + levelCount);
		moreInfo.put("loginZoneId", "" + ServerConfig.getInstance().getZoneId());
		moreInfo.put("regSubChannelId", regSubChannelId);
		moreInfo.put("loginClientPlatForm", clientPlatForm);

		log(eBILogType.LevelSpread, null, null, null, moreInfo);
	}

	public void logZoneCountVipSpread(String regSubChannelId, String vipLevel, long count, String clientPlatForm) {

		Map<String, String> moreInfo = new HashMap<String, String>();
		moreInfo.put("threadId", "" + Thread.currentThread().getId());
		moreInfo.put("totalAccount", "" + count);
		moreInfo.put("vip", "" + vipLevel);
		moreInfo.put("loginZoneId", "" + ServerConfig.getInstance().getZoneId());
		moreInfo.put("regSubChannelId", regSubChannelId);
		moreInfo.put("loginClientPlatForm", clientPlatForm);

		log(eBILogType.VipSpread, null, null, null, moreInfo);
	}

	public void logZoneCountTotalAccount(String regSubChannelId, long totalAccount, String clientPlatForm) {

		Map<String, String> moreInfo = new HashMap<String, String>();
		moreInfo.put("threadId", "" + Thread.currentThread().getId());
		moreInfo.put("totalAccount", "" + totalAccount);
		moreInfo.put("loginZoneId", "" + ServerConfig.getInstance().getZoneId());
		moreInfo.put("regSubChannelId", regSubChannelId);
		moreInfo.put("loginClientPlatForm", clientPlatForm);

		log(eBILogType.TotalAccount, null, null, null, moreInfo);
	}

	public void doLogoutLog() {
		List<String> offLineUserIds = UserChannelMgr.extractLogoutUserIdList();
		if (offLineUserIds == null) {
			return;
		}
		for (String uidTmp : offLineUserIds) {
			Player offLiner = PlayerMgr.getInstance().find(uidTmp);
			if (offLiner != null) {
				try {
					logZoneLogout(offLiner);
					// TODO HC 临时增加一个离线通知到帮派修改成员离线时间
					GroupMemberHelper.onPlayerLogout(offLiner);
					TargetSellManager.getInstance().packHeroChangeAttr(uidTmp);
				} catch (Exception e) {
					GameLog.error(LogModule.BILOG.getName(), uidTmp, "player BILogMgr[doLogoutLog] error!", e);
				}
			} else {
				GameLog.error(LogModule.BILOG.getName(), uidTmp, "player not found!");
			}
		}

	}

	/**
	 * 
	 * @param player
	 * @param activityEntry 活动入口
	 * @param activityCode 活动code
	 * @param severBegin 为开服活动时传入的子参数
	 */
	public void logActivityBegin(Player player, BIActivityEntry activityEntry, BIActivityCode activityCode, int copyLevelId, int severBegin) {
		Map<String, String> moreInfo = new HashMap<String, String>();
		moreInfo.put("activityEntry", "" + player.getUserDataMgr().getEntranceId());

		if (StringUtils.equals(activityCode.toString(), BIActivityCode.SEVER_BEGIN_ACTIVITY_ONE.toString())) {
			FresherActivityCfg fresherActivityCfg = FresherActivityCfgDao.getInstance().getFresherActivityCfg(severBegin);
			moreInfo.put("activityCode", "" + fresherActivityCfg.getActivityCode());
		} else if (StringUtils.equals(activityCode.toString(), BIActivityCode.DAILY_TASK.toString())) {
			DailyActivityCfg cfg = DailyActivityCfgDAO.getInstance().getCfgById(String.valueOf(severBegin));
			moreInfo.put("activityCode", "" + cfg.getBICode());
		} else {
			moreInfo.put("activityCode", "" + activityCode.getCode());
		}
		moreInfo.put("copyId", "" + copyLevelId);
		moreInfo.put("result", "1");

		logPlayer(eBILogType.ActivityBegin, player, moreInfo);
	}

	/**
	 * 
	 * @param player
	 * @param activityEntry 入口id
	 * @param activityCode 活动code
	 * @param copyLevelId 副本id
	 * @param isWin 是否成功
	 * @param activityTime 耗时
	 * @param rewardinfoactivity 奖励文字
	 * @param severBegin 为开服活动时传入的子参数
	 */
	public void logActivityEnd(Player player, BIActivityEntry activityEntry, BIActivityCode activityCode, int copyLevelId, boolean isWin, int activityTime, String rewardinfoactivity, int severBegin) {
		Map<String, String> moreInfo = new HashMap<String, String>();
		moreInfo.put("activityEntry", "" + player.getUserDataMgr().getEntranceId());
		if (StringUtils.equals(activityCode.toString(), BIActivityCode.SEVER_BEGIN_ACTIVITY_ONE.toString())) {
			FresherActivityCfg fresherActivityCfg = FresherActivityCfgDao.getInstance().getFresherActivityCfg(severBegin);
			moreInfo.put("activityCode", "" + fresherActivityCfg.getActivityCode());
		} else if (StringUtils.equals(activityCode.toString(), BIActivityCode.DAILY_TASK.toString())) {
			DailyActivityCfg cfg = DailyActivityCfgDAO.getInstance().getCfgById(String.valueOf(severBegin));
			moreInfo.put("activityCode", "" + cfg.getBICode());
		} else {
			moreInfo.put("activityCode", "" + activityCode.getCode());
		}
		moreInfo.put("copyId", "" + copyLevelId);
		moreInfo.put("activityTime", "" + activityTime);
		moreInfo.put("result", "1");
		if (isWin) {
			moreInfo.put("operationCode", "activity_win");
		} else {
			moreInfo.put("operationCode", "activity_fail");
		}
		moreInfo.put("rewardsinfoactivity", rewardinfoactivity);
		logPlayer(eBILogType.ActivityEnd, player, moreInfo);
	}

	/**
	 * 任务开始
	 * 
	 * @param player
	 * @param taskId
	 * @param biTaskType
	 */
	public void logTaskBegin(Player player, Integer taskId, BITaskType biTaskType) {
		Map<String, String> moreInfo = new HashMap<String, String>();
		moreInfo.put("taskId", taskId.toString());
		moreInfo.put("result", "1");
		moreInfo.put("optype", "task_start");
		moreInfo.put("biTaskType", "" + biTaskType.getTypeNo());

		logPlayer(eBILogType.TaskBegin, player, moreInfo);
	}

	public void logTaskBegin(Player player, List<TaskItem> list, BITaskType biTaskType) {
		for (TaskItem taskItem : list) {
			String taskId = String.valueOf(taskItem.getTaskId());
			Map<String, String> moreInfo = new HashMap<String, String>();
			moreInfo.put("taskId", taskId);
			moreInfo.put("result", "1");
			moreInfo.put("optype", "task_start");
			moreInfo.put("biTaskType", "" + biTaskType.getTypeNo());

			logPlayer(eBILogType.TaskBegin, player, moreInfo);
		}
	}

	/**
	 * 任务结束
	 * 
	 * @param player
	 * @param taskId
	 * @param biTaskType
	 */
	public void logTaskEnd(Player player, Integer taskId, BITaskType biTaskType, boolean success, String rewardinfoactivity) {
		Map<String, String> moreInfo = new HashMap<String, String>();

		if (success) {
			moreInfo.put("optype", "task_win");
		} else {
			moreInfo.put("optype", "task_fail");

		}
		moreInfo.put("taskId", taskId.toString());
		moreInfo.put("result", "1");
		moreInfo.put("biTaskType", String.valueOf(biTaskType.getTypeNo()));
		moreInfo.put("task_time", "0");
		moreInfo.put("rewardsinfotask", rewardinfoactivity);
		logPlayer(eBILogType.TaskEnd, player, moreInfo);
	}

	public void logCopyBegin(Player player, Integer copyId, int copyLevel, boolean isFirst, eBILogCopyEntrance entranceType) {
		Map<String, String> moreInfo = new HashMap<String, String>();
		moreInfo.put("copyEntrance", String.valueOf(player.getUserDataMgr().getEntranceId()));
		moreInfo.put("copyId", copyId.toString());
		moreInfo.put("result", "1");
		moreInfo.put("copyLevel", getLogCopyLevel(copyLevel));
		if (Integer.parseInt(getLogCopyLevel(copyLevel)) == 0) {
			return;
		}
		if (isFirst) {
			moreInfo.put("copyStatus", "1");
		} else {
			moreInfo.put("copyStatus", "2");
		}
		// moreInfo.put("copyEntrance", entranceType.name());

		logPlayer(eBILogType.CopyBegin, player, moreInfo);
	}

	/**
	 * 
	 * @param player
	 * @param copyId
	 * @param isFirst 是否首次
	 */
	public void logCopyEnd(Player player, Integer copyId, int copyLevel, boolean isFirst, boolean isWin, int fightTime, String rewards) {
		Map<String, String> moreInfo = new HashMap<String, String>();
		moreInfo.put("copyEntrance", String.valueOf(player.getUserDataMgr().getEntranceId()));
		moreInfo.put("copyId", copyId.toString());
		moreInfo.put("result", "1");
		moreInfo.put("copyLevel", getLogCopyLevel(copyLevel));
		if (Integer.parseInt(getLogCopyLevel(copyLevel)) == 0) {
			return;
		}

		moreInfo.put("fightTime", "" + fightTime);
		if (isFirst) {
			moreInfo.put("copyStatus", "1");
		} else {
			moreInfo.put("copyStatus", "2");
		}

		if (isWin) {
			moreInfo.put("operationCode", "case_win");
		} else {
			moreInfo.put("operationCode", "case_fail");
		}
		moreInfo.put("rewardsinfocopy", rewards);
		int[] levelId = BILogTemplateHelper.getLevelId(player);
		moreInfo.put("sp_case", levelId[0] + "");
		moreInfo.put("nm_case", levelId[1] + "");
		logPlayer(eBILogType.CopyEnd, player, moreInfo);
	}

	private String getLogCopyLevel(int levelType) {
		Integer logLevelType = 0;

		if (levelType == CopyType.COPY_TYPE_NORMAL) {
			logLevelType = 1;
		} else if (levelType == CopyType.COPY_TYPE_ELITE) {
			logLevelType = 2;
		}

		return logLevelType.toString();
	}

	/**
	 * 
	 * @param player
	 * @param copyId 扫荡
	 */
	public void logSweep(Player player, Integer copyId, int copyLevel, String rewards) {
		Map<String, String> moreInfo = new HashMap<String, String>();
		moreInfo.put("copyEntrance", "" + player.getUserDataMgr().getEntranceId());
		moreInfo.put("copyId", copyId.toString());
		moreInfo.put("result", "1");
		moreInfo.put("copyStatus", "3");
		moreInfo.put("copyLevel", getLogCopyLevel(copyLevel));
		moreInfo.put("operationCode", "case_win");
		moreInfo.put("fightTime", "0");
		if (Integer.parseInt(getLogCopyLevel(copyLevel)) == 0) {
			return;
		}
		int[] levelId = BILogTemplateHelper.getLevelId(player);
		moreInfo.put("sp_case", levelId[0] + "");
		moreInfo.put("nm_case", levelId[1] + "");
		moreInfo.put("rewardsinfocopy", rewards);
		logPlayer(eBILogType.CopyBegin, player, moreInfo);
		logPlayer(eBILogType.CopyEnd, player, moreInfo);
	}

	public void logItemChanged(GameBehaviorRecord record, String incrInfo, String decrInfo) {
		String userId = record.getUserId();
		Player player = PlayerMgr.getInstance().findPlayerForRead(userId);
		if (player == null) {
			FSUtilLogger.error("logItemChanged fail by " + userId + ",incrInfo=" + incrInfo + ",decrInfo=" + decrInfo);
			return;
		}

		HashMap<String, String> moreInfo = record(record);

		moreInfo.put("itemList_incr", incrInfo);
		moreInfo.put("itemList_decr", decrInfo);

		logPlayer(eBILogType.ItemChanged, player, moreInfo);
	}

	public void logCoinChanged(GameBehaviorRecord record, int coinChanged, long coinRemain) {
		String userId = record.getUserId();
		Player player = PlayerMgr.getInstance().findPlayerForRead(userId);
		if (player == null) {
			FSUtilLogger.error("logCoinChanged fail by " + userId + ",coinChanged=" + coinChanged + ",coinRemain=" + coinRemain);
			return;
		}
		HashMap<String, String> moreInfo = record(record);
		moreInfo.put("coinChanged", String.valueOf(coinChanged));
		moreInfo.put("coinRemain", String.valueOf(coinRemain));

		logPlayer(eBILogType.CoinChanged, player, moreInfo);
	}

	private HashMap<String, String> record(GameBehaviorRecord record) {
		HashMap<String, String> moreInfo = new HashMap<String, String>();
		int viewId = record.getViewId();
		if (viewId > 0) {
			moreInfo.put("scenceId", String.valueOf(viewId));
		}

		Command command = record.getCommand();
		if (command != null) {
			moreInfo.put("ItemChangedEventType_1", String.valueOf(record.getCommand().getNumber()));
		}

		ProtocolMessageEnum type = record.getMsgType();
		if (type != null) {
			moreInfo.put("ItemChangedEventType_2", String.valueOf(type.getNumber()));
		}
		return moreInfo;
	}

	public void logGiftGoldChanged(GameBehaviorRecord record, int coinChanged, long coinRemain) {
		String userId = record.getUserId();
		Player player = PlayerMgr.getInstance().findPlayerForRead(userId);
		if (player == null) {
			FSUtilLogger.error("logCoinChanged fail by " + userId + ",coinChanged=" + coinChanged + ",coinRemain=" + coinRemain);
			return;
		}
		HashMap<String, String> moreInfo = record(record);
		moreInfo.put("giftGoldChanged", String.valueOf(coinChanged));
		moreInfo.put("giftGoldRemain", String.valueOf(coinRemain));

		logPlayer(eBILogType.GiftGoldChanged, player, moreInfo);
	}

	/**
	 * 充值币（变动）
	 * 
	 * @param typeList
	 * @param coinChanged
	 * @param coinRemain
	 */
	public void logGoldChanged(GameBehaviorRecord record, Player player, int coinChanged, long coinRemain) {
		HashMap<String, String> moreInfo = record(record);
		moreInfo.put("giftGoldChanged", String.valueOf(coinChanged));
		moreInfo.put("giftGoldRemain", String.valueOf(coinRemain));

		logPlayer(eBILogType.GoldChange, player, moreInfo);
	}

	/**
	 * 财务充值币消耗
	 * 
	 * @param typeList
	 * @param coinChanged
	 * @param coinRemain
	 */
	public void logFinanceMainCoinConsume(GameBehaviorRecord record, Player player, int coinChanged, long coinRemain) {
		HashMap<String, String> moreInfo = record(record);
		moreInfo.put("mainGoldGountconsume", String.valueOf(coinChanged));
		moreInfo.put("mainGoldGount", String.valueOf(coinRemain));

		logPlayer(eBILogType.FinanceMainCoinConsume, player, moreInfo);
	}

	public void logRoleUpgrade(Player player, int oldlevel, int fightbeforelevelup) {
		GameBehaviorRecord record = (GameBehaviorRecord) DataEventRecorder.getParam();
		HashMap<String, String> moreInfo = null;
		if (record != null) {
			if (player.getUserId().equals(record.getUserId())) {
				moreInfo = record(record);
			} else {
				FSUtilLogger.error("logRoleUpgrade wrong userId=" + player.getUserId() + ",recordUserId=" + record.getUserId());
			}
		}
		if (moreInfo == null) {
			moreInfo = new HashMap<String, String>();
		}

		moreInfo.put("levelBeforeUp", String.valueOf(oldlevel));
		moreInfo.put("fightbeforelevelup", "last_fight_power:" + fightbeforelevelup);
		moreInfo.put("mapid", String.valueOf(record.getMapId()));
		logPlayer(eBILogType.RoleUpgrade, player, moreInfo);
	}

	public void logEmail(String userId, EmailItem emailData, EmailLogTemplate.EamilOpType opType) {
		try {
			Map<String, String> moreInfo = new HashMap<String, String>();
			moreInfo.put("opType", opType.getId());
			moreInfo.put("userId", userId);
			moreInfo.put("emailId", emailData.getEmailId());
			moreInfo.put("emailTitle", emailData.getTitle().replace("\n", "\\n").replace("\r", "\\r"));
			moreInfo.put("emailContent", emailData.getContent().replace("\n", "\\n").replace("\r", "\\r"));
			moreInfo.put("coolTime", emailData.getCoolTime() == 0 ? "" : DateUtils.getDateTimeFormatString(emailData.getCoolTime(), "yyyy-MM-dd HH:mm:ss"));
			moreInfo.put("expireTime", emailData.getDeadlineTimeInMill() == 0 ? "" : DateUtils.getDateTimeFormatString(emailData.getDeadlineTimeInMill(), "yyyy-MM-dd HH:mm:ss"));

			String emailAttachment = emailData.getEmailAttachment();
			String[] split = emailAttachment.split(",");
			StringBuilder sbAttachList = new StringBuilder();
			StringBuilder sbAttachAttr = new StringBuilder();
			int index = 0;
			if (split.length > 0) {
				for (String value : split) {
					if (StringUtils.isEmpty(value)) {
						continue;
					}
					String[] split2 = value.split("~");
					if (split2.length > 1) {
						int model = Integer.parseInt(split2[0]);
						String count = split2[1];
						sbAttachList.append(model).append(":").append(count);
						eSpecialItemId def = eSpecialItemId.getDef(model);
						if (def == null) {
							ItemBaseCfg cfg = ItemCfgHelper.GetConfig(model);
							String name = cfg != null ? cfg.getName() : "";
							String desc = cfg != null ? cfg.getDescription() : "";
							sbAttachAttr.append(model).append("(").append(name).append("+").append(desc).append(")");
						} else {
							SpecialItemCfg cfg = SpecialItemCfgDAO.getDAO().getCfgById(String.valueOf(model));
							String name = cfg != null ? cfg.getName() : "";
							sbAttachAttr.append(model).append("(").append(name).append(")");
						}
						index++;
						if (index < split.length) {
							sbAttachAttr.append("&");
							sbAttachList.append("&");
						}
					}
				}
			}
			moreInfo.put("attachList", sbAttachList.toString());
			moreInfo.put("attachAttr", sbAttachAttr.toString());

			logUserId(eBILogType.Email, userId, moreInfo);
		} catch (Exception ex) {
			GameLog.error("LOGSENDER", userId, "logEmail" + ex.getMessage());
		}
	}

	public void logPayFinish(Player player, ChargeContentPojo chargeContentPojo, int vipBefore, ChargeCfg cfg, String entranceId) {
		GameBehaviorRecord record = (GameBehaviorRecord) DataEventRecorder.getParam();
		HashMap<String, String> moreInfo = null;
		if (record != null) {
			if (player.getUserId().equals(record.getUserId())) {
				moreInfo = record(record);
			} else {
				FSUtilLogger.error("logPayFinish wrong userId=" + player.getUserId() + ",recordUserId=" + record.getUserId());
			}
		}
		if (moreInfo == null) {
			moreInfo = new HashMap<String, String>();
		}
		moreInfo.put("CpTradeNo", chargeContentPojo.getCpTradeNo());
		moreInfo.put("payMoney", String.valueOf(chargeContentPojo.getMoney()));
		moreInfo.put("mainGoldGountAdd", String.valueOf(cfg.getGoldCount()));
		moreInfo.put("payEntrance", entranceId);
		moreInfo.put("vip", String.valueOf(vipBefore));
		moreInfo.put("payInfo", "next_vip_level:" + player.getVip() + "#");
		moreInfo.put("mainGoldGount", String.valueOf(player.getUserGameDataMgr().getChargeGold()));
		logPlayer(eBILogType.Pay, player, moreInfo);
		logPlayer(eBILogType.FinanceMainCoinAdd, player, moreInfo);
	}

	private void logPlayer(eBILogType logType, Player player, Map<String, String> moreInfo) {
		if (player.isRobot()) {
			return;
		}
		UserDataMgr userDataMgr = player.getUserDataMgr();
		ZoneRegInfo zoneRegInfo = userDataMgr.getZoneRegInfo();
		RoleGameInfo roleGameInfo = RoleGameInfo.fromPlayer(player, moreInfo);
		ZoneLoginInfo zoneLoginInfo = userDataMgr.getZoneLoginInfo();
		log(logType, zoneRegInfo, zoneLoginInfo, roleGameInfo, moreInfo);
	}

	/**
	 * 用于邮件日志，邮件日志不需要记录其他信息，只需要传进zoneid
	 * 
	 * @param logType
	 * @param userId
	 * @param moreInfo
	 */
	private void logUserId(eBILogType logType, String userId, Map<String, String> moreInfo) {
		ZoneLoginInfo zoneLoginInfo = new ZoneLoginInfo();
		zoneLoginInfo.setLoginZoneId(GameManager.getZoneId());
		log(logType, null, zoneLoginInfo, null, moreInfo);
	}

	private void log(final eBILogType logType, ZoneRegInfo zoneRegInfo, ZoneLoginInfo zoneLoginInfo, RoleGameInfo roleGameInfo, Map<String, String> moreInfo) {

		final BILogTemplate logTemplate = templateMap.get(logType);
		if (logTemplate != null) {
			long logTime = System.currentTimeMillis();
			if (moreInfo == null) {
				moreInfo = new HashMap<String, String>();
			}
			moreInfo.put("logTime", DateUtils.getDateTimeFormatString(logTime, "yyyy-MM-dd HH:mm:ss"));
			final String log = logTemplate.build(zoneRegInfo, zoneLoginInfo, roleGameInfo, moreInfo);

			GameWorldFactory.getGameWorld().asynExecute(new Runnable() {

				@Override
				public void run() {
					// biLog.info(logType + " " +
					// logTemplate.getTextTemplate());
					Logger logger = getLogger(logType);
					logger.info(logType + " " + log);
					LogService.getInstance().sendLog(log);
				}
			});
		}

	}

	public BILogTemplate getBILogTemplate(eBILogType type) {
		return templateMap.get(type);
	}

}
