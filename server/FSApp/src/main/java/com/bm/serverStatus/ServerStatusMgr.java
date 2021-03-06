package com.bm.serverStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.common.playerFilter.PlayerFilter;
import com.common.playerFilter.PlayerFilterCondition;
import com.gm.gmEmail.GMEmail;
import com.gm.gmEmail.GMEmailDataDao;
import com.gm.task.GmEmailAll;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.manager.GameManager;
import com.rw.service.Email.EmailUtils;
import com.rw.service.role.MainMsgHandler;
import com.rwbase.common.timer.core.FSGameTimerMgr;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.serverData.GmNoticeInfo;
import com.rwbase.dao.serverData.ServerDataHolder;
import com.rwbase.dao.serverData.ServerGmEmail;
import com.rwbase.dao.serverData.ServerGmEmailDao;
import com.rwbase.dao.serverData.ServerGmEmailHolder;
import com.rwbase.dao.serverData.ServerGmNotice;
import com.rwbase.dao.serverData.ServerGmNoticeHolder;


public class ServerStatusMgr {

	private static ServerStatus status = ServerStatus.CLOSE;
	
	//是否开启白名单  默认开启白名单
	private static boolean whiteListOn = true;
	
	private static ServerDataHolder dataHolder = new ServerDataHolder();
	private static ServerGmEmailHolder mailHolder = new ServerGmEmailHolder();
	private static ServerGmNoticeHolder gmNoticeHolder = new ServerGmNoticeHolder();
	
	private static AtomicLong iSequenceNum = new AtomicLong(0);

	public static void init(){
		gmNoticeHolder.initGmNotices();
		FSGameTimerMgr.getInstance().submitMinuteTask(new ProcessGmNoticeTimerTask(), 1);
	}
	
	public static ServerStatus getStatus() {
		return status;
	}

	public static void setStatus(ServerStatus statusP) {
		status = statusP;
	} 
	
	public static int getOnlineLimit(){
		return dataHolder.getOnlineLimit();
	}
	
	public static void setOnlineLimit(int limit){
		dataHolder.setOnlineLimit(limit);
	}
	
	public static  List<String> getWhiteList(){
		return dataHolder.getWhiteList();
	}
	
	public static void addWhite(String userId){
		dataHolder.addWhite(userId);
	}
	public static void removeWhite(String userId){
		dataHolder.removeWhite(userId);
	}
	
	public static void switchWhiteList(boolean isOn){
		whiteListOn = isOn;
	}
	
	public static boolean isWhilteListON(){
		return whiteListOn;
	}
	
	public static void setChargeOn(boolean chargeOn){
		dataHolder.setChargeOn(chargeOn);
	}
	
	public static void setLastBIStatLogTime(long lastBIStatLogTime){
		dataHolder.setLastBIStatLogTime(lastBIStatLogTime);
	}
	
	public static long getLastBIStatLogTime(){
		return dataHolder.getLastBIStatLogTime();
	}
	
	public static boolean isChargeOn(){
		return dataHolder.isChargeOn();
	}
	
	public static void addGmMail(ServerGmEmail gmEmail){
		mailHolder.addGmMail(gmEmail);
	}
	
	public static ServerGmEmail getGmMail(long taskId){
		return mailHolder.getGmMailByTaskId(taskId);
	}
	
	public static void updateGmMail(ServerGmEmail gmEmail){
		mailHolder.updateGmMail(gmEmail);
	}
	
	public static List<ServerGmEmail> getGmMails(){
		return mailHolder.getGmMailList();
	}
	
	public static long getTaskId(){
		return dataHolder.getTaskId();
	}
	
	public static void setTaskId(long taskId){
		dataHolder.setTaskId(taskId);
	}
	
	public static void processGmMailWhenLogin(Player player){
		List<ServerGmEmail> gmMailList = ServerGmEmailDao.getInstance().getAllMails();
		long currentTimeMillis = System.currentTimeMillis();
		for (ServerGmEmail serverGmEmail : gmMailList) {
			int status = serverGmEmail.getStatus();
			if (status == GmEmailAll.STATUS_CLOSE || status == GmEmailAll.STATUS_ORIGINAL) {
				continue;
			}
			List<PlayerFilterCondition> conditionList = serverGmEmail.getConditionList();
			EmailData emailData = serverGmEmail.getSendToAllEmailData();
			int expireTime = emailData.getExpireTime();
			int delayTime = emailData.getDelayTime() * 1000;
			long sendTime = emailData.getSendTime();
			long dis;
			if (expireTime == 0) {
				dis = delayTime;

			} else {
				dis = TimeUnit.DAYS.toMillis(expireTime);
			}
			if ((sendTime + dis) <= currentTimeMillis) {
				serverGmEmail.setStatus(GmEmailAll.STATUS_CLOSE);
				ServerStatusMgr.updateGmMail(serverGmEmail);
				continue;
			}

			boolean filted = false;
			for (PlayerFilterCondition conTmp : conditionList) {
				if (!PlayerFilter.isInRange(player, conTmp)) {
					filted = true;
					break;
				}
			}

			if (filted) {
				continue;
			}

			long taskId = emailData.getTaskId();
			if (status == GmEmailAll.STATUS_DELETE) {
				if (player.getEmailMgr().containsEmailWithTaskId(taskId)) {

					List<Player> temp = new ArrayList<Player>();
					temp.add(player);
					PlayerMgr.getInstance().callbackEmailToList(temp, emailData);
				}
			} else {
				String userId = player.getUserId();
				GMEmail gmEmail = GMEmailDataDao.getInstance().getGMEmail(userId);
				List<Long> taskIdList = gmEmail.getTaskIdList();
				if (!taskIdList.contains(taskId)) {
					boolean sendEmail = EmailUtils.sendEmail(userId, emailData);
					if (sendEmail) {
						GMEmailDataDao.getInstance().updateGmEmailStatus(userId, taskId);
					}
				}
			}
		}
	}

	public static long getiSequenceNum() {
		int intServerId = GameManager.getZoneId();
		return intServerId * 100000000 + iSequenceNum.getAndIncrement();
	}
	
	public static List<ServerGmNotice> getAllGmNotice(){
		return gmNoticeHolder.GetGmNotices();
	}
	
	public static void removeGmNotice(int serverGmNoticeId){
		gmNoticeHolder.removeGmNotice(serverGmNoticeId);
	}
	
	public static void editGmNotice(ServerGmNotice gmNotice, boolean insert){
		gmNoticeHolder.editGmNotice(gmNotice, insert);
	}
	
	public static ServerGmNotice getGmNotice(int serverGmNoticeId){
		return gmNoticeHolder.getGmNoticeById(serverGmNoticeId);
	}
	
	/**
	 * 分时效处理广播
	 */
	public static void processGmNotice() {
		List<ServerGmNotice> allGmNotice = getAllGmNotice();
		long currentTime = System.currentTimeMillis();
		for (ServerGmNotice serverGmNotice : allGmNotice) {
			GmNoticeInfo noticeInfo = serverGmNotice.getNoticeInfo();
			long startTime = noticeInfo.getStartTime();
			long endTime = noticeInfo.getEndTime();
			long cycleInterval = noticeInfo.getCycleInterval() * 60 * 1000;
			long broadcastTime = noticeInfo.getLastBroadcastTime();
			if (currentTime >= startTime && currentTime <= endTime) {
				if (cycleInterval < (currentTime - broadcastTime)) {
					MainMsgHandler.getInstance().sendPmdNotId(noticeInfo.getTitle() + ":" + noticeInfo.getContent());
					noticeInfo.setLastBroadcastTime(currentTime);
				}
			}
		}
	}
}
