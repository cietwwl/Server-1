package com.gm.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bm.login.AccoutBM;
import com.bm.serverStatus.ServerStatusMgr;
import com.common.playerFilter.PlayerFilter;
import com.common.playerFilter.PlayerFilterCondition;
import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.gm.gmEmail.GMEmailDataDao;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.fsutil.log.GmLog;
import com.rw.manager.GameManager;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.serverData.ServerGmEmail;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwbase.dao.user.accountInfo.TableAccount;
import com.rwbase.dao.user.platformwhitelist.PlatformWhiteList;
import com.rwbase.dao.user.platformwhitelist.PlatformWhiteListDataHolder;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;

public class GmEmailWhiteList implements IGmTask {

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		try {
			response.setStatus(0);
			response.setCount(1);
			Map<String, Object> resultMap = new HashMap<String, Object>();

			Map<String, Object> args = request.getArgs();
			final EmailData emailData = GmEmailHelper.getEmailData(args);

			String emailAttachment = emailData.getEmailAttachment();
			boolean checkAttachItemIegal = GmUtils.checkAttachItemIegal(emailAttachment);
			if (!checkAttachItemIegal) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_INVALID_ATTACHMENT.getStatus()));
			}

			String conditionListJson = GmUtils.parseString(args,
					"conditionList");
			final List<PlayerFilterCondition> conditionList = GmEmailHelper
					.parseCondition(conditionListJson);

			ServerGmEmail gmEmail = new ServerGmEmail();
			gmEmail.setSendToAllEmailData(emailData);
			gmEmail.setConditionList(conditionList);
			ServerStatusMgr.addGmMail(gmEmail);

			List<PlatformWhiteList> allWhiteList = PlatformWhiteListDataHolder.getInstance().getAllWhiteList();
			
			final List<Player> playerList = new ArrayList<Player>();
			for (PlatformWhiteList tablePlatformWhiteList : allWhiteList) {
				String openAccount = tablePlatformWhiteList.getAccountId();
				boolean close = tablePlatformWhiteList.isClose();
				try {
					TableAccount account = AccoutBM.getInstance().getByOpenAccount(openAccount);
					User user = UserDataDao.getInstance().getByAccoutAndZoneId(account.getAccountId(), GameManager.getZoneId());
					Player targetTmp = PlayerMgr.getInstance().find(user.getUserId());
					if (targetTmp != null) {
						playerList.add(targetTmp);
					}
				} catch (Exception ex) {
					GmLog.info("send gm mail fail:" + openAccount);
					continue;
				}
			}

			for (Player player : playerList) {
				GameWorldFactory.getGameWorld().asyncExecute(player.getUserId(), new PlayerTask() {

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
						if (!filted && !player.getEmailMgr().containsEmailWithTaskId(taskId)) {
							boolean sendEmail = EmailUtils.sendEmail(player.getUserId(), emailData);
							if(sendEmail){
								GMEmailDataDao.getInstance().updateGmEmailStatus(player.getUserId(), emailData.getTaskId());
							}
						}
					}
				});
			}

			response.addResult(resultMap);
		} catch (Exception ex) {
			SocketHelper.processException(ex, response);
		}
		return response;
	}

}
