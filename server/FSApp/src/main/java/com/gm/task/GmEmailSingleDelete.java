package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.SocketHelper;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.EmailMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.email.EmailItem;

public class GmEmailSingleDelete implements IGmTask {

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		try {
			response.setStatus(0);
			response.setCount(1);
			Map<String, Object> resultMap = new HashMap<String, Object>();

			Map<String, Object> args = request.getArgs();
			final EmailData emailData = GmEmailHelper.getEmailData(args);

			String roleId = (String) args.get("roleId");
			String mailId = (String) args.get("mailId");
			Player targetPlayer = PlayerMgr.getInstance().find(roleId);
			if (targetPlayer != null) {
				EmailMgr emailMgr = targetPlayer.getEmailMgr();
				EmailItem emailItem = emailMgr.getEmailItem(mailId);
				if (emailItem != null) {
					emailMgr.delEmail(mailId);
				} else {
					GameLog.info(LogModule.GM.getName(), "GmEmailSingleDelete",
							"GmEmailSingleDelete[doTask] 邮件不存在， userId:"
									+ roleId + " mailId" + mailId, null);
				}
			} else {
				GameLog.info(LogModule.GM.getName(), "GmEmailSingleDelete",
						"GmEmailSingleDelete[doTask] 没有找到用户 userId:" + roleId,
						null);
			}

			response.addResult(resultMap);
		} catch (Exception ex) {
			SocketHelper.processException(ex, response);
		}
		return response;
	}

}
