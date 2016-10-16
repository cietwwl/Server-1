package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.email.EmailData;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;

public class GmEmailSingleSend implements IGmTask {

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
			if(!checkAttachItemIegal){
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_INVALID_ATTACHMENT.getStatus()));
			}

			String roleId = GmUtils.parseString(args, "roleId");
			Player targetPlayer = PlayerMgr.getInstance().find(roleId);
			if (targetPlayer != null) {
				final String userId = targetPlayer.getUserId();
				GameWorldFactory.getGameWorld().asyncExecute(userId, new PlayerTask() {
					
					@Override
					public void run(Player e) {
						// TODO Auto-generated method stub
						EmailUtils.sendEmail(userId, emailData);
					}
				});
				
			} else {
				GameLog.info(LogModule.GM.getName(), "GmEmailSingleSend",
						"GmEmailSingleSend[doTask] 没有找到用户 userId:" + roleId,
						null);
			}

			response.addResult(resultMap);
		} catch (Exception ex) {
			SocketHelper.processException(ex, response);
		}
		return response;
	}

}
