package com.gm.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bm.serverStatus.ServerStatusMgr;
import com.common.playerFilter.FilterType;
import com.common.playerFilter.PlayerFilter;
import com.common.playerFilter.PlayerFilterCondition;
import com.gm.GmExecutor;
import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.gm.gmEmail.GMEmail;
import com.gm.gmEmail.GMEmailDataDao;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.netty.UserChannelMgr;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.serverData.ServerGmEmail;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;

public class GmEmailAll implements IGmTask {

	public final static int STATUS_ORIGINAL = 0;
	public final static int STATUS_SEND = 1;
	public final static int STATUS_CLOSE = 2;
	public final static int STATUS_DELETE = 3;

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		try {
			long taskId = GmUtils.parseLong(request.getArgs(), "taskId");
			int status = GmUtils.parseInt(request.getArgs(), "status");
			boolean blnUpdate = false;
			ServerGmEmail gmMail = ServerStatusMgr.getGmMail(taskId);
			if (gmMail == null) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_NOT_FIND_GMMAIL.getStatus()));
			}
			if (status == STATUS_CLOSE) {
				gmMail.setStatus(STATUS_CLOSE);
				blnUpdate = true;
			}
			if (status == STATUS_SEND) {
				gmMail.setStatus(STATUS_SEND);
				sendMail(gmMail);
				blnUpdate = true;
			}
			if (status == STATUS_DELETE) {
				deleteMail(gmMail);
				blnUpdate = true;
			}
			if (blnUpdate) {
				ServerStatusMgr.updateGmMail(gmMail);
			}

			response.setStatus(0);
			response.setCount(1);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			response.addResult(resultMap);
		} catch (Exception ex) {
			SocketHelper.processException(ex, response);
		}
		return response;
	}

	private void deleteMail(ServerGmEmail gmMail) {
		final EmailData emailData = gmMail.getSendToAllEmailData();
		GmExecutor.getInstance().submit(new Runnable() {

			@Override
			public void run() {
				try {
					PlayerMgr.getInstance().callbackEmail(emailData);
				} catch (Throwable e) {
					GameLog.error(LogModule.GM.getName(), "GmEmailAll", "GmEmailAll[doTask] GmExecutor run", e);
				}
			}
		});
		gmMail.setStatus(STATUS_DELETE);
	}

	private void sendMail(ServerGmEmail gmMail) throws Exception {
		if (gmMail.getStatus() == STATUS_CLOSE || gmMail.getStatus() == STATUS_DELETE) {
			throw new Exception(String.valueOf(GmResultStatusCode.STATUS_GMMAIL_CLOSE.getStatus()));
		}
		final EmailData emailData = gmMail.getSendToAllEmailData();
		final List<PlayerFilterCondition> conditionList = gmMail.getConditionList();
		GmExecutor.getInstance().submit(new Runnable() {

			public void run() {
				List<String> onlinePlayers = UserChannelMgr.getOnlineList();
				for (String userId : onlinePlayers) {
					GameWorldFactory.getGameWorld().asyncExecute(userId, new PlayerTask() {

						@Override
						public void run(Player player) {
							boolean filted = false;
							for (PlayerFilterCondition conTmp : conditionList) {
								if (!PlayerFilter.isInRange(player, conTmp)) {
									filted = true;
									break;
								}
							}
							long taskId = emailData.getTaskId();
							GMEmail gmEmail = GMEmailDataDao.getInstance().getGMEmail(player.getUserId());
							List<Long> taskIdList = gmEmail.getTaskIdList();
							if (!filted && !player.getEmailMgr().containsEmailWithTaskId(taskId) && !taskIdList.contains(taskId)) {
								boolean sendEmail = EmailUtils.sendEmail(player.getUserId(), emailData);
								if (sendEmail) {
									GMEmailDataDao.getInstance().updateGmEmailStatus(player.getUserId(), emailData.getTaskId());
								}
							}
						}
					});
				}
			}
		});

		for (PlayerFilterCondition condition : conditionList) {
			if (condition.getType() == FilterType.CREATE_TIME.getValue()) {
				long endTime = condition.getMaxValue() * 1000;
				gmMail.setStatus(STATUS_SEND);
			}
		}

		int expireTime = emailData.getExpireTime();
		int delayTime = emailData.getDelayTime() * 1000;
		long sendTime = emailData.getSendTime();
		if (expireTime == 0 || (sendTime + delayTime) > System.currentTimeMillis()) {
			gmMail.setStatus(STATUS_SEND);
		} else {
			gmMail.setStatus(STATUS_CLOSE);
		}
	}
}
