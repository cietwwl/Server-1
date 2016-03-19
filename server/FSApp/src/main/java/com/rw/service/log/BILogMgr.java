package com.rw.service.log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.fsutil.util.DateUtils;
import com.rw.netty.ServerConfig;
import com.rw.netty.UserChannelMgr;
import com.rw.service.log.eLog.eBILogCopyEntrance;
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
import com.rw.service.log.template.BITaskType;
import com.rw.service.log.template.CoinChangedLogTemplate;
import com.rw.service.log.template.CopyBeginLogTemplate;
import com.rw.service.log.template.CopyEndLogTemplate;
import com.rw.service.log.template.ItemChangedEventType_1;
import com.rw.service.log.template.ItemChangedEventType_2;
import com.rw.service.log.template.ItemChangedLogTemplate;
import com.rw.service.log.template.OnlineCountLogTemplate;
import com.rw.service.log.template.RoleCreatedLogTemplate;
import com.rw.service.log.template.RoleLoginLogTemplate;
import com.rw.service.log.template.RoleLogoutLogTemplate;
import com.rw.service.log.template.RoleUpgradeLogTemplate;
import com.rw.service.log.template.TaskBeginLogTemplate;
import com.rw.service.log.template.TaskEndLogTemplate;
import com.rw.service.log.template.ZoneCountCoinLogTemplate;
import com.rw.service.log.template.ZoneCountLevelSpreadLogTemplate;
import com.rw.service.log.template.ZoneCountTotalAccountLogTemplate;
import com.rw.service.log.template.ZoneCountVipSpreadLogTemplate;
import com.rw.service.log.template.ZoneLoginLogTemplate;
import com.rw.service.log.template.ZoneLogoutLogTemplate;
import com.rw.service.log.template.ZoneRegLogTemplate;
import com.rwbase.dao.copypve.CopyType;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.gameworld.GameWorldFactory;

public class BILogMgr {
	
//	private ZoneRegInfo zoneRegInfo;
//	
//	private ZoneLoginInfo zoneLoginInfo;
//	
//	private RoleGameInfo roleGameInfo;
	
	private static Logger biLog = Logger.getLogger("biLog");
	
	private static BILogMgr instance = new BILogMgr();
	
	private Map<eBILogType, BILogTemplate> templateMap;
	
	public static BILogMgr getInstance(){
		return instance;
	}
	
	private BILogMgr(){
		
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
		
	}
	
	
	public void logZoneReg(Player player){
		logPlayer(eBILogType.ZoneReg, player, null);
		logPlayer(eBILogType.RoleCreated, player, null);
		logZoneLogin(player);
	}
	
	public void logZoneLogin(Player player){
		Map<String,String> moreInfo = new HashMap<String, String>();
		moreInfo.put("result", "1");
		logPlayer(eBILogType.ZoneLogin, player, moreInfo);
		logRoleLogin(player);
	}
	public void logZoneLogout(Player player){
		Map<String,String> moreInfo = new HashMap<String, String>();
		moreInfo.put("result", "1");
		logPlayer(eBILogType.ZoneLogout, player, moreInfo);
		logAccountLogout(player, moreInfo);
		logRoleLogout(player);
	}
	private void logAccountLogout(Player player, Map<String,String> moreInfo){
		logPlayer(eBILogType.AccountLogout, player, moreInfo);
	}
	private void logRoleLogin(Player player){
		logPlayer(eBILogType.RoleLogin, player, null);
	}
	private void logRoleLogout(Player player){
		logPlayer(eBILogType.RoleLogout, player, null);
	}
	
	public void logOnlineCount(String regSubChannelId, AtomicInteger onlineCount){
		
		Map<String,String> moreInfo = new HashMap<String, String>();
		moreInfo.put("onlineCount", ""+onlineCount.get());
		moreInfo.put("loginZoneId", ""+ServerConfig.getInstance().getZoneId());
		moreInfo.put("regSubChannelId", regSubChannelId);
		
		log(eBILogType.OnlineCount, null, null, null, moreInfo);
	}
	public void logZoneCountCoin(String regSubChannelId, long zoneCoinRemain, String clientPlatForm){
		
		Map<String,String> moreInfo = new HashMap<String, String>();
		
		moreInfo.put("threadId", ""+Thread.currentThread().getId());
		moreInfo.put("zoneCoinRemain", ""+zoneCoinRemain);
		moreInfo.put("loginZoneId", ""+ServerConfig.getInstance().getZoneId());
		moreInfo.put("regSubChannelId", regSubChannelId);
		moreInfo.put("loginClientPlatForm", clientPlatForm);
		
		log(eBILogType.ZoneCountCoin, null, null, null, moreInfo);
	}
	public void logZoneCountLevelSpread(String regSubChannelId, String level, long levelCount, String clientPlatForm){
		
		Map<String,String> moreInfo = new HashMap<String, String>();
		moreInfo.put("threadId", ""+Thread.currentThread().getId());
		moreInfo.put("level", level);
		moreInfo.put("levelCount", ""+levelCount);
		moreInfo.put("loginZoneId", ""+ServerConfig.getInstance().getZoneId());
		moreInfo.put("regSubChannelId", regSubChannelId);
		moreInfo.put("loginClientPlatForm", clientPlatForm);
		
		log(eBILogType.LevelSpread, null, null, null, moreInfo);
	}
	public void logZoneCountVipSpread(String regSubChannelId, String vipLevel, long count, String clientPlatForm){
		
		Map<String,String> moreInfo = new HashMap<String, String>();
		moreInfo.put("threadId", ""+Thread.currentThread().getId());
		moreInfo.put("totalAccount", ""+count);
		moreInfo.put("vip", ""+vipLevel);
		moreInfo.put("loginZoneId", ""+ServerConfig.getInstance().getZoneId());
		moreInfo.put("regSubChannelId", regSubChannelId);
		moreInfo.put("loginClientPlatForm", clientPlatForm);
		
		log(eBILogType.VipSpread, null, null, null, moreInfo);
	}
	public void logZoneCountTotalAccount(String regSubChannelId, long totalAccount, String clientPlatForm){
		
		Map<String,String> moreInfo = new HashMap<String, String>();
		moreInfo.put("threadId", ""+Thread.currentThread().getId());
		moreInfo.put("totalAccount", ""+totalAccount);
		moreInfo.put("loginZoneId", ""+ServerConfig.getInstance().getZoneId());
		moreInfo.put("regSubChannelId", regSubChannelId);
		moreInfo.put("loginClientPlatForm", clientPlatForm);
		
		log(eBILogType.TotalAccount, null, null, null, moreInfo);
	}
	
	public void doLogoutLog(){
		List<String> offLineUserIds = UserChannelMgr.extractLogoutUserIdList();
		if(offLineUserIds == null){
			return;
		}
		for (String uidTmp : offLineUserIds) {
			Player offLiner = PlayerMgr.getInstance().find(uidTmp);
			if(offLiner!=null){
				try {
					
					logZoneLogout(offLiner);
				} catch (Exception e) {
					GameLog.error(LogModule.BILOG.getName(), uidTmp, "player BILogMgr[doLogoutLog] error!", e);
				}
			}else{
				GameLog.error(LogModule.BILOG.getName(), uidTmp, "player not found!");
			}
		}
		
	}
	
	public void logActivityBegin(Player player, BIActivityEntry activityEntry, BIActivityCode activityCode){
		Map<String,String> moreInfo = new HashMap<String, String>();
		moreInfo.put("activityEntry", ""+activityEntry.getEntry());
		moreInfo.put("activityCode", ""+activityCode.getCode());
		moreInfo.put("result", "1");
		
		logPlayer(eBILogType.ActivityBegin, player, moreInfo);
	}
	public void logActivityEnd(Player player, BIActivityEntry activityEntry, BIActivityCode activityCode, int activityTime){
		Map<String,String> moreInfo = new HashMap<String, String>();
		moreInfo.put("activityEntry", ""+activityEntry.getEntry());
		moreInfo.put("activityCode", ""+activityCode.getCode());
		moreInfo.put("activityTime", ""+activityTime);
		moreInfo.put("result", "1");
		
		logPlayer(eBILogType.ActivityEnd, player, moreInfo);
	}
	/**
	 * 任务开始
	 * @param player
	 * @param taskId
	 * @param biTaskType
	 */
	public void logTaskBegin(Player player, Integer taskId, BITaskType biTaskType){
		Map<String,String> moreInfo = new HashMap<String, String>();
		moreInfo.put("taskId", taskId.toString());
		moreInfo.put("result", "1");
		moreInfo.put("optype", "task_start");
		moreInfo.put("biTaskType", ""+biTaskType.getTypeNo());
		
		logPlayer(eBILogType.TaskBegin, player, moreInfo);
	}
	/**
	 * 任务结束
	 * @param player
	 * @param taskId
	 * @param biTaskType
	 */
	public void logTaskEnd(Player player, Integer taskId, BITaskType biTaskType, boolean success){
		Map<String,String> moreInfo = new HashMap<String, String>();
		
		if(success){
			moreInfo.put("optype", "task_win");
		}else{
			moreInfo.put("optype", "task_fail");
			
		}
		moreInfo.put("taskId", taskId.toString());
		moreInfo.put("result", "1");
		moreInfo.put("biTaskType", ""+biTaskType.getTypeNo());
		
		logPlayer(eBILogType.TaskEnd, player, moreInfo);
	}
	
	public void logCopyBegin(Player player, Integer copyId, int copyLevel, boolean isFirst,eBILogCopyEntrance entranceType){
		Map<String,String> moreInfo = new HashMap<String, String>();
		moreInfo.put("copyId", copyId.toString());
		moreInfo.put("result", "1");
		moreInfo.put("copyLevel", getLogCopyLevel(copyLevel));
		if(isFirst){
			moreInfo.put("copyStatus", "1");
		}else {
			moreInfo.put("copyStatus", "2");
		}
		moreInfo.put("copyEntrance",entranceType.name());
		
		logPlayer(eBILogType.CopyBegin, player, moreInfo);
	}
	
	/**
	 * 
	 * @param player
	 * @param copyId
	 * @param isFirst 是否首次
	 */
	public void logCopyEnd(Player player, Integer copyId, int copyLevel, boolean isFirst, boolean isWin, int fightTime){
		Map<String,String> moreInfo = new HashMap<String, String>();
		moreInfo.put("copyId", copyId.toString());
		moreInfo.put("result", "1");
		moreInfo.put("copyLevel", getLogCopyLevel(copyLevel));
		moreInfo.put("fightTime", ""+fightTime);
		if(isFirst){
			moreInfo.put("copyStatus", "1");
		}else {
			moreInfo.put("copyStatus", "2");
		}
		
		if(isWin){
			moreInfo.put("operationCode", "case_win");
		}else{
			moreInfo.put("operationCode", "case_fail");
		}
		
		logPlayer(eBILogType.CopyEnd, player, moreInfo);
	}
	
	private String getLogCopyLevel(int levelType){
		Integer logLevelType = 0;
		
		if( levelType == CopyType.COPY_TYPE_NORMAL ) {
			logLevelType = 1;
		}else if (levelType == CopyType.COPY_TYPE_ELITE) {
			logLevelType = 2;
		}

		return logLevelType.toString();
	}
	/**
	 * 
	 * @param player
	 * @param copyId
	 * 扫荡
	 */
	public void logSweep(Player player, Integer copyId, int copyLevel){
		Map<String,String> moreInfo = new HashMap<String, String>();
		moreInfo.put("copyId", copyId.toString());
		moreInfo.put("result", "1");		
		moreInfo.put("copyStatus", "3");
		moreInfo.put("copyLevel", getLogCopyLevel(copyLevel));
		moreInfo.put("operationCode", "case_win");
		moreInfo.put("fightTime", "0");
		
		logPlayer(eBILogType.CopyBegin, player, moreInfo);
		logPlayer(eBILogType.CopyEnd, player, moreInfo);
	}
	public void logItemChanged(Player player, String scenceId, ItemChangedEventType_1 type_1, ItemChangedEventType_2 type_2, List<ItemData> itemList_incr, List<ItemData> itemList_decr){
		Map<String,String> moreInfo = new HashMap<String, String>();
		moreInfo.put("scenceId", scenceId);
		moreInfo.put("ItemChangedEventType_1", type_1.name());		
		moreInfo.put("ItemChangedEventType_2", type_2.name());		
		moreInfo.put("itemList_incr", getItemListLog(itemList_incr));
		moreInfo.put("itemList_decr", getItemListLog(itemList_decr));
		
		logPlayer(eBILogType.ItemChanged, player, moreInfo);
	}
	public void logCoinChanged(Player player, String scenceId, ItemChangedEventType_1 type_1, ItemChangedEventType_2 type_2, int coinChanged,int coinRemain){
		Map<String,String> moreInfo = new HashMap<String, String>();
		moreInfo.put("scenceId", scenceId);
		moreInfo.put("ItemChangedEventType_1", type_1.name());		
		moreInfo.put("ItemChangedEventType_2", type_2.name());		
		moreInfo.put("coinChanged", String.valueOf(coinChanged));
		moreInfo.put("coinRemain", String.valueOf(coinRemain));
		
		logPlayer(eBILogType.CoinChanged, player, moreInfo);
	}
	
	public void logRoleUpgrade(Player player){
		logPlayer(eBILogType.RoleUpgrade, player, null);
	}
	
	private String getItemListLog(List<ItemData> itemList){
		if(itemList==null){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (ItemData itemData : itemList) {
			sb.append(itemData.getModelId())
				.append(":")
				.append(itemData.getCount())
				.append("&");
		}
		return StringUtils.substringBeforeLast(sb.toString(), "&");
	}
	
	
	private void logPlayer(eBILogType logType, Player player, Map<String,String> moreInfo ){
		if(player.isRobot()){
			return;
		}
		ZoneRegInfo zoneRegInfo = player.getUserDataMgr().getZoneRegInfo();
		RoleGameInfo roleGameInfo = RoleGameInfo.fromPlayer(player);
		ZoneLoginInfo zoneLoginInfo = player.getZoneLoginInfo();
		log(logType, zoneRegInfo, zoneLoginInfo, roleGameInfo, moreInfo);
		
	}

	
	private void log(final eBILogType logType, ZoneRegInfo zoneRegInfo, ZoneLoginInfo zoneLoginInfo, RoleGameInfo roleGameInfo, Map<String,String> moreInfo){
		
		final BILogTemplate logTemplate = templateMap.get(logType);
		if(logTemplate!=null){
			long logTime = System.currentTimeMillis();
			if(moreInfo == null){
				moreInfo = new HashMap<String, String>();
			}
			moreInfo.put("logTime", DateUtils.getDateTimeFormatString(logTime, "yyyy-MM-dd HH:mm:ss"));
			final String log = logTemplate.build(zoneRegInfo, zoneLoginInfo, roleGameInfo, moreInfo);
			
			GameWorldFactory.getGameWorld().asynExecute(new Runnable() {
				
				@Override
				public void run() {
					biLog.info(logType+" "+logTemplate.getTextTemplate());
					biLog.info(logType+" "+log);
					LogService.getInstance().sendLog(log);
					
				}
				
			});
		}
		
		
		
	}
	
}
