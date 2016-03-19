package com.gm.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bm.serverStatus.ServerStatusMgr;
import com.common.playerFilter.PlayerFilterCondition;
import com.gm.GmExecutor;
import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.serverData.ServerDataHolder;
import com.rwbase.dao.serverData.ServerGmEmail;

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

			String conditionListJson = GmUtils.parseString(args,
					"conditionList");
			final List<PlayerFilterCondition> conditionList = GmEmailHelper
					.parseCondition(conditionListJson);

			ServerGmEmail gmEmail = new ServerGmEmail();
			gmEmail.setSendToAllEmailData(emailData);
			gmEmail.setConditionList(conditionList);
			ServerStatusMgr.addGmMail(gmEmail);

			List<String> whiteList = ServerStatusMgr.getWhiteList();
			final List<Player> playerList = new ArrayList<Player>();
			for (String userIdTmp : whiteList) {
				Player targetTmp = PlayerMgr.getInstance().find(userIdTmp);
				if (targetTmp != null) {
					playerList.add(targetTmp);
				}
			}

			GmExecutor.getInstance().submit(new Runnable() {

				@Override
				public void run() {
					try {
						PlayerMgr.getInstance().sendEmailToList(playerList,
								emailData, conditionList);
					} catch (Throwable e) {
						GameLog.error(LogModule.GM.getName(),
								"GmEmailWhiteList",
								"GmEmailWhiteList[doTask] GmExecutor run", e);
					}
				}
			});

			response.addResult(resultMap);
		} catch (Exception ex) {
			SocketHelper.processException(ex, response);
		}
		return response;
	}

}
